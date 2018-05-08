package com.hk.business.websocket.service;

import javax.websocket.CloseReason;
import javax.websocket.Session;

public interface ICallbackService {
	void onOpen(String uid,Session session);
	void onClose(Session session, CloseReason reason,String uid);
	void onError(Session session, Throwable error, String uid);
	//账号在其他手机登录
	void loginedError(String uid,Session session);
}
