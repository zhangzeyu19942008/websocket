package com.hk.business.websocket.service;

import java.util.Map;

import com.hk.business.entity.MatchStrategy;
import com.hk.business.websocket.entity.ClientCondition;
import com.hk.business.websocket.entity.UserCondition;

public interface IUserConditionService {
	UserCondition getCondition(String uid,MatchStrategy strategy,ClientCondition clientCondition);
}
