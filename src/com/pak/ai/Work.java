package com.pak.ai;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pak.ai.work.entity.Dbconfig;
import com.pak.ai.work.entity.Hostmachine;
import com.pak.ai.work.entity.proc_param;
import com.pak.ai.work.entity.task_route;
import com.pak.ai.work.entity.work_entity;
import com.pak.ai.work.entity.work_task;
import com.pak.ai.work.manage.Dbconfig_manage;
import com.pak.ai.work.manage.Host_manage;
import com.pak.ai.work.manage.Route_manage;
import com.pak.ai.work.manage.Work_manage;
import com.pak.ai.work.manage.pw_util;
import com.pak.ai.work.manage.task_manage;

public class Work {
	
	private static boolean forceExit = false;
	private final static Logger log=LoggerFactory.getLogger(Work.class);
	private final static String JOB_GROUP_NAME="PAK_JOB_GROUP_NAME";
	private final static String TIGGER_GROUP_NAME="PAK_TIGGER_GROUP_NAME";
	//private  static JobDetail[] jobs=null;
	private  static ArrayList<JobDetail> jobs=new ArrayList<JobDetail>();
	private  static Scheduler sched=null;
	public static void main(String[] args) {
		
		Option run=new Option("run","start", false, "Start work job.");
		Option encode=new Option("encode",true,"encode the password.");
		Option decode=new Option("decode",true,"decode the password");
		Option client=new Option("client",true,"send message to main");
		Option help=new Option("h","help",false,"display the command parameter and help information.");
		Option stop=new Option("stop","shutdown",false,"stop the procedure.");
		Options ops=new Options();
		ops.addOption(run);
		ops.addOption(encode);
		ops.addOption(decode);
		ops.addOption(client);
		ops.addOption(help);
		ops.addOption(stop);
		CommandLine line=null;
		CommandLineParser clp=new BasicParser();
		try {
			line = clp.parse(ops, args);
		} catch (org.apache.commons.cli.ParseException e1) {
			log.error(e1.getMessage());
		}
		
		if(line.hasOption("help")){
			HelpFormatter format=new HelpFormatter();
			format.printHelp("help", ops);
			System.exit(0);
		}
		
		if(line.hasOption("encode")){
			log.info("your input password is:"+line.getOptionValue("encode"));
			String encodeString=pw_util.encodeStr(line.getOptionValue("encode"));
			log.info("the encode string is:"+encodeString);
		}
		if(line.hasOption("decode")){
			log.info("you input encryption string is:{}",line.getOptionValue("decode"));
			String decodeString=pw_util.decodeStr(line.getOptionValue("decode"));
			log.info("The decode string is:{}",decodeString);
		}
		
		if(line.hasOption("client")){
			clientMoniter(line.getOptionValue("client"));
		}
		if(line.hasOption("stop")){
			clientMoniter("stop");
		}
		if(!line.hasOption("start")){
			return;
		}
		
		//Calendar cl=Calendar.getInstance();
		//Date nowdate=cl.getTime();
		//SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//log.info("Start work at:"+sdf.format(nowdate));
		
		Work_manage wmanage=new Work_manage();
		task_manage tmanage=new task_manage();
		Route_manage rmanage=new Route_manage();
		Host_manage hmanage=new Host_manage();
		Dbconfig_manage dbmanage=new Dbconfig_manage();
		
		ArrayList<work_entity> worklist=wmanage.getAllWork();
		ArrayList<work_task> tasklist=tmanage.getAllTask();
		ArrayList<task_route> routelist=rmanage.getAllRoute();
		ArrayList<Hostmachine> hostlist=hmanage.getAllHost();
		ArrayList<Dbconfig> dblist=dbmanage.getAllDbconfig();
		
		int k=0;
		for(work_task wt:tasklist){
			String mcid=wt.getHostmachine();
			for(Hostmachine mc:hostlist){
				if(String.valueOf(mc.getMachine_id()).equals(mcid)){
					wt.setHostmc(mc);
					break;
				}
			}
			
			for(Dbconfig df:dblist){
				if(df.getDb_id()==wt.getDbase()){
					wt.setDbf(df);
					break;
				}
			}
			if(wt.getTask_type()==0){
				ArrayList<proc_param> pp=new ArrayList<proc_param>();
				pp=tmanage.getproc_param(wt);
				wt.setProc_conf(pp);
			}
			tasklist.set(k, wt);
			k=k+1;
		}
		HashMap<Integer,work_task> taskmap=new HashMap<Integer, work_task>();
		for(work_task wt:tasklist){
			if(taskmap.containsKey(wt.getTask_id())){
				taskmap.remove(wt.getTask_id());
			}
			taskmap.put(wt.getTask_id(), wt);
		}
		
		HashMap<Integer, ArrayList<task_route>> routemap=new HashMap<Integer, ArrayList<task_route>>();
		for(work_entity wk:worklist){
			ArrayList<task_route> oneworklist=new ArrayList<task_route>();
			for(task_route tr :routelist){
				if(tr.getWork_id()==wk.getWork_id()){
					work_task wt=new work_task();
					if(tr.getNext_task_id()==-1){
						wt.setTask_id(-1);
					}else
					{
						wt=taskmap.get(tr.getNext_task_id());
					}
					
					tr.setTask(wt);
					oneworklist.add(tr);
					
				}
			}
			
			routemap.put(wk.getWork_id(), oneworklist);
		}
		
        try {
        	SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
			sched = schedFact.getScheduler();
			manageScheduler(worklist,routemap);
			sched.start();
			
			Thread workThread = new Thread(new MessageHandler());
			workThread.start();
			startMoniter();
			doShutDownWork();
		} catch (SchedulerException e) {
			log.error(e.getMessage());
		} 
	}
	
