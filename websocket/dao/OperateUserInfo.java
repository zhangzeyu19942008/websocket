package com.hk.business.websocket.dao;

import com.hk.util.RedisUtil;

public class OperateUserInfo {
	public static final String USERINFO_SET_KEY = "userinfo";

	public static void put(String token, String userinfo) {
		RedisUtil.getJedis().hset(USERINFO_SET_KEY, token, userinfo);
	}

	public static String get(String token) {
		return RedisUtil.getJedis().hget(USERINFO_SET_KEY, token);
	}
	public static Long remove(String token) {
		return RedisUtil.getJedis().hdel(USERINFO_SET_KEY, token);
	}
}
