package com.pak.ai.work.manage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pak.ai.db.ConnectionPool;
import com.pak.ai.work.entity.Hostmachine;

public class Host_manage {
	private final static Logger log=LoggerFactory.getLogger(Host_manage.class);
	public ArrayList<Hostmachine> getAllHost(){
		ArrayList<Hostmachine> alhost=new ArrayList<Hostmachine>();
		ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = null;
        PreparedStatement stm= null;
        ResultSet rs = null;
        con=pool.getConnection();
        String sql="select machine_id,host_ip_address,login_name,hs_password,host_directory,remark from tj.t_s_host";
        try {
			stm=con.prepareStatement(sql);
			rs=stm.executeQuery();
			while(rs.next()){
				Hostmachine mc=new Hostmachine();
				mc.setMachine_id(rs.getInt("machine_id"));
				mc.setHost_ip_address(rs.getString("host_ip_address"));
				mc.setLogin_name(rs.getString("login_name"));
				mc.setHs_password(rs.getString("hs_password"));
				mc.setHost_directory(rs.getString("host_directory"));
				mc.setRemark(rs.getString("remark"));
				
				mc.setHs_password(pw_util.decodeStr(mc.getHs_password()));
				alhost.add(mc);
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
		return alhost;
	}
}
