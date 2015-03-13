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
import com.pak.ai.work.entity.work_entity;

public class Work_manage {
	private final static Logger logger=LoggerFactory.getLogger(Work_manage.class);
	public ArrayList<work_entity> getAllWork(){
		ArrayList<work_entity> worklist=new ArrayList<work_entity>();
		ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = null;
        PreparedStatement stmt= null;
        ResultSet rs = null;
        con=pool.getConnection();
        String sql="select work_id, work_name, run_state, isvalid, init_start_date, expire_date, last_run_date, this_run_date, "
        		+ "next_run_date, run_at_load, cron_expression, remark from tj.t_s_work";
        try {
			stmt=con.prepareStatement(sql);
			rs=stmt.executeQuery();
			while(rs.next()){
				work_entity wenty=new work_entity();
				wenty.setWork_id(rs.getInt("work_id"));
				wenty.setWork_name(rs.getString("work_name"));
				wenty.setRun_state(rs.getInt("run_state"));
				wenty.setIsvalid(rs.getInt("isvalid"));
				wenty.setInit_start_date(rs.getDate("init_start_date"));
				wenty.setExpire_date(rs.getDate("expire_date"));
				wenty.setLast_run_date(rs.getDate("last_run_date"));
				wenty.setThis_run_date(rs.getDate("this_run_date"));
				wenty.setNext_run_date(rs.getDate("next_run_date"));
				wenty.setRun_at_load(rs.getInt("run_at_load"));
				wenty.setCron_expression(rs.getString("cron_expression"));
				wenty.setRemark(rs.getString("remark"));
				worklist.add(wenty);
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}finally{
			try {
				con.close();
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
		}
		return worklist;
	}
	
	public boolean upwork(work_entity we){
		ArrayList<work_entity> al=new ArrayList<work_entity>();
		al.add(we);
		return upwork(al);
	}
	public boolean upwork(ArrayList<work_entity> worklist){
		ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = null;
        PreparedStatement stmt= null;
        con=pool.getConnection();
        try {
			con.setAutoCommit(false);
		} catch (SQLException e1) {
			logger.error(e1.getMessage());
		}
        int v_num=0;
		for (work_entity we : worklist) {
			if( 0==we.getWork_id() ){
				continue;
			}
			String upsql="update tj.t_s_work a set work_name=?, run_state=?, isvalid=?, init_start_date=?, "
					+ "expire_date=?, last_run_date=?, this_run_date=?, next_run_date=?, run_cycle=?, run_at_load=?, "
					+ "cron_expression=?, remark=?,cycle_frequence=? "
					+ "where a.work_id=?";
			try {
				stmt=con.prepareStatement(upsql);
				stmt.setString(1, we.getWork_name());
				stmt.setInt(2, we.getRun_state());
				stmt.setInt(3, we.getIsvalid());
				stmt.setTimestamp(4, we.getInit_start_date()==null?null:new Timestamp(we.getInit_start_date().getTime()));
				stmt.setTimestamp(5, we.getExpire_date()==null?null:new Timestamp(we.getExpire_date().getTime()));
				stmt.setTimestamp(6, we.getLast_run_date()==null?null:new Timestamp(we.getLast_run_date().getTime()));
				stmt.setTimestamp(7, we.getThis_run_date()==null?null:new Timestamp(we.getThis_run_date().getTime()));
				stmt.setTimestamp(8, we.getNext_run_date()==null?null:new Timestamp(we.getNext_run_date().getTime()));
				stmt.setInt(10, we.getRun_at_load());
				stmt.setString(11, we.getCron_expression());
				stmt.setString(12, we.getRemark());
				stmt.setInt(14, we.getWork_id());
				v_num=v_num+stmt.executeUpdate();
			} catch (SQLException e) {
				try {
					con.rollback();
				} catch (SQLException e1) {
					logger.error(e1.getMessage());
				}
				logger.error(e.getMessage());
			}
			
		}
		try {
			if (v_num==worklist.size()){
				con.commit();
				con.close();
				return true;
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		
				
		return false;
	}
}
