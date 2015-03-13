package com.pak.ai.work.manage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pak.ai.db.ConnectionPool;
import com.pak.ai.work.entity.task_route;

public class Route_manage {
	private final static Logger log=LoggerFactory.getLogger(Route_manage.class);
	public ArrayList<task_route> getAllRoute(){
		ArrayList<task_route> routelist=new ArrayList<task_route>();
		ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = null;
        PreparedStatement stm= null;
        ResultSet rs = null;
        con=pool.getConnection();
        String sql="select work_id,pre_task_id,next_task_id,is_wait,remark from tj.t_s_taskroute";
        try {
			stm=con.prepareStatement(sql);
			rs=stm.executeQuery();
			while(rs.next()){
				task_route tr=new task_route();
				tr.setWork_id(rs.getInt("work_id"));
				tr.setPre_task_id(rs.getInt("pre_task_id"));
				tr.setNext_task_id(rs.getInt("next_task_id"));
				tr.setIs_wait(rs.getInt("is_wait"));
				tr.setRemark(rs.getString("remark")==null?"":rs.getString("remark"));
				routelist.add(tr);
			}
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
        finally{
        	try {
				con.close();
			} catch (SQLException e) {
				log.error(e.getMessage());
			}
        }
		return routelist;
	}
}
