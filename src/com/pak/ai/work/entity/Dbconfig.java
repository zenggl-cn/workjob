package com.pak.ai.work.entity;

public class Dbconfig {
	int db_id;
	String db_alais;
	String db_ip;
	String db_port;
	String db_driver;
	String db_url;
	String db_username;
	String db_password;
	String db_sid;
	int db_max_poolsize;
	String remark;
	
	
	public int getDb_id() {
		return db_id;
	}
	public void setDb_id(int db_id) {
		this.db_id = db_id;
	}
	public String getDb_alais() {
		return db_alais;
	}
	public void setDb_alais(String db_alais) {
		this.db_alais = db_alais;
	}
	
	public String getDb_username() {
		return db_username;
	}
	public void setDb_username(String db_username) {
		this.db_username = db_username;
	}
	public String getDb_password() {
		return db_password;
	}
	public void setDb_password(String db_password) {
		this.db_password = db_password;
	}
	public String getDb_sid() {
		return db_sid;
	}
	public void setDb_sid(String db_sid) {
		this.db_sid = db_sid;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getDb_ip() {
		return db_ip;
	}
	public void setDb_ip(String db_ip) {
		this.db_ip = db_ip;
	}
	public String getDb_port() {
		return db_port;
	}
	public void setDb_port(String db_port) {
		this.db_port = db_port;
	}
	public String getDb_driver() {
		return db_driver;
	}
	public void setDb_driver(String db_driver) {
		this.db_driver = db_driver;
	}
	public String getDb_url() {
		return db_url;
	}
	public void setDb_url(String db_url) {
		this.db_url = db_url;
	}
	public int getDb_max_poolsize() {
		return db_max_poolsize;
	}
	public void setDb_max_poolsize(int db_max_poolsize) {
		this.db_max_poolsize = db_max_poolsize;
	}
	
}
