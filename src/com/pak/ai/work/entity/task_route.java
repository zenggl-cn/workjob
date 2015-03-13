package com.pak.ai.work.entity;

public class task_route {
	
	int work_id=0;
	int pre_task_id=0;
	int next_task_id=0;
	int is_wait;
	String remark="";
	int state=0;
	work_task task;
	
	public int getWork_id() {
		return work_id;
	}
	public void setWork_id(int work_id) {
		this.work_id = work_id;
	}
	public int getPre_task_id() {
		return pre_task_id;
	}
	public void setPre_task_id(int pre_task_id) {
		this.pre_task_id = pre_task_id;
	}
	public int getNext_task_id() {
		return next_task_id;
	}
	public void setNext_task_id(int next_task_id) {
		this.next_task_id = next_task_id;
	}
	public int getIs_wait() {
		return is_wait;
	}
	public void setIs_wait(int is_wait) {
		this.is_wait = is_wait;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public work_task getTask() {
		return task;
	}
	public void setTask(work_task task) {
		this.task = task;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String toString(){
		StringBuffer sbf=new StringBuffer();
		sbf.append("work id:"+this.getWork_id()+";route state:"+this.getState());
		sbf.append(";pre task_id:"+this.getPre_task_id()+";next task id:"+this.getNext_task_id());
		sbf.append(";remark:"+this.getRemark());
		return sbf.toString();
	}
}