	private static void manageScheduler(ArrayList<work_entity> worklist ,HashMap<Integer, ArrayList<task_route>> routemap){
		try {
			int worksize=worklist.size();
			//JobDetail[] jobs =new JobDetail[worksize];
			//jobs =new JobDetail[worksize];
			Trigger[] triggers=new Trigger[worksize];
			//HashMap<Integer, JobDetail> jobmap=new HashMap<Integer, JobDetail>();
			
			for (int i = 0; i < worksize; i++) {
				work_entity singlework=worklist.get(i);
				//if(singlework.getIsvalid()!=1 || singlework.getRun_state()!=0){
				if(singlework.getRun_state()!=0){
					//log.error("the work:"+singlework.getWork_id()+" state is abnormal,the isvalid:"+singlework.getIsvalid()+" and run state:"+singlework.getRun_state());
					log.error("the work state is abnormal.");
					log.error(singlework.toString());
					continue;
				}
				
				JobDetailImpl jobDetail=null;
				JobDataMap jdm=new JobDataMap();
				jdm.put("workentity", singlework);
				jdm.put("worktaskroute",routemap.get(singlework.getWork_id() ));
				
				log.info("init work:"+singlework.getWork_id()+"----"+singlework.getWork_name());
				String triggername=String.valueOf(singlework.getWork_id());
				String jobname=String.valueOf(singlework.getWork_id());
				if(singlework.getCron_expression()!=null && singlework.getCron_expression()!=""){
					log.info("start conr work:"+singlework.getWork_id());
					CronTriggerImpl cronTrigger = new CronTriggerImpl();
					if(singlework.getRun_at_load()==1){
						cronTrigger.setStartTime(new Date());
					}else{
						if(singlework.getNext_run_date()==null){
							if(singlework.getInit_start_date()!=null){
								cronTrigger.setStartTime(singlework.getInit_start_date());
							}
						}else
						{
							cronTrigger.setStartTime(singlework.getNext_run_date());
						}
					}
					cronTrigger.setEndTime(singlework.getExpire_date());
					cronTrigger.setCronExpression(singlework.getCron_expression());
					cronTrigger.setGroup(TIGGER_GROUP_NAME);
					cronTrigger.setName(triggername);
					triggers[i]=cronTrigger;
					
					jobDetail=new JobDetailImpl();
					jobDetail.setJobClass(Job_work_manage.class);
					jobDetail.setJobDataMap(jdm);
					jobDetail.setName(jobname);
					jobDetail.setGroup(JOB_GROUP_NAME);

					//jobs[i]=jobDetail;
					jobs.add(jobDetail);
					sched.scheduleJob(jobDetail, triggers[i]);
					
				}
				else
				{
					/*jobs[i] =org.quartz.JobBuilder.newJob(HelloJob.class)
							.setJobData(jdm)
							.withIdentity(triggername, JOB_GROUP_NAME).build();*/
				}
				
				log.info(jobDetail.toString());
				//jobmap.put(singlework.getWork_id(), jobDetail);
				
			}
		} catch (ParseException e) {
			log.error(e.getMessage());
		} catch (SchedulerException e) {
			log.error(e.getMessage());
		}
	}
	private static void startMoniter() {
        ServerSocket server = null;
        BufferedReader br = null;
        Socket sock=null;
        try {
        	Properties pts=loadworkproperty();
            server = new ServerSocket();
            String server_host=pts.getProperty("server_host");
            int server_port=Integer.valueOf(pts.getProperty("server_port"));
            server.bind(new InetSocketAddress(server_host, server_port));
            //server.bind(new InetSocketAddress("127.0.0.1", 8888));
        } catch (Exception e) {
            log.info("bind port fail.");
            log.info(e.getMessage());
        }
        try {
        	sock = server.accept();
        	//sock.setSoTimeout(1000);
        	br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            while(!forceExit){
                String readContent = br.readLine();
                if(readContent==null || readContent==""){
                	continue;
                }
                log.info("input message:{}",readContent);
                if ("stop".equalsIgnoreCase(readContent)) {
                    log.info("ready to stop.");
                    readytostop();
                    br.close();
                    sock.close();
                    forceExit = true;
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }finally{
        	try {
				
				if(!sock.isClosed()){
					br.close();
					sock.close();
				}
			} catch (IOException e) {
				log.error(e.getMessage());
			}
        }
    }
	
	private static void clientMoniter(String sms){
		Properties pts=loadworkproperty();
		String targetHost = pts.getProperty("server_host");
        int targetPort = Integer.valueOf(pts.getProperty("server_port"));;
        Socket socket=null;
        try {
            socket = new Socket(targetHost, targetPort);
            PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream())));
            /*int k=0;
            while(true){
            	pw.println("sms from client and connect to server sucess. "+k);
            	pw.flush();
            	k++;
            	try {
					Thread.sleep( 1000 );
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            }*/
            pw.println("sms from client and connect to server sucess. ");
        	pw.flush();
        	
        	pw.println(sms);
        	pw.flush();
        	
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
        	try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}
	
