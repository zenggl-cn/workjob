package com.pak.ai.work.manage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pak.ai.db.ConnectionPool;
import com.pak.ai.work.entity.proc_param;
import com.pak.ai.work.entity.work_task;

public class task_manage {
	private final static Logger log=LoggerFactory.getLogger(task_manage.class);
	public ArrayList<work_task> getAllTask(){
		String sql="select task_id, task_name, this_run_date, finish_date, isvalid, run_state, task_type, "
				+ "command_str, command_param, hostmachine, dbase, session_id, retry, is_continue, remark from t_s_task";
		ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = null;
        PreparedStatement stmt= null;
        ResultSet rs = null;
        con=pool.getConnection();
        ArrayList<work_task> tasklist=new ArrayList<work_task>();
        try {
			stmt=con.prepareStatement(sql);
			rs=stmt.executeQuery();
			while(rs.next()){
				work_task wt=new work_task();
				wt.setTask_id(rs.getInt("task_id"));
				wt.setTask_name(rs.getString("task_name"));
				wt.setThis_run_date(rs.getDate("this_run_date"));
				wt.setFinish_date(rs.getDate("finish_date"));
				wt.setIsvalid(rs.getInt("isvalid"));
				wt.setRun_state(rs.getInt("run_state"));
				wt.setCommand(rs.getString("command_str"));
				wt.setCommand_param(rs.getString("command_param"));
				wt.setHostmachine(rs.getString("hostmachine"));
				wt.setDbase(rs.getInt("dbase"));
				wt.setSession_id(rs.getString("session_id"));
				wt.setRetry(rs.getInt("retry"));
				wt.setIs_continue(rs.getInt("is_continue"));
				wt.setRemark(rs.getString("remark"));
				tasklist.add(wt);
			}
		} catch (SQLException e) {
			log.error(e.getMessage());
		}finally{
			try {
				con.close();
			} catch (SQLException e) {
				log.error(e.getMessage());
			}
		}
        
		return tasklist;
	}
	
	public boolean uptask(work_task wt){
		ArrayList<work_task> al=new ArrayList<work_task>();
		al.add(wt);
		return uptask(al);
	}
	
	public boolean uptask(ArrayList<work_task> al){
		ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = null;
        PreparedStatement stm= null;
        con=pool.getConnection();
        try {
			con.setAutoCommit(false);
		} catch (SQLException e1) {
			log.error(e1.getMessage());
		}
        int v_num=0;
        for(work_task wt:al){
        	String sql="update tj.t_s_task set task_name=?, this_run_date=?, finish_date=?, isvalid=?, run_state=?, task_type=?, "
    				+ "command_str=?, command_param=?, hostmachine=?, dbase=?, session_id=?, retry=?, is_continue=?, remark=?"
    				+ "where task_id=?";
        	try {
				stm=con.prepareStatement(sql);
				stm.setString(1, wt.getTask_name());
				stm.setTimestamp(2, wt.getThis_run_date()==null?null:new Timestamp(wt.getThis_run_date().getTime()));
				stm.setTimestamp(3, wt.getFinish_date()==null?null:new Timestamp(wt.getFinish_date().getTime()));
				stm.setInt(4, wt.getIsvalid());
				stm.setInt(5, wt.getRun_state());
				stm.setInt(6, wt.getTask_type());
				stm.setString(7, wt.getCommand());
				stm.setString(8, wt.getCommand_param());
				stm.setString(9,wt.getHostmachine());
				stm.setInt(10, wt.getDbase());
				stm.setString(11, wt.getSession_id());
				stm.setInt(12, wt.getRetry());
				stm.setInt(13, wt.getIs_continue());
				stm.setString(14, wt.getRemark());
				stm.setInt(15, wt.getTask_id());
				v_num=v_num+stm.executeUpdate();
			} catch (SQLException e) {
				try {
					log.error("task error:"+al.toString());
					con.rollback();
					break;
				} catch (SQLException e1) {
					log.error(e.getMessage());
				}
				log.error(e.getMessage());
			}
        }
        try {
			if(v_num==al.size()){
				con.commit();
			}else{
				con.rollback();
			}
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
        
		return false;
	}
	
	public ArrayList<proc_param> getproc_param(work_task wt){
		ArrayList<proc_param> al=new ArrayList<proc_param>();
		if(wt.getTask_type()!=0 && wt.getCommand_param()!=null && wt.getCommand_param()!=""){
			return al;
		}
		
		proc_param pp=new proc_param();
		String owner="";
		String proc_name="";
		if(wt.getCommand().indexOf(".")>0){
			owner=wt.getCommand().substring(0,wt.getCommand().indexOf(".")).toUpperCase();
			proc_name=wt.getCommand().substring(wt.getCommand().indexOf(".")+1).toUpperCase();
		}else{
			proc_name=wt.getCommand().toUpperCase();
		}
		
		StringBuffer sqlbf=new StringBuffer();
		if(owner!=""){
			sqlbf.append("SELECT text FROM ALL_SOURCE  where TYPE='PROCEDURE' ");
			sqlbf.append(" and owner='"+owner+"' and name='"+proc_name+"'");
		}else{
			sqlbf.append("select text from user_source where TYPE='PROCEDURE' ");
			sqlbf.append(" and name='"+proc_name+"'");
		}
		sqlbf.append(" order by line");
		ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = null;
        PreparedStatement stm= null;
        ResultSet rs=null;
        con=pool.getConnection(wt.getDbase());
        boolean isparam=false;
        try {
			stm=con.prepareStatement(sqlbf.toString());
			rs=stm.executeQuery();
			while(rs.next()){
				String text=rs.getString(1);
				if(text.indexOf("(")>=0){
					isparam=true;
					continue;
				}
				if(text.indexOf(")")>=0){
					isparam=false;
					break;
				}
				if(isparam){
					text=text.substring(0,text.indexOf(",")==-1?text.length()-1:text.indexOf(",") ).trim();
					pp=new proc_param();
					pp.setParam_name(text.substring(0,text.indexOf(" ")).toUpperCase() );
					text=text.substring(text.indexOf(" ")).trim();
					pp.setOp_type(text.substring(0,text.indexOf(" ")).toUpperCase());
					text=text.substring(text.indexOf(" ")).trim();
					if(text.indexOf(" ")==-1){
						pp.setParam_type(text.substring(0).toUpperCase());
					}else{
						pp.setParam_type(text.substring(0,text.indexOf(" ")).toUpperCase());
					}
					al.add(pp);
				}
			}
		} catch (SQLException e) {
			log.error(e.getMessage());
		}finally{
			try {
				rs.close();
				stm.close();
				con.close();
			} catch (SQLException e) {
				log.error(e.getMessage());
			}
		}
		return al;
	}
}
