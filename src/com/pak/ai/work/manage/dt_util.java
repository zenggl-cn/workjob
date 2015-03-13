package com.pak.ai.work.manage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class dt_util {
	public static String dateTo_string(Date dt){
		if(dt==null){
			return "";
		}
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(dt);
	}
}