	private static void doShutDownWork() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				//todo something
				log.info("Im going to end.");
				}
			}
		);
	}
	
	private static Properties loadworkproperty(){
		Properties pts=null;
		try {
			pts=new Properties();
			pts.load(Work.class.getClassLoader().getResourceAsStream("workjob.properties"));
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return pts;
	}
	private static class MessageHandler implements Runnable{
        @Override
        public void run() {
                while(!forceExit){
                    try{
                       //todo something
                        Thread.sleep(1000);
                    }catch (Exception e) {
                        log.error(e.getMessage());
                    }
                }
	            log.info("exit work job.");
	            System.exit(0);
        }
    }
	
	private static void readytostop(){
		for(JobDetail job:jobs){
			if(job!=null){
				log.info("job name:"+job.getKey().getName());
				work_entity end_work=(work_entity)job.getJobDataMap().get("workentity");
				log.info("work information work id:"+end_work.getWork_id()+";work name:"+end_work.getWork_name());
				log.info("work entity:"+end_work.toString());
				@SuppressWarnings("unchecked")
				ArrayList<task_route> al=(ArrayList<task_route>)job.getJobDataMap().get("worktaskroute");
				for(task_route end_tr:al){
					log.info("---task route:"+end_tr.toString());
					log.info("------task:"+end_tr.getTask().toString());
				}
				
			}
		}
		try {
			if(sched!=null){
				log.info("the scheduler shutdown:"+sched.getSchedulerName());
				sched.shutdown();
			}
		} catch (SchedulerException e) {
			log.error(e.getMessage());
		}
	}
}
