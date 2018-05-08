
package com.hk.business.websocket.util;

import javax.websocket.Session;

import com.hk.util.Base64;

import net.sf.json.JSONObject;

public class SendMsgUtil {
	public static void sendMsg(Session session, String type, String data) {
		JSONObject msg = new JSONObject();
		msg.put("type", type);
		msg.put("data", Base64.getBase64(data));
		msg.put("videoInfo", "");
		if (null != session && session.isOpen()) {
			session.getAsyncRemote().sendText(Base64.getBase64(msg.toString()));
		}
	}
	/**
	 * 转发type为offer,answer,candidate消息方法
	 * 
	 * @param session
	 * @param message
	 */
	public static void transpondMsg(Session session, String message) {
		if (session != null && session.isOpen()) {
			session.getAsyncRemote().sendText(message);
			System.out.println(message);
		}
	}
}
