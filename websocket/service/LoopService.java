package com.hk.business.websocket.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.hk.business.service.DebugSwitchService;
import com.hk.business.service.RecordVideoService;
import com.hk.business.websocket.entity.AritubeVideoCollection;
import com.hk.business.websocket.entity.WaitInfo;
import com.hk.business.websocket.service.impl.MatcherService;
import com.hk.business.websocket.service.impl.SendMsgService;

//创建房间线程 每200ms 循环一次
public class LoopService extends Thread {
	private static final String RANDOM = "0";
	private static final String BOY = "1";
	private static final String GIRL = "2";
	@Autowired
	RecordVideoService recordVideoService;
	@Autowired
	DebugSwitchService debugSwitchService;
	@Autowired
	MatcherService matcherService;
	@Autowired
	SendMsgService sendMsgService;

	public void loopStart() {
		this.start();
	}

	@Override
	public void run() {
		while (true) {
			System.out.println("匹配线程 正在匹配");
			Map<String, WaitInfo> waitMap = AritubeVideoCollection.getwaitMap();
			for (Map.Entry<String, WaitInfo> wait : waitMap.entrySet()) {
				Object msg = matcherService.match(wait.getKey(),wait.getValue());
				//判断是那种消息类型
				sendMsgService.sendMsg(msg);
				
				//此处跳出循环 不然报错
				break;
				
			}
			
			try {
				//匹配线程 每600毫秒匹配一次
				Thread.sleep(600);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
