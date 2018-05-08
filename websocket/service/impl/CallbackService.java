package com.hk.business.websocket.service.impl;

import javax.websocket.CloseReason;
import javax.websocket.Session;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.hk.business.websocket.entity.AritubeVideoCollection;
import com.hk.business.websocket.service.ICallbackService;
import com.hk.business.websocket.util.SendMsgUtil;
import com.hk.util.Base64;

import net.sf.json.JSONObject;

@Component("callbackService")
@Transactional
public class CallbackService implements ICallbackService {

	@Override
	public void onOpen(String uid, Session session) {
		AritubeVideoCollection.putSessions(uid, session);
		System.out.println("有用户加入app 当前在线人数:" + AritubeVideoCollection.getSessionsSize());
		System.out.println("用户uid为:" + uid);

	}

	@Override
	public void onClose(Session session, CloseReason reason, String uid) {
		AritubeVideoCollection.removeSessions(uid);
		AritubeVideoCollection.remove_wait(uid);
		String oid = AritubeVideoCollection.removeRoom(uid);
		// 通知与之通话的另一方 会话已中断
		Session osession = AritubeVideoCollection.getSession(uid);
		JSONObject json_data = new JSONObject();
		json_data.put("openid", oid);
		SendMsgUtil.sendMsg(osession, "match_close", json_data.toString());
		System.out.println("有用户退出app 当前在线人数:" + AritubeVideoCollection.getSessionsSize());
		System.out.println("用户uid为:" + uid);
		System.out.println("close becauseof " + reason);

	}

	@Override
	public void onError(Session session, Throwable error, String uid) {
		System.out.println("error because of");
		error.printStackTrace();
	}

	@Override
	public void loginedError(String uid, Session session) {
		JSONObject json = new JSONObject();
		json.put("type", "user_logined");
		session.getAsyncRemote().sendText(Base64.getBase64(json.toString()));
		System.out.println("该用户账号在其他手机登入");
	}
}
