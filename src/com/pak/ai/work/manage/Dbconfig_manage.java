package com.pak.ai.work.manage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pak.ai.db.ConnectionPool;
import com.pak.ai.work.entity.Dbconfig;

public class Dbconfig_manage {
	private final static Logger log=LoggerFactory.getLogger(Dbconfig_manage.class);
	public ArrayList<Dbconfig> getAllDbconfig(){
		ArrayList<Dbconfig> al=new ArrayList<Dbconfig>();
		ConnectionPool pool = ConnectionPool.getInstance();
        Connection con = null;
        PreparedStatement stm= null;
        ResultSet rs = null;
        con=pool.getConnection();
        String sql="select db_id,db_alais,db_ip,db_port,db_driver,db_url,db_username,db_password,"
        		+ "db_sid,db_max_poolsize,remark from tj.t_s_dbconfig";
        try {
			stm=con.prepareStatement(sql);
			rs=stm.executeQuery();
			while(rs.next()){
				Dbconfig df=new Dbconfig();
				df.setDb_id(rs.getInt("db_id"));
				df.setDb_alais(rs.getString("db_alais"));
				df.setDb_ip(rs.getString("db_ip"));
				df.setDb_port(rs.getString("db_port"));
				df.setDb_driver(rs.getString("db_driver"));
				df.setDb_url(rs.getString("db_url"));
				df.setDb_username(rs.getString("db_username"));
				df.setDb_password(rs.getString("db_password"));
				df.setDb_sid(rs.getString("db_sid"));
				df.setDb_max_poolsize(rs.getInt("db_max_poolsize"));
				df.setRemark(rs.getString("remark"));
				
				df.setDb_password(pw_util.decodeStr(df.getDb_password()));
				
				al.add(df);
			}
		} catch (SQLException e) {
			log.error(e.getMessage());
			System.exit(0);
		}
        finally{
        	try {
				con.close();
			} catch (SQLException e) {
				log.error(e.getMessage());
			}
        }
		return al;
	}
}
