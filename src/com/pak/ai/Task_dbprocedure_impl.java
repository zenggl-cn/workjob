package com.pak.ai;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pak.ai.db.ConnectionPool;
import com.pak.ai.work.entity.proc_param;
import com.pak.ai.work.entity.task_route;
import com.pak.ai.work.entity.work_task;
import com.pak.ai.work.manage.task_manage;

public class Task_dbprocedure_impl implements Runnable {

	private final static Logger logger=LoggerFactory.getLogger(Task_dbprocedure_impl.class);
	private task_route tr;
	
	public Task_dbprocedure_impl(task_route route){
		tr=route;
	}
	
	@Override
	public void run() {
		if(tr.getState()==1){
			Calendar cal=Calendar.getInstance();
			Date nowdt=new Date();
			cal.setTime(nowdt);
			work_task wtask=tr.getTask();
			wtask.setThis_run_date(cal.getTime());
			wtask.setRun_state(1);
			wtask.setRemark("");
			task_manage tma=new task_manage();
			tma.uptask(wtask);
			
			ArrayList<proc_param> p_param=wtask.getProc_conf();
			
			logger.info("start run task:"+tr.getWork_id()+":"+tr.getPre_task_id()+":"+tr.getTask().getTask_id());
			String command_param=wtask.getCommand_param();
			command_param=command_param.replaceAll("#", " #");
			//start logic
			
			String[] params=command_param.split("#");
			for (int i = 0; i < params.length; i++) {
				if(params[i].trim().equals("")){
					params[i]=null;
					
				}else{
					params[i]=params[i].trim();
				}
			}
			
			ConnectionPool pool = ConnectionPool.getInstance();
			Connection con=pool.getConnection(wtask.getDbase());
			StringBuffer commbf=new StringBuffer();
			commbf.append("{call ");
			commbf.append(wtask.getCommand()).append("(");
			if(params.length>1){
				for(int i=0;i<params.length-1;i++){
					commbf.append("?,");
				}
			}
			if(params.length>=1){
				commbf.append("?");
			}
			commbf.append(")}");
			
			logger.info(commbf.toString());
			CallableStatement cstm=null;
			try {
				cstm=con.prepareCall(commbf.toString());
				
				int p=0;
				int error_code=0;
				boolean is_error_code=false;
				String error_info="";
				boolean is_error_info=false;
				if(params.length==p_param.size()){
					for(proc_param pp:p_param){
						if(pp.getOp_type().equals("IN")){
							if(pp.getParam_type().equals("VARCHAR2")){
								cstm.setString(pp.getParam_name(),params[p]);
							}else if(pp.getParam_type().equals("NUMBER")){
								cstm.setInt(pp.getParam_name(),Integer.valueOf(params[p]) );
							}else{
								cstm.setString(pp.getParam_name(),params[p]);
							}
						}
						
						if(pp.getOp_type().equals("OUT")){
							if(pp.getParam_name().equals("V_ERROR_CODE") && pp.getParam_type().equals("NUMBER")){
								is_error_code=true;
							}
							if(pp.getParam_name().equals("V_ERROR_INFO") && pp.getParam_type().equals("VARCHAR2")){
								is_error_info=true;
							}
							
							if(pp.getParam_type().equals("VARCHAR2")){
								cstm.registerOutParameter(pp.getParam_name(), java.sql.Types.VARCHAR);
							}else{
								cstm.registerOutParameter(pp.getParam_name(), java.sql.Types.INTEGER);
							}
						}
						p++;
					}
				}
				cstm.execute();
				
				// save the procedure run error code to task state.
				// the task normal state is 2 when the procedure run success.
				if(is_error_code){
					if(cstm.getInt("V_ERROR_CODE")!=0){
						error_code=cstm.getInt("V_ERROR_CODE");
						//save the procedure return error code.
					}else{
						//the procedure execute success and the out parameter code is 0
						error_code=2;
					}
					wtask.setRun_state(error_code);
				}
				if(is_error_info){
					//if exists out parameter error info .
					error_info=cstm.getString("V_ERROR_INFO");
					wtask.setRemark(error_info);
				}else{
					wtask.setRemark("OK");
				}
			
			} catch (SQLException e) {
				logger.error(e.getMessage());
				wtask.setRun_state(e.getErrorCode());
				wtask.setRemark(e.getMessage());
			}
			
			//end save task state
			nowdt=new Date();
			cal.setTime(nowdt);
			wtask.setFinish_date(cal.getTime());
			tma.uptask(wtask);
			tr.setTask(wtask);
			tr.setState(2);
			//logger.info("end run task:"+tr.getWork_id()+":"+tr.getPre_task_id()+":"+tr.getTask().getTask_id());
			logger.info("end run task:"+tr.toString());
		}
	}

	public task_route getTr() {
		return tr;
	}


}
