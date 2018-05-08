package com.hk.business.websocket.util;

import org.apache.commons.lang3.StringUtils;

import com.hk.business.websocket.entity.AritubeVideoCollection;
import com.hk.util.LocationUtils;
import com.hk.util.RedisUtil;

public class DistanceUtil {
	public static double getDistance(String uid) {
		String location1 = RedisUtil.getValue("location_" + uid);
		String location2 = RedisUtil.getValue("location_" + AritubeVideoCollection.getOid(uid));
		if (StringUtils.isNotBlank(location1) && StringUtils.isNotBlank(location2)) {
			return LocationUtils.getDistance(location1, location2);
		} else {
			return 1.23D;
		}
	}
}
