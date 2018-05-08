package com.hk.business.websocket.server;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.hk.business.websocket.service.impl.CallbackService;
import com.hk.business.websocket.service.impl.MessageHandleService;
import com.hk.util.RedisUtil;
import com.hk.util.SpringContextsUtil;

/**
 * @version1.0
 * 
 * @author zzy
 *
 */
@ServerEndpoint("/aritube.video/{token}")
public class TestWebsocket {
	private static final CallbackService callbackService = (CallbackService) SpringContextsUtil
			.getBean("callbackService");
	private static final MessageHandleService messageHandleService = (MessageHandleService) SpringContextsUtil
			.getBean("messageHandleService");

	@OnOpen
	public void onOpen(Session session, @PathParam("token") String token) {
		String uid = RedisUtil.getValue(token);
		if (null != uid) {
			callbackService.onOpen(uid, session);
		}else{
			callbackService.loginedError(uid, session);
		}
	}

	@OnClose
	public void onClose(Session session, CloseReason reason, @PathParam("token") String token) {
		String uid = RedisUtil.getValue(token);
		callbackService.onClose(session, reason, uid);
	}

	@OnError
	public void onError(Session session, Throwable error, @PathParam("token") String token) {
		String uid = RedisUtil.getValue(token);
		callbackService.onError(session, error, uid);

	}

	@OnMessage
	public void onMessage(String message, Session session, @PathParam("token") String token) {
		String uid = RedisUtil.getValue(token);
		if (null != uid) {
			messageHandleService.Handle(message, session, uid);
		}else{
			callbackService.loginedError(uid, session);
		}
	}

}
