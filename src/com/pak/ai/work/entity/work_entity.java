package com.pak.ai.work.entity;

import java.util.Date;

import com.pak.ai.work.manage.dt_util;

public class work_entity {
	int work_id;
	String work_name;
	int run_state;
	int isvalid;
	Date init_start_date;
	Date expire_date;
	
	Date last_run_date;
	Date this_run_date;
	Date next_run_date;
	
	int run_at_load;
	
	String cron_expression;
	String remark;
	
	public int getWork_id() {
		return work_id;
	}
	public void setWork_id(int work_id) {
		this.work_id = work_id;
	}
	public String getWork_name() {
		return work_name;
	}
	public void setWork_name(String work_name) {
		this.work_name = work_name;
	}
	public int getRun_state() {
		return run_state;
	}
	public void setRun_state(int run_state) {
		this.run_state = run_state;
	}
	
	public int getIsvalid() {
		return isvalid;
	}
	public void setIsvalid(int isvalid) {
		this.isvalid = isvalid;
	}
	public Date getLast_run_date() {
		return last_run_date;
	}
	public void setLast_run_date(Date last_run_date) {
		this.last_run_date = last_run_date;
	}
	public Date getThis_run_date() {
		return this_run_date;
	}
	public void setThis_run_date(Date this_run_date) {
		this.this_run_date = this_run_date;
	}
	public Date getNext_run_date() {
		return next_run_date;
	}
	public void setNext_run_date(Date next_run_date) {
		this.next_run_date = next_run_date;
	}
	public Date getInit_start_date() {
		return init_start_date;
	}
	public void setInit_start_date(Date init_start_date) {
		this.init_start_date = init_start_date;
	}
	public int getRun_at_load() {
		return run_at_load;
	}
	public void setRun_at_load(int run_at_load) {
		this.run_at_load = run_at_load;
	}
	public Date getExpire_date() {
		return expire_date;
	}
	public void setExpire_date(Date expire_date) {
		this.expire_date = expire_date;
	}
	public String getCron_expression() {
		return cron_expression;
	}
	public void setCron_expression(String cron_expression) {
		this.cron_expression = cron_expression;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String toString(){
		StringBuffer sbf=new StringBuffer();
		sbf.append("work id:"+this.getWork_id()+"; work name:"+this.getWork_name()+"; run state:"+this.getRun_state());
		sbf.append("; is valid:"+this.getIsvalid());
		sbf.append("; this run date:"+dt_util.dateTo_string(this.getThis_run_date()));
		sbf.append("; next run date:"+dt_util.dateTo_string(this.getNext_run_date()));
		sbf.append("; last run date:"+dt_util.dateTo_string(this.getLast_run_date()));
		sbf.append("; init date:"+dt_util.dateTo_string(this.getInit_start_date()));
		sbf.append("; expire date:"+dt_util.dateTo_string(this.getExpire_date()));
		sbf.append("");
		return sbf.toString();
	}
}
