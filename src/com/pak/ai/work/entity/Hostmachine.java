package com.pak.ai.work.entity;

public class Hostmachine {
	int machine_id;
	String host_ip_address;
	String login_name;
	String hs_password;
	String host_directory;
	String remark;
	
	public int getMachine_id() {
		return machine_id;
	}
	public void setMachine_id(int machine_id) {
		this.machine_id = machine_id;
	}
	public String getHost_ip_address() {
		return host_ip_address;
	}
	public void setHost_ip_address(String host_ip_address) {
		this.host_ip_address = host_ip_address;
	}
	public String getLogin_name() {
		return login_name;
	}
	public void setLogin_name(String login_name) {
		this.login_name = login_name;
	}
	
	public String getHs_password() {
		return hs_password;
	}
	public void setHs_password(String hs_password) {
		this.hs_password = hs_password;
	}
	public String getHost_directory() {
		return host_directory;
	}
	public void setHost_directory(String host_directory) {
		this.host_directory = host_directory;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
