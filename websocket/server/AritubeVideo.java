package com.hk.business.websocket.server;

import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.hk.business.entity.RecordVideo;
import com.hk.business.entity.UserAccount;
import com.hk.business.service.RecordVideoService;
import com.hk.business.service.UserAccountService;
import com.hk.util.Base64;
import com.hk.util.LocationUtils;
import com.hk.util.RedisUtil;
import com.hk.util.SpringContextsUtil;

import net.sf.json.JSONObject;
import redis.clients.jedis.Jedis;

/**
 * @version1.0
 * 
 * @author zzy
 *
 */
//@ServerEndpoint("/aritube.video/{token}")
public class AritubeVideo {
	private static final Map<String, Session> uid_session = Collections.synchronizedMap(new HashMap<String, Session>());
	private static final Map<String, Map<String, String>> waitsId_sex = Collections
			.synchronizedMap(new HashMap<String, Map<String, String>>());
	private static final Map<String, String> uid_uid = Collections.synchronizedMap(new HashMap<String, String>());
	static UserAccountService userAccountService = (UserAccountService) SpringContextsUtil
			.getBean("userAccountService");
	static RecordVideoService recordVideoService = (RecordVideoService) SpringContextsUtil
			.getBean("recordVideoService");
	/*@Autowired
	static UserAccountService userAccountService;
	@Autowired
	static RecordVideoService recordVideoService;*/
	private static final String RANDOM = "0";
	private static final String BOY = "1";
	private static final String GIRL = "2";

	@OnOpen
	public void onOpen(Session session, @PathParam("token") String token) {
		System.out.println(token);
		Jedis jedis = RedisUtil.getJedis();
		String uid = jedis.get(token);
		RedisUtil.returnResource(jedis);
		uid_session.put(uid, session);
		System.out.println("有用户加入app 当前在线人数:" + uid_session.size() + "  uid:" + uid);
		System.out.println("有用户加入app 当前等待集合中:" + waitsId_sex.size() + "  uid:" + uid);
		System.out.println("有用户加入app 当前房间集合人数:" + uid_uid.size() + "  uid:" + uid);
	}

	@OnClose
	public void onClose(Session session, CloseReason reason,@PathParam("token") String token) throws ParseException {
		Jedis jedis = RedisUtil.getJedis();
		String uid = jedis.get(token);
		RedisUtil.returnResource(jedis);
		uid_session.remove(uid);
		waitsId_sex.remove(uid);
		String oid = removeRoom(uid);
		Session osession = uid_session.get(oid);
		JSONObject json_data = new JSONObject();
		json_data.put("openid", oid);
		sendMsg(osession, "match_close", json_data.toString());
		System.out.println("有用户退出app 当前在线人数:" + uid_session.size() + "  uid:" + uid);
		System.out.println("有用户退出app 当前等待集合中:" + waitsId_sex.size() + "  uid:" + uid);
		System.out.println("有用户退出app 当前房间集合人数:" + uid_uid.size() + "  uid:" + uid);
		System.out.println("close becauseof " + reason);
	}

	@OnError
	public void onError(Session session, Throwable error, @PathParam("token") String token) {
		// String uid = RedisUtil.getJedis().get(token);
		System.out.println("error because of");
		error.printStackTrace();

	}

