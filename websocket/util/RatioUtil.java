package com.hk.business.websocket.util;

public class RatioUtil {
	public  static String getTrueOrFlase(){
		double random = Math.random();
		
		if(random<1/4){
			//真人
			return "true";
		}
		else{
			//假视频
			return "false";
		}
		
		
	}
	public  static  String getSex(){
		double random = Math.random();
		
		if(random<2/3){
			//男
			return "1";
		}
		else{
			//女
			return "2";
		}
		
		
	}
}
