package com.hk.business.websocket.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.websocket.Session;

import org.springframework.util.StringUtils;

public class AritubeVideoCollection {
	// 会话集合
	public static final Map<String, Session> uid_session = Collections.synchronizedMap(new HashMap<String, Session>());
	// Map里面的map key:自己的性别 key:自己要筛选的性别 key:...
	public static final Map<String, WaitInfo> waitUid_info = Collections
			.synchronizedMap(new HashMap<String, WaitInfo>());
	// 房间集合 key_value 为用户uid 两个uid组成一个房间
	public static final Map<String, String> uid_uid = Collections.synchronizedMap(new HashMap<String, String>());
	

	public static void putSessions(String uid, Session session) {
		uid_session.put(uid, session);
	}

	public static void removeSessions(String uid) {
		uid_session.remove(uid);
	}

	public static Session getSession(String uid) {
		return uid_session.get(uid);
	}

	public static int getSessionsSize() {
		return uid_session.size();
	}

	public static void put_wait(String uid, WaitInfo waitInfo) {
		waitUid_info.put(uid, waitInfo);
	}

	public static void remove_wait(String uid) {
		waitUid_info.remove(uid);
	}

	public static int getwaitSize() {
		return waitUid_info.size();
	}

	public static Map<String, WaitInfo> getwaitMap() {
		return waitUid_info;
	}
	public static void createRoom (String uid,String oid) {
		uid_uid.put(uid, oid);
	}
	public static int getRoomSize () {
		return uid_uid.size();
	}

	// 删除房间
	// 如果该用户为map中key 则直接删除
	// 如果该用户为map中的value 则找到key 再删除
	// 返回对方的uid 如果房间不存在返回null
	public static String removeRoom(String uid) {
		if (StringUtils.hasLength(uid)) {
			Iterator<Entry<String, String>> it = uid_uid.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> entry = it.next();
				if (entry.getKey().equals(uid)) {
					it.remove();
					return entry.getValue();
				}
				if (entry.getValue().equals(uid)) {
					it.remove();
					return entry.getKey();
				}
			}
		}
		return null;
	}

	// 返回对方的uid
	public static String getOid(String uid) {
		for (Map.Entry<String, String> entry : uid_uid.entrySet()) {
			if (null != uid && uid.equals(entry.getKey())) {
				return entry.getValue();
			}
			if (null != uid && uid.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	//生成房间号
	public static String getRoom(String uid,String oid){
		return uid+"_"+oid;
	}
}
