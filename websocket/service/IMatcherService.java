package com.hk.business.websocket.service;

import com.hk.business.websocket.entity.FakerMsg;
import com.hk.business.websocket.entity.RealMsg;
import com.hk.business.websocket.entity.WaitInfo;

public interface IMatcherService {

	RealMsg match_true(String uid, WaitInfo waitInfo);

	FakerMsg match_faker(String uid, WaitInfo waitInfo);

	Object match(String uid, WaitInfo waitInfo);
}
