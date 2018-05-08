package com.hk.business.websocket.service.impl;

import java.util.Date;

import javax.websocket.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.hk.business.service.MatchStrategyService;
import com.hk.business.service.UserAccountService;
import com.hk.business.websocket.entity.AritubeVideoCollection;
import com.hk.business.websocket.entity.ClientCondition;
import com.hk.business.websocket.entity.UserCondition;
import com.hk.business.websocket.entity.UserInfo;
import com.hk.business.websocket.entity.WaitInfo;
import com.hk.business.websocket.service.IMessageHandleService;
import com.hk.business.websocket.util.DistanceUtil;
import com.hk.business.websocket.util.LogUtil;
import com.hk.business.websocket.util.SendMsgUtil;
import com.hk.util.Base64;

import net.sf.json.JSONObject;

@Component("messageHandleService")
@Transactional
public class MessageHandleService implements IMessageHandleService {
	@Autowired
	UserAccountService userAccountService;
	@Autowired
	MatchStrategyService matchStrategyService;
	@Autowired
	UserConditionService userConditionService;

	@Override
	public void Handle(String message, Session session, String uid) {
		JSONObject json_message = JSONObject.fromObject(Base64.getFromBase64(message));
		JSONObject json_data = JSONObject.fromObject(Base64.getFromBase64(json_message.getString("data")));
		String type = json_message.getString("type");
		if ("match_start".equals(type)) {
			match_start(uid, json_data);
		} else if ("match_close".equals(type)) {
			match_close(uid, json_data);
		} else if ("offer".equals(type)) {
			match_offer(uid, json_message, json_data);
		} else if ("answer".equals(type)) {
			match_answer(uid, json_message, json_data);
		} else if ("send_gift".equals(type)) {
			send_gift(uid, json_data);
		} else if ("candidate".equals(type)) {
			match_candidate(uid, message);
		}

	}

	@Override
	public void match_start(String uid, JSONObject json_data) {
		// 解析客户端筛选条件
		String filter_sex = json_data.getString("sex");
		ClientCondition client_condition = new ClientCondition();
		client_condition.setFilter_sex(filter_sex);
		// 根据客户端删选条件 后台cms 生成用户condition
		UserCondition user_condition = userConditionService.getCondition(uid, matchStrategyService.get(1),
				client_condition);
		// 将用户的有用的信息 用户的condition 放如等待用户对象中
		WaitInfo waitInfo = new WaitInfo();
		UserInfo userInfo = new UserInfo();
		userInfo.setSex(userAccountService.getUserAccount(uid).getU_sex());
		userInfo.setAddTime(new Date().getTime());
		waitInfo.setUserCondition(user_condition);
		waitInfo.setUserInfo(userInfo);
		AritubeVideoCollection.put_wait(uid, waitInfo);
		System.out.println("match_start 用户uid----"+uid);
		LogUtil.printAritubeCollections();
	}

	@Override
	public void match_close(String uid, JSONObject json_data) {
		AritubeVideoCollection.remove_wait(uid);
		String oid = AritubeVideoCollection.removeRoom(uid);
		Session oSession = AritubeVideoCollection.getSession(oid);
		JSONObject data = new JSONObject();
		data.put("openid", uid);
		SendMsgUtil.sendMsg(oSession, "match_close", data.toString());
		System.out.println("match_close 用户uid----"+uid);
		LogUtil.printAritubeCollections();
	}

	@Override
	public void match_offer(String uid, JSONObject json_message, JSONObject json_data) {
		Session oSession = AritubeVideoCollection.getSession(AritubeVideoCollection.getOid(uid));
		double distance = DistanceUtil.getDistance(uid);
		json_data.put("distance", distance);
		json_message.put("data", Base64.getBase64(json_data.toString()));
		SendMsgUtil.transpondMsg(oSession, Base64.getBase64(json_message.toString()));
	}

	@Override
	public void match_answer(String uid, JSONObject json_message, JSONObject json_data) {
		Session oSession = AritubeVideoCollection.getSession(AritubeVideoCollection.getOid(uid));
		double distance = DistanceUtil.getDistance(uid);
		json_data.put("distance", distance);
		json_message.put("data", Base64.getBase64(json_data.toString()));
		SendMsgUtil.transpondMsg(oSession, Base64.getBase64(json_message.toString()));

	}

	@Override
	public void match_candidate(String uid, String message) {
		String oid = AritubeVideoCollection.getOid(uid);
		Session oSession = AritubeVideoCollection.getSession(oid);
		SendMsgUtil.transpondMsg(oSession, message);

	}

	@Override
	public void send_gift(String uid, JSONObject json_data) {
		String coins = json_data.getString("coins");
		String gift_type = json_data.getString("gift_type");
		String time = json_data.getString("time");
		String oid = AritubeVideoCollection.getOid(uid);
		String consumption = userAccountService.consumption(uid, "send_gift", Integer.parseInt(coins));
		// 收到礼物方 扣除百分40金币
		if (null != oid) {
			String add_balance = userAccountService.add_balance(oid, "receive_gift",
					(int) (Integer.parseInt(coins) * 0.6));
			JSONObject data1 = new JSONObject();
			data1.put("gift_type", gift_type);
			data1.put("time", time);
			data1.put("u_blance", add_balance);
			SendMsgUtil.sendMsg(AritubeVideoCollection.getSession(oid), "send_gift", data1.toString());
		}
		JSONObject data2 = new JSONObject();
		data2.put("gift_type", gift_type);
		data2.put("time", time);
		data2.put("u_blance", consumption);
		SendMsgUtil.sendMsg(AritubeVideoCollection.getSession(uid), "send_gift", data2.toString());
		System.out.println("send_gift");
		LogUtil.printAritubeCollections();
	}

}
