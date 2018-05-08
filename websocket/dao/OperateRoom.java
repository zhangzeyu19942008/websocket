package com.hk.business.websocket.dao;

import java.util.Iterator;
import java.util.Set;

import com.hk.util.RedisUtil;

import redis.clients.jedis.Jedis;

/**
 * 操作redis里面Set
 * 
 * @author win10
 *
 */
public class OperateRoom {
	public static final String ROOM_SET_KEY = "room";

	/**
	 * 加入到房间集合 房间集合元素用 token_token 表示
	 */
	public static void add(String token, String o_token) {
		String room = token + "_" + o_token;
		RedisUtil.getJedis().sadd(ROOM_SET_KEY, room);
	}

	/**
	 * 得到对方的token
	 */
	public static String get_otoken(String token) {
		Jedis jedis = RedisUtil.getJedis();
		Set<String> rooms = jedis.smembers(ROOM_SET_KEY);
		Iterator<String> iterator = rooms.iterator();
		while (iterator.hasNext()) {
			String room = iterator.next();
			String[] tokens = room.split("_");
			if (tokens[0].equals(token)) {
				return tokens[1];
			}
			if (tokens[1].equals(token)) {
				return tokens[0];
			}
		}
		return "no_otoken";
	}

	/**
	 * 根据token 删除房间
	 * 返回 房间里面另一个人的token
	 */
	public static String removeRoom(String token) {
		Jedis jedis = RedisUtil.getJedis();
		Set<String> rooms = jedis.smembers(ROOM_SET_KEY);
		Iterator<String> iterator = rooms.iterator();
		while (iterator.hasNext()) {
			String room = iterator.next();
			String[] tokens = room.split("_");
			if (tokens[0].equals(token)) {
				jedis.srem(ROOM_SET_KEY, room);
				return tokens[1];
			}
			if (tokens[1].equals(token)) {
				jedis.srem(ROOM_SET_KEY, room);
				return tokens[0];
			}
		}
		return "no_room";
	}
}
