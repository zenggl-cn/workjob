package com.pak.ai.work.entity;

import java.util.ArrayList;
import java.util.Date;

import com.pak.ai.work.manage.dt_util;

public class work_task {
	int task_id;
	String task_name;
	Date this_run_date;
	Date finish_date;
	int isvalid;
	int run_state;
	int task_type;
	String command;
	String command_param;
	String hostmachine;
	int dbase;
	String session_id;
	int retry;
	int retry_times=0;
	int is_continue;
	String remark;
	Hostmachine hostmc;
	Dbconfig dbf;
	ArrayList<proc_param> proc_conf;
	
	public int getTask_id() {
		return task_id;
	}
	public void setTask_id(int task_id) {
		this.task_id = task_id;
	}
	public String getTask_name() {
		return task_name;
	}
	public void setTask_name(String task_name) {
		this.task_name = task_name;
	}
	
	public Date getThis_run_date() {
		return this_run_date;
	}
	public void setThis_run_date(Date this_run_date) {
		this.this_run_date = this_run_date;
	}
	public Date getFinish_date() {
		return finish_date;
	}
	public void setFinish_date(Date finish_date) {
		this.finish_date = finish_date;
	}
	
	public int getIsvalid() {
		return isvalid;
	}
	public void setIsvalid(int isvalid) {
		this.isvalid = isvalid;
	}
	public int getRun_state() {
		return run_state;
	}
	public void setRun_state(int run_state) {
		this.run_state = run_state;
	}
	public int getTask_type() {
		return task_type;
	}
	public void setTask_type(int task_type) {
		this.task_type = task_type;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public String getCommand_param() {
		return command_param;
	}
	public void setCommand_param(String command_param) {
		this.command_param = command_param;
	}
	public String getHostmachine() {
		return hostmachine;
	}
	public void setHostmachine(String hostmachine) {
		this.hostmachine = hostmachine;
	}
	
	public int getDbase() {
		return dbase;
	}
	public void setDbase(int dbase) {
		this.dbase = dbase;
	}
	public String getSession_id() {
		return session_id;
	}
	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}
	public int getRetry() {
		return retry;
	}
	public void setRetry(int retry) {
		this.retry = retry;
	}
	public int getRetry_times() {
		return retry_times;
	}
	public void setRetry_times(int retry_times) {
		this.retry_times = retry_times;
	}
	public int getIs_continue() {
		return is_continue;
	}
	public void setIs_continue(int is_continue) {
		this.is_continue = is_continue;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Hostmachine getHostmc() {
		return hostmc;
	}
	public void setHostmc(Hostmachine hostmc) {
		this.hostmc = hostmc;
	}
	public Dbconfig getDbf() {
		return dbf;
	}
	public void setDbf(Dbconfig dbf) {
		this.dbf = dbf;
	}
	public ArrayList<proc_param> getProc_conf() {
		return proc_conf;
	}
	public void setProc_conf(ArrayList<proc_param> proc_conf) {
		this.proc_conf = proc_conf;
	}
	
	public String toString(){
		StringBuffer sbf=new StringBuffer();
		sbf.append("task id:"+this.getTask_id()+";task name:"+this.getTask_name());
		sbf.append(";task type:"+this.getTask_type()+";task run state:"+this.getRun_state());
		sbf.append(";command:"+this.getCommand()+";command paramerter:"+this.getCommand_param());
		sbf.append("; this run date:"+dt_util.dateTo_string(this.getThis_run_date()));
		sbf.append("; run finish date:"+dt_util.dateTo_string(this.getFinish_date()));
		return sbf.toString();
	}
}