	@OnMessage
	public void onMessage(String message, Session session, @PathParam("token") String token) throws Exception {
		Jedis jedis = RedisUtil.getJedis();
		String uid = jedis.get(token);
		RedisUtil.returnResource(jedis);
		JSONObject json_message = JSONObject.fromObject(Base64.getFromBase64(message));
		String string = json_message.getString("data");
		System.out.println(string);
		JSONObject json_data = JSONObject.fromObject(Base64.getFromBase64(json_message.getString("data")));
		String type = json_message.getString("type");
		System.out.println(type + "=======================================" + uid);
		String oid = getOid(uid);
		Session oSession = uid_session.get(oid);
		JSONObject data = new JSONObject();
		switch (type) {
		case "offer":
			String location1 = RedisUtil.getValue("location_" + uid);
			String location2 = RedisUtil.getValue("location_" + oid);
			if (StringUtils.isNotBlank(location1) && StringUtils.isNotBlank(location2)) {
				double distance = LocationUtils.getDistance(location1, location2);
				json_data.put("distance", distance);
			} else {
				json_data.put("distance", 0d);	
			}
			json_message.put("data",Base64.getBase64(json_data.toString()));
			transpondMsg(oSession, Base64.getBase64(json_message.toString()));
			break;
		case "answer":
			String location3 = RedisUtil.getValue("location_" + uid);
			String location4 = RedisUtil.getValue("location_" + oid);
			if (StringUtils.isNotBlank(location3) && StringUtils.isNotBlank(location4)) {
				double distance = LocationUtils.getDistance(location3, location4);
				json_data.put("distance", distance);
			} else {
				json_data.put("distance", "");	
			}
			json_message.put("data",Base64.getBase64(json_data.toString()));
			transpondMsg(oSession, Base64.getBase64(json_message.toString()));
			break;
		case "candidate":
			transpondMsg(oSession, new String(message.getBytes("utf-8")));
			break;
		default:
			switch (type) {
			case "match_start":
				String sex = json_data.getString("sex");
				HashMap<String, String> userInfo = new HashMap<>();
				userInfo.put("my_sex", userAccountService.getUserAccount(uid).getU_sex());
				userInfo.put("filter_sex", sex);
				waitsId_sex.put(uid, userInfo);
				if (waitsId_sex.size() >= 2) {
					match_start(session, uid);
				}
			}
			break;
		// 一方关闭通话
		case "match_close":
			waitsId_sex.remove(uid);
			removeRoom(uid);
			data = new JSONObject();
			data.put("openid", uid);
			sendMsg(oSession, "match_close", data.toString());
			break;
		// 加时间    
		/*case "match_addtime":
			String time = json_data.getString("time");
			data = new JSONObject();
			data.put("openid", uid);
			data.put("time", time);
			sendMsg(oSession, "match_addtime", data.toString());
			data = new JSONObject();
			data.put("openid", oid);
			data.put("time", time);
			sendMsg(session, "match_addtime", data.toString());
			break;*/
		// 通话时间到了
		/*case "match_timeout":
			removeRoom(uid);
			data = new JSONObject();
			data.put("openid", oid);
			sendMsg(session, "match_timeout", data.toString());
			data = new JSONObject();
			data.put("openid", uid);
			sendMsg(oSession, "match_timeout", data.toString());
			break;*/
		// 匹配不到人时候匹配假视频
		case "match_video":
			String sex = json_data.getString("sex");
			System.out.println(sex);
			if (getOid(uid) == null) {
				match_video(session, uid, sex);
			}
			break;
		case "send_gift":
			String coins = json_data.getString("coins");
			String gift_type = json_data.getString("gift_type");
			String time = json_data.getString("time");
			String consumption = userAccountService.consumption(uid, "send_gift", Integer.parseInt(coins));
			//收到礼物方 扣除百分40金币
			if(null!=oid){
			String add_balance = userAccountService.add_balance(oid, "receive_gift", (int)(Integer.parseInt(coins)*0.6));
			data = new JSONObject();
			data.put("gift_type", gift_type);
			data.put("time", time);
			data.put("u_blance", add_balance);
			sendMsg(oSession, "send_gift", data.toString());
			}
			data = new JSONObject();
			data.put("gift_type", gift_type);
			data.put("time", time);
			data.put("u_blance", consumption);
			sendMsg(session, "send_gift", data.toString());
			break;
		}
	}

	private synchronized static void match_start(Session session, String uid) {
		Map<String, String> current = waitsId_sex.get(uid);
		for (Map.Entry<String, Map<String, String>> entry : waitsId_sex.entrySet()) {
			if (entry.getKey().equals(uid)) {
				continue;
			}
			// 1.当前用户做了筛选   1.等待用户做了筛选 2.等待用户没有做筛选
			// 2.当前用户没有做了筛选  1.等待用户做了筛选 2.等待用户没有做筛选
			
			if (current.get("filter_sex").equals(entry.getValue().get("my_sex"))&&entry.getValue().get("filter_sex").equals(current.get("my_sex"))
				||current.get("filter_sex").equals(entry.getValue().get("my_sex"))&&entry.getValue().get("filter_sex").equals(RANDOM)
				||current.get("filter_sex").equals(RANDOM)&&entry.getValue().get("filter_sex").equals(current.get("my_sex"))
				||current.get("filter_sex").equals(RANDOM)&&entry.getValue().get("filter_sex").equals(RANDOM)
			) {
				uid_uid.put(uid, entry.getKey());
				waitsId_sex.remove(uid);
				waitsId_sex.remove(entry.getKey());
				JSONObject json_data = new JSONObject();
				json_data.put("openid", entry.getKey());
				sendMsg(session, "match_start", json_data.toString());
				//添加匹配历史记录
				Jedis jedis = RedisUtil.getJedis();
				Long count1 = jedis.llen("friendList_"+uid);
				Long count2 = jedis.llen("friendList_"+entry.getKey());
				//如果匹配历史列表为100个 删除尾部
				if(count1==100){
					jedis.rpop("friendList_"+uid);
				}
				if(count2==100){
					jedis.rpop("friendList_"+entry.getKey());
				}
				UserAccount user1 = userAccountService.getUserAccount(uid);
				UserAccount user2 = userAccountService.getUserAccount(entry.getKey());
				//分别存放对方的信息 添加到各自的匹配历史列表
				JSONObject json_user1 = new JSONObject();
				json_user1.put("u_nickname", user1.getU_nickname());
				json_user1.put("u_nationalFlag", user1.getU_nationalFlag());
				json_user1.put("u_img", user1.getU_img());
				json_user1.put("u_nation", user1.getU_nation());
				json_user1.put("u_uid", user1.getU_uid());
				//如果该用户匹配历史存在  删除后再重新添加
				jedis.lrem("friendList_"+entry.getKey(), 0, json_user1.toString());
				
				jedis.lpush("friendList_"+entry.getKey(), json_user1.toString());
				
				JSONObject json_user2 = new JSONObject();
				json_user2.put("u_nickname", user2.getU_nickname());
				json_user2.put("u_nationalFlag", user2.getU_nationalFlag());
				json_user2.put("u_img", user2.getU_img());
				json_user2.put("u_nation", user2.getU_nation());
				json_user2.put("u_uid", user2.getU_uid());
				
				jedis.lrem("friendList_"+uid, 0, json_user2.toString());
				
				jedis.lpush("friendList_"+uid, json_user2.toString());
				
				RedisUtil.returnResource(jedis);
				break;
			}
		}
	}

