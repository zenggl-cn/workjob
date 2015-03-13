/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pak.ai.db;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.pak.ai.work.entity.Dbconfig;

/**
 *
 * @author zenggl
 */
public class ConnectionPool {
    private final DataSource ds;
    private HashMap<Integer,DataSource> dsmap;
    private static ConnectionPool pool;
    private final static Logger logger=LoggerFactory.getLogger(ConnectionPool.class);
    private ArrayList<Dbconfig> dblist;
    private ConnectionPool(){
        ds = new ComboPooledDataSource();
        initConfigDb();
    }
    public static final ConnectionPool getInstance(){
        if(pool==null){
            try{
                pool = new ConnectionPool();
            }catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return pool;
    }  
    public synchronized final Connection getConnection() {
        try {
        	
            return ds.getConnection();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return null;
    }
    
    public synchronized final Connection getConnection(Integer keys) {
        try {
        	if(keys==null || keys==0){
        		return getConnection();
        	}
            return dsmap.get(keys).getConnection();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return null;
    }
    
    public final void colose_con(Connection con){
    	try {
			con.close();
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
    }
    
    private final void initConfigDb(){
    	dsmap=new HashMap<java.lang.Integer, DataSource>();
    	dblist=new ArrayList<Dbconfig>();
    	Connection conn=getConnection();
    	String sql="select db_id, db_alais, db_ip, db_port, db_driver, db_url, db_username, db_password, db_sid, db_max_poolsize,remark from t_s_dbconfig";
    	try {
			Statement stm=conn.createStatement();
			ResultSet rs=stm.executeQuery(sql);
			
			while (rs.next()){
				Dbconfig dbcf = Dbconfig.class.newInstance();
				dbcf.setDb_id(rs.getInt("db_id"));
				dbcf.setDb_alais(rs.getString("db_alais"));
				dbcf.setDb_ip(rs.getString("db_ip"));
				dbcf.setDb_port(rs.getString("db_port"));
				dbcf.setDb_driver(rs.getString("db_driver"));
				dbcf.setDb_url(rs.getString("db_url"));
				dbcf.setDb_username(rs.getString("db_username"));
				dbcf.setDb_password(rs.getString("db_password"));
				dbcf.setDb_sid(rs.getString("db_sid"));
				dbcf.setRemark(rs.getString("remark"));
				dbcf.setDb_max_poolsize(rs.getInt("db_max_poolsize"));
				if(dbcf.getDb_max_poolsize()<=0){
					dbcf.setDb_max_poolsize(15);
				}
				//Class.forName(dbcf.getDb_driver()).newInstance();
				String db_url=dbcf.getDb_url();
				if(db_url==null || db_url==""){
					db_url="jdbc:oracle:thin:@"+dbcf.getDb_ip()+":"+dbcf.getDb_port()+":"+dbcf.getDb_sid();
				}
				ComboPooledDataSource cpds = new ComboPooledDataSource();
				cpds.setDriverClass(dbcf.getDb_driver());
				cpds.setJdbcUrl(db_url);
				cpds.setUser(dbcf.getDb_username());
				cpds.setPassword(dbcf.getDb_password());
				cpds.setMaxPoolSize(dbcf.getDb_max_poolsize());
				dsmap.put(dbcf.getDb_id(), cpds);
				dblist.add(dbcf);
			}
			conn.close();
		} catch (SQLException e) {
			logger.error(e.getMessage());
		} catch (InstantiationException e) {
			logger.error(e.getMessage());
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage());
		} catch (SecurityException e) {
			logger.error(e.getMessage());
		}  catch (PropertyVetoException e) {
			logger.error(e.getMessage());
		}
    }
	public ArrayList<Dbconfig> getDblist() {
		return dblist;
	}
    
}
