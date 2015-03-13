package com.pak.ai;

import java.util.ArrayList;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pak.ai.work.entity.task_route;
import com.pak.ai.work.entity.work_entity;
import com.pak.ai.work.entity.work_task;
import com.pak.ai.work.manage.Work_manage;
import com.pak.ai.work.manage.task_manage;

public class Job_work_manage implements Job {
	private final static Logger logger=LoggerFactory.getLogger(Job_work_manage.class);
	public void execute(JobExecutionContext jobcontext) throws JobExecutionException {
		
		JobDataMap jdmap= jobcontext.getMergedJobDataMap();
		work_entity work=(work_entity) jdmap.get("workentity");
		logger.info("start Job_work_manage,work id:{}",work.getWork_id());
		//todo update work state
		Work_manage wm=new Work_manage();
		work.setRun_state(1);
		work.setThis_run_date(jobcontext.getFireTime());
		work.setNext_run_date(jobcontext.getNextFireTime());
		
		if(!wm.upwork(work)){
			logger.error("The work {} start fail,please check it.",work.getWork_id());
			return;
		}
		
		@SuppressWarnings("unchecked")
		ArrayList<task_route> al=(ArrayList<task_route>)jdmap.get("worktaskroute");
		
		ArrayList<task_route> runningtask=new ArrayList<task_route>();
		ArrayList<task_route> rantask=new ArrayList<task_route>();
		boolean is_run=false;
		while(true){
			if(runningtask.size()==0){
				runningtask=getRunningTask(al,rantask);
				rantask=new ArrayList<task_route>();
				is_run=false;
			}
			
			if(runningtask.size()==0){
				break;
			}
			
			if(!is_run){
				is_run=true;
				for(task_route task_id:runningtask){
					for (task_route tr : al) {
						if(task_id.getNext_task_id()==-1){
							continue;
						}
						//check the route link of task
						if(task_id.getNext_task_id()==tr.getNext_task_id() && task_id.getPre_task_id()==tr.getPre_task_id() && tr.getState()==1){
							//tr.setState(1);
							if(tr.getTask().getTask_type()==0){
								//oracle procedure
								Task_dbprocedure_impl proc=new Task_dbprocedure_impl(tr);
								new Thread(proc).start();
							}else if(tr.getTask().getTask_type()==1){
								//linux sheel
							}
						}
					}
				}
			}
			for (task_route tr : al) {
				if(tr.getState()==2){//check the route state,2:run finished;1:ready run;0:default state
					for (int i = 0; i < runningtask.size(); i++) {
						task_route task = runningtask.get(i);
						if(task.getNext_task_id()==tr.getNext_task_id()){
							runningtask.remove(i);
							rantask.add(task);
							
							ArrayList<task_route> midtask=new ArrayList<task_route>();
							midtask.add(task);
							for(task_route tt:getRunningTask(al,midtask)){
								runningtask.add(tt);
								is_run=false;
							}
						}
					}
				}
				if(runningtask.size()==0){
					break;
				}
			}
			
		}
		///the job run finished,init the task and work. task_state:0 work_state:0
		task_manage tmg=new task_manage();
		ArrayList<work_task> uptask=new ArrayList<work_task>();
		for (task_route tr : al) {
			tr.setState(0);
			tr.getTask().setRun_state(0);
			if(tr.getTask().getTask_id()!=-1){
				uptask.add(tr.getTask());
			}
		}
		tmg.uptask(uptask);
		
		work.setRun_state(0);
		if(jobcontext.getPreviousFireTime()!=null){
			work.setLast_run_date(jobcontext.getPreviousFireTime());
		}
		wm.upwork(work);
		logger.info("the work:{} run finished.",work.getWork_id());
	}
	
	private ArrayList<task_route> getRunningTask(ArrayList<task_route> rtlist,ArrayList<task_route> runnedtask){
		ArrayList<task_route> task=new ArrayList<task_route>();
		if(runnedtask.size()==0){
			task_route tr=new task_route();
			tr.setNext_task_id(0);
			tr.setState(2);
			runnedtask.add(tr);
		}
		for (task_route tr : runnedtask) {
			if(tr.getNext_task_id()==0 || tr.getNext_task_id()==-1){
				continue;
			}
			if(tr.getTask().getRun_state()!=2 && tr.getTask().getRetry()>0){
				//redo the fail task
				if(tr.getTask().getRetry()>tr.getTask().getRetry_times()){
					tr.getTask().setRetry_times(tr.getTask().getRetry_times()+1);
					tr.setState(1);
					tr.getTask().setRun_state(0);
					task.add(tr);
					continue;
				}
			}
			if(tr.getTask().getRun_state()!=2 && tr.getTask().getIs_continue()==0){
				//the route whether continue when the task execute fail.
				continue;
			}
			if(tr.getIs_wait()==1){
				//it's need check the wait sate,the task will be wait until all task complate. it' like a task group
				for (task_route mtr : rtlist) {
					if(mtr.getPre_task_id()==tr.getPre_task_id() && mtr.getNext_task_id()==tr.getNext_task_id() && mtr.getState()!=2){
						return null;
					}
				}
			}else{
				continue;
			}
		}
		
		synchronized (this) {
			for (task_route tr : rtlist) {
				if(tr.getState()!=0){
					continue;
				}
				for(task_route i:runnedtask){
					if(i.getNext_task_id()==tr.getPre_task_id() && i.getState()!=0 ){
						if(tr.getNext_task_id()!=-1 && tr.getState()==0){
							if(tr.getIs_wait()==01){
								tr.setState(1);
								task.add(tr);
							}else if(tr.getIs_wait()==1)
							{
								boolean is_finish_all=true;
								for(task_route pre_tr:rtlist){
									if(pre_tr.getState()!=2){
										is_finish_all=false;
										break;
									}
								}
								if(is_finish_all){
									tr.setState(1);
									task.add(tr);
								}
							}else{
								logger.error("the route of waite value abnormal."+tr.toString());
							}
						}
					}
				}
			}
		}
		logger.info("run task size:{}",task.size());
		
		return task;
	}

}
