package com.pak.ai.work.manage;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class pw_util {
	public final static String ENCODING="UTF-8";
	private final static Logger log=LoggerFactory.getLogger(pw_util.class);
	public static String encodeStr(String code){
		Base64 b64=new Base64();
		
		byte[] b;
		try {
			b = b64.encode(code.getBytes(ENCODING));
			code=new String(b,ENCODING);
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage());
		}
		return code;
	}
	
	public static String decodeStr(String encodeStr){  
		try {
			byte[] b=encodeStr.getBytes(ENCODING);
			Base64 base64=new Base64();
			b=base64.decode(b);
			encodeStr = new String(b,ENCODING);
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage());
		}
        return encodeStr;
    }
	
}