	/**
	 * 发送给客户端消息的方法
	 * 
	 * @param session
	 * @param type
	 * @param data
	 */
	private static void sendMsg(Session session, String type, String data) {
		JSONObject msg = new JSONObject();
		msg.put("type", type);
		msg.put("data", Base64.getBase64(data));
		msg.put("videoInfo", "");
		if (null != session && session.isOpen()) {
			session.getAsyncRemote().sendText(Base64.getBase64(msg.toString()));
			System.out.println("send_gift:    "+msg.toString());
		}
	}

	/**
	 * 删除房间
	 * 
	 * @param uid
	 *            根据uid删除房间
	 * @return 返回房间中另一个用户的uid
	 */
	private String removeRoom(String uid) {
		if (hasLength(uid)) {
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

	private synchronized static void match_video(Session session, String uid, String sex) {
		List<RecordVideo> recordVideos;
		if (sex.equals(RANDOM)) {
			recordVideos = recordVideoService.findAll();
		} else {
			recordVideos = recordVideoService.queryDataBySex(sex);
		}
		Random random = new Random();
		int nextInt = random.nextInt(recordVideos.size() - 1);
		RecordVideo recordVideo = recordVideos.get(nextInt);
		waitsId_sex.remove(uid);
		JSONObject msg = new JSONObject();
		msg.put("type", "match_faker");
		JSONObject data = new JSONObject();
		data.put("url", recordVideo.getVideo_file()==null?"":recordVideo.getVideo_file());
		data.put("age", recordVideo.getAge()==null?"": recordVideo.getAge().toString());
		data.put("img", recordVideo.getVideo_img()==null?"":recordVideo.getVideo_img());
		data.put("nickname", recordVideo.getUsername()==null?"":recordVideo.getUsername());
		data.put("sex", recordVideo.getSex()==null?"":recordVideo.getSex());
		data.put("nation", recordVideo.getNation()==null?"":recordVideo.getNation());
		data.put("nationFlag", recordVideo.getNationFlag()==null?"":recordVideo.getNationFlag());
		data.put("distance", "0.45");
		msg.put("data", Base64.getBase64(data.toString()));
		msg.put("videoInfo", "");
		if (null != session && session.isOpen()) {
			System.out.println("假视频数据"+msg.toString());
			session.getAsyncRemote().sendText(Base64.getBase64(msg.toString()));
		}
		System.out.println("假数据匹配成功 当前在线人数:" + uid_session.size() + "  uid:" + uid);
		System.out.println("假数据匹配成功 当前等待集合中:" + waitsId_sex.size() + "  uid:" + uid);
		System.out.println("假数据匹配成功 当前房间集合人数:" + uid_uid.size() + "  uid:" + uid);
	}

	/**
	 * 转发type为offer,answer,candidate消息方法
	 * 
	 * @param session
	 * @param message
	 */
	private void transpondMsg(Session session, String message) {
		if (session != null && session.isOpen()) {
			session.getAsyncRemote().sendText(message);
		}
	}

	/**
	 * 通过自己的uid 找到同一个房间里对方的uid
	 * 
	 * @param uid
	 *            自己的uid
	 * @return 返回对方的uid 如果没有返回null
	 */
	private static String getOid(String uid) {
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

	private static boolean hasLength(String str) {
		return null != str && !"".equals(str.trim());
	}

	public static void main(String[] args) {
	}
}
