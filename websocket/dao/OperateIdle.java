package com.hk.business.websocket.dao;

import com.hk.util.RedisUtil;

import net.sf.json.JSONObject;
import redis.clients.jedis.Jedis;
/**
 * 操作Redis里面List
 * @author win10
 *
 */
public class OperateIdle {
	public static final String IDLE_SET_KEY = "idle";

	/**
	 * 放入等待集合头部中
	 * @param info
	 *           
	 * @return 列表的总数
	 */
	public static int put(String token) {
		Long lpush = RedisUtil.getJedis().lpush(IDLE_SET_KEY, token);
		return lpush.intValue();
	}

   /**
    * 移除等待集合中的指定元素
    * @param info
    * @return 被移除的元素数量
    */
	public static int removeEle(String token) {
		Long lrem = RedisUtil.getJedis().lrem(IDLE_SET_KEY, 0, token);
		return lrem.intValue();
	}
	/**
	 * 从等待集合的尾部移除数据,放入到头部
	 * @param info
	 * @return 列表长度
	 */
	public static int tailToHead() {
		Jedis jedis = RedisUtil.getJedis();
		Long lpush = jedis.lpush(IDLE_SET_KEY, jedis.rpop(IDLE_SET_KEY));
		return lpush.intValue();
	}
	/**
	 * 返回尾部元素
	 * @param info
	 * @return 列表长度
	 */
	public static String getTail() {
	    return  RedisUtil.getJedis().lindex(IDLE_SET_KEY, -1);
	}
	/**
	 * 返回等待列表长度
	 */
	public static int getLength(){
		Long llen = RedisUtil.getJedis().llen(IDLE_SET_KEY);
		return llen.intValue();
	}
	/**
	 * 删除尾部元素
	 * 返回被删除的尾部元素
	 */
	public static String removeTail(){
		return RedisUtil.getJedis().rpop(IDLE_SET_KEY);
	}
}
