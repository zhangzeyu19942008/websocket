package com.hk.business.websocket.service;

import javax.websocket.Session;

import net.sf.json.JSONObject;

public interface IMessageHandleService {
	void Handle(String message, Session session, String uid);

	void match_start(String uid, JSONObject json_data);

	void match_close(String uid, JSONObject json_data);

	void match_offer(String uid, JSONObject json_data, JSONObject json_message);

	void match_answer(String uid, JSONObject json_data, JSONObject json_message);

	void match_candidate(String uid,String message);
	
	void send_gift(String uid, JSONObject json_data);

}
