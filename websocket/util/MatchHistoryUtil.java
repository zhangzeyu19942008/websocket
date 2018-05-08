package com.hk.business.websocket.util;

import com.hk.business.entity.RecordVideo;
import com.hk.business.entity.UserAccount;
import com.hk.business.service.RecordVideoService;
import com.hk.business.service.UserAccountService;
import com.hk.util.RedisUtil;
import com.hk.util.SpringContextsUtil;

import net.sf.json.JSONObject;
import redis.clients.jedis.Jedis;

public class MatchHistoryUtil {
	private static final UserAccountService userAccountService = (UserAccountService) SpringContextsUtil
			.getBean("userAccountService");
		public static void recordRealHistory(String uid ,String oid){
			//添加匹配历史记录
			Jedis jedis = RedisUtil.getJedis();
			Long count1 = jedis.llen("friendList_"+uid);
			Long count2 = jedis.llen("friendList_"+oid);
			//如果匹配历史列表为100个 删除尾部
			if(count1==100){
				jedis.rpop("friendList_"+uid);
			}
			if(count2==100){
				jedis.rpop("friendList_"+oid);
			}
			UserAccount user1 = userAccountService.getUserAccount(uid);
			UserAccount user2 = userAccountService.getUserAccount(oid);
			//分别存放对方的信息 添加到各自的匹配历史列表
			JSONObject json_user1 = new JSONObject();
			json_user1.put("u_nickname", user1.getU_nickname());
			json_user1.put("u_nationalFlag", user1.getU_nationalFlag());
			json_user1.put("u_img", user1.getU_img());
			json_user1.put("u_nation", user1.getU_nation());
			json_user1.put("u_uid", user1.getU_uid());
			json_user1.put("isRobot", "0");
			//如果该用户匹配历史存在  删除后再重新添加
			jedis.lrem("friendList_"+oid, 0, json_user1.toString());
			
			jedis.lpush("friendList_"+oid, json_user1.toString());
			
			JSONObject json_user2 = new JSONObject();
			json_user2.put("u_nickname", user2.getU_nickname());
			json_user2.put("u_nationalFlag", user2.getU_nationalFlag());
			json_user2.put("u_img", user2.getU_img());
			json_user2.put("u_nation", user2.getU_nation());
			json_user2.put("u_uid", user2.getU_uid());
			json_user2.put("isRobot", "0");
			
			jedis.lrem("friendList_"+uid, 0, json_user2.toString());
			
			jedis.lpush("friendList_"+uid, json_user2.toString());
			RedisUtil.returnResource(jedis);
		}
		public static void recordFilterReal(String uid, String other_uid) {
			Jedis jedis = RedisUtil.getJedis();
			//jedis.lpush("filter_true_"+uid, other_uid);
			//jedis.lpush("filter_true_"+other_uid, uid);
			
			jedis.sadd("filter_true_"+uid, other_uid);
			jedis.sadd("filter_true_"+other_uid, uid);
			
			//设置过期时间为一天
			jedis.expire("filter_true_"+uid, 60*60*24);
			jedis.expire("filter_true_"+other_uid, 60*60*24);
			
			RedisUtil.returnResource(jedis);
			
		}
		public static void recordFilterfaker(String uid, String recordId) {
			Jedis jedis = RedisUtil.getJedis();
			//jedis.lpush("filter_faker_"+uid, recordId);
			jedis.sadd("filter_faker_"+uid, recordId);
			//设置过期时间为一天
			jedis.expire("filter_faker_"+uid, 60*60*24);
			
			RedisUtil.returnResource(jedis);
			
			
			
		}
		public static void recordFakerHistory(String uid,RecordVideo recordVideo){
			//添加匹配历史记录
			Jedis jedis = RedisUtil.getJedis();
			Long count1 = jedis.llen("friendList_"+uid);
			//如果匹配历史列表为100个 删除尾部
			if(count1==100){
				jedis.rpop("friendList_"+uid);
			}
			//分别存放对方的信息 添加到各自的匹配历史列表
			JSONObject json_recordVideo = new JSONObject();
			json_recordVideo.put("u_nickname", recordVideo.getUsername());
			json_recordVideo.put("u_nationalFlag",recordVideo.getNationFlag());
			json_recordVideo.put("u_img", recordVideo.getVideo_img());
			json_recordVideo.put("u_nation", recordVideo.getNation());
			json_recordVideo.put("u_uid", recordVideo.getUid());
			json_recordVideo.put("isRobot", "1");
			//如果该用户匹配历史存在  删除后再重新添加
			jedis.lrem("friendList_"+uid, 0, json_recordVideo.toString());
			
			jedis.lpush("friendList_"+uid, json_recordVideo.toString());
			
			RedisUtil.returnResource(jedis);
		}
}
