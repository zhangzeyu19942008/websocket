package com.hk.business.websocket.util;

import com.hk.business.websocket.entity.AritubeVideoCollection;

public class LogUtil {
	public static void printAritubeCollections(){
		
		System.out.println("在线人数集合:在线人数:"+AritubeVideoCollection.getSessionsSize()+"--------"+AritubeVideoCollection.uid_session);
		System.out.println("等待集合:等待人数:"+AritubeVideoCollection.getwaitSize()+"--------"+AritubeVideoCollection.waitUid_info);
		System.out.println("房间集合:在线人数:"+AritubeVideoCollection.getRoomSize()+"--------"+AritubeVideoCollection.uid_uid);
	}
}
