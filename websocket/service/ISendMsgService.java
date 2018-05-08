package com.hk.business.websocket.service;

import com.hk.business.websocket.entity.FakerMsg;
import com.hk.business.websocket.entity.RealMsg;

public interface ISendMsgService {
	void sendMsg(Object obj);
	void sendFakerMsg(FakerMsg fakerMsg);
	void sendTrueMsg(RealMsg realMsg);

}
