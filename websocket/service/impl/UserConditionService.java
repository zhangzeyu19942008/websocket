package com.hk.business.websocket.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.hk.business.entity.MatchStrategy;
import com.hk.business.websocket.entity.ClientCondition;
import com.hk.business.websocket.entity.UserCondition;
import com.hk.business.websocket.service.IUserConditionService;
import com.hk.util.RedisUtil;

import redis.clients.jedis.Jedis;
@Component("userConditionService")
@Transactional
public class UserConditionService implements IUserConditionService {

	@Override
	public UserCondition getCondition(String uid, MatchStrategy strategy, ClientCondition clientCondition) {
		UserCondition userCondition = new UserCondition();

		// 判断过滤重复真人开关
		if ("1".equals(strategy.getIsFilterTrueOpen())) {
			Jedis jedis = RedisUtil.getJedis();
			//List<String> filter_true = jedis.lrange("filter_true_" + uid, 0, -1);
			Set<String> filter_true = jedis.smembers("filter_true_" + uid);
			RedisUtil.returnResource(jedis);
			userCondition.setFilter_trueHistory(filter_true);
		} else {
			userCondition.setFilter_trueHistory(new HashSet<>());
		}
		// 判断过滤假视频开关

		if ("1".equals(strategy.getIsFilterFakerOpen())) {
			Jedis jedis = RedisUtil.getJedis();
			//List<String> filter_faker = jedis.lrange("filter_faker_" + uid, 0, -1);
			Set <String> filter_faker = jedis.smembers("filter_faker_" + uid);
			RedisUtil.returnResource(jedis);
			userCondition.setFilter_fakerHistory(filter_faker);
		} else {
			userCondition.setFilter_fakerHistory(new HashSet<>());
		}

		// 判断后台匹配策略是否开启
		// 判断用户是否进行性别筛选
		String isFilter_sex = clientCondition.getFilter_sex();
		if ("0".equals(isFilter_sex)) {
			// 设置假视频质量为普通视频
			userCondition.setIsHiVideo("1"); // 1为普通视频
			// 判断真假视频比例开关是否打开
			if ("1".equals(strategy.getIsTrueOrFakerRatioOpen())) {
				// 判断这次要匹配 是真人还是假视频
				String trueRatio = strategy.getTrueRatio();
				String fakerRatio = strategy.getFakerRatio();
				double random1 = Math.random();
				if (random1 < Double.parseDouble(fakerRatio) /( Double.parseDouble(trueRatio)
						+ Double.parseDouble(fakerRatio))) {
					userCondition.setTrueOrFaker("0"); // faker
				} else {
					userCondition.setTrueOrFaker("1"); // true
				}
			
			}
			// 判断男女比例开关是否打开
			if ("1".equals(strategy.getIsManOrWomanRatioOpen())) {
				String manRatio = strategy.getManRatio();
				String womanRatio = strategy.getWomanRatio();
				double random2 = Math.random();
				if (random2 < Double.parseDouble(manRatio) / (Double.parseDouble(manRatio)
						+ Double.parseDouble(womanRatio))) {
					userCondition.setFilter_sex("1"); // 男
				} else {
					userCondition.setFilter_sex("2"); // 女
				}
			} else {
				// 如果开关没有打开
				userCondition.setFilter_sex(clientCondition.getFilter_sex());
			}

		} else {

			userCondition.setTrueOrFaker("1");
			userCondition.setFilter_sex(clientCondition.getFilter_sex());
			userCondition.setIsHiVideo("2"); // 匹配高质量视频

		}

		return userCondition;
	}

}
