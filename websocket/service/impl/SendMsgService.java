package com.hk.business.websocket.service.impl;

import java.util.Random;
import java.util.UUID;

import javax.websocket.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.hk.business.entity.RecordVideo;
import com.hk.business.service.UserAccountService;
import com.hk.business.websocket.entity.AritubeVideoCollection;
import com.hk.business.websocket.entity.FakerMsg;
import com.hk.business.websocket.entity.RealMsg;
import com.hk.business.websocket.service.ISendMsgService;
import com.hk.business.websocket.util.DistanceUtil;
import com.hk.util.Base64;

import net.sf.json.JSONObject;

@Component("sendMsgService")
@Transactional
public class SendMsgService implements ISendMsgService {
	
	//匹配倒计时 默认15秒
	private static final String TIME = "15";
	
	@Autowired
	UserAccountService userAccountService;
	@Override
	public void sendMsg(Object obj) {
		if (obj instanceof FakerMsg) {

			sendFakerMsg((FakerMsg) obj);
		}
		if (obj instanceof RealMsg) {
			sendTrueMsg((RealMsg) obj);
		}

	}

	@Override
	public void sendFakerMsg(FakerMsg fakerMsg) {
		String uid = fakerMsg.getUid();
		RecordVideo recordVideo = fakerMsg.getRecordVideo();
		Session session = AritubeVideoCollection.getSession(uid);
		JSONObject msg = new JSONObject();
		msg.put("type", "match_faker");
		JSONObject data = new JSONObject();
		data.put("url", recordVideo.getVideo_file() == null ? "" : recordVideo.getVideo_file());
		data.put("u_age", recordVideo.getAge() == null ? "" : recordVideo.getAge().toString());
		data.put("u_img", recordVideo.getVideo_img() == null ? "" : recordVideo.getVideo_img());
		data.put("u_nickname", recordVideo.getUsername() == null ? "" : recordVideo.getUsername());
		data.put("u_sex", recordVideo.getSex() == null ? "" : recordVideo.getSex());
		data.put("u_nation", recordVideo.getNation() == null ? "" : recordVideo.getNation());
		data.put("u_nationFlag", recordVideo.getNationFlag() == null ? "" : recordVideo.getNationFlag());
		
		data.put("time", TIME);
		data.put("language", "a,b");
		//生成500-2000之间随机数
		String distance = String.valueOf((int) (500+Math.random()*1500));
		data.put("distance", distance);
		msg.put("data", Base64.getBase64(data.toString()));
		msg.put("videoInfo", "");
		if (null != session && session.isOpen()) {
			System.out.println("假视频数据" + msg.toString());
			session.getAsyncRemote().sendText(Base64.getBase64(msg.toString()));
		}

	}

	@Override
	public void sendTrueMsg(RealMsg realMsg) {
		String uid = realMsg.getUid();
		String other_uid = realMsg.getOther_uid();
		Session session = AritubeVideoCollection.getSession(uid);
		Session oSession = AritubeVideoCollection.getSession(other_uid);
		
		//查询距离  
		String distance = String.valueOf(DistanceUtil.getDistance(uid));
		//生成房间号
		//String room = AritubeVideoCollection.getRoom(uid, other_uid);
		String room = UUID.randomUUID().toString();
		
		
		//封装一个人信息
		JSONObject msg1 = new JSONObject();
		JSONObject json_data1 = JSONObject.fromObject(userAccountService.getMatchSuccessFriend(other_uid));
		json_data1.put("distance", distance);
		json_data1.put("room", room);
		
		json_data1.put("time", TIME);
		json_data1.put("language", "a,b");

		msg1.put("type", "match_start");
		msg1.put("data", Base64.getBase64(json_data1.toString()));
		msg1.put("videoInfo", "");
		if (null != session && session.isOpen()) {
			session.getAsyncRemote().sendText(Base64.getBase64(msg1.toString()));
		}
		
		//封装另一个人信息
		
		JSONObject msg2 = new JSONObject();
		JSONObject json_data2 = JSONObject.fromObject(userAccountService.getMatchSuccessFriend(uid));
		json_data2.put("distance", distance);
		json_data2.put("room", room);
		json_data2.put("time", TIME);
		json_data2.put("language", "a,b");

		msg2.put("type", "match_start");
		msg2.put("data", Base64.getBase64(json_data2.toString()));
		msg2.put("videoInfo", "");
		if (null != oSession && oSession.isOpen()) {
			oSession.getAsyncRemote().sendText(Base64.getBase64(msg2.toString()));
		}
		System.out.println("真人匹配成功");
		System.out.println(msg2.toString());
		
	}

}
