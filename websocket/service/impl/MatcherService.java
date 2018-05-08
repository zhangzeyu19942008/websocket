package com.hk.business.websocket.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.hk.business.entity.RecordVideo;
import com.hk.business.service.MatchStrategyService;
import com.hk.business.service.RecordVideoService;
import com.hk.business.websocket.entity.AritubeVideoCollection;
import com.hk.business.websocket.entity.FakerMsg;
import com.hk.business.websocket.entity.RealMsg;
import com.hk.business.websocket.entity.WaitInfo;
import com.hk.business.websocket.service.IMatcherService;
import com.hk.business.websocket.util.MatchHistoryUtil;

@Component("matcherService")
@Transactional
public class MatcherService implements IMatcherService {
	@Autowired
	RecordVideoService recordVideoService;
	@Autowired
	MatchStrategyService matchStrategyService;

	@Override
	public Object match(String uid, WaitInfo waitInfo) {
		Long addTime = waitInfo.getUserInfo().getAddTime();
		String match_realOrFaker = waitInfo.getUserCondition().getTrueOrFaker();
		// 如果超时 或者cms控制 匹配假视频
		if ((waitTimeout(addTime) || "0".equals(match_realOrFaker))
				&& "1".equals(matchStrategyService.get(1).getIsfakerOpen())) {
			System.out.println("当前正在匹配假视频用户uid :"+uid+"--waitInfo"+waitInfo);
			return match_faker(uid, waitInfo);
		} else {
			System.out.println("当前正在匹配真实用户uid :"+uid+"--waitInfo"+waitInfo);
			return match_true(uid, waitInfo);
		}
	}

	@Override
	public RealMsg match_true(String uid, WaitInfo waitInfo) {
		Map<String, WaitInfo> waitMap = AritubeVideoCollection.getwaitMap();
		String self_sex = waitInfo.getUserInfo().getSex();
		String filter_sex = waitInfo.getUserCondition().getFilter_sex();
		Set<String> filter_trueHistory = waitInfo.getUserCondition().getFilter_trueHistory();
		RealMsg realMsg = null;
		for (Entry<String, WaitInfo> entry : waitMap.entrySet()) {
			// 如果匹配到自己
			if (uid.equals(entry.getKey())) {
				continue;
			}

			// 如果自己的性别 和要筛选的性别对应 并过滤匹配历史
			System.out.println(filter_trueHistory);
			if (!filter_trueHistory.contains(entry.getKey())
					&& (filter_sex.equals(entry.getValue().getUserInfo().getSex())
							&& entry.getValue().getUserCondition().getFilter_sex().equals(self_sex)
							|| filter_sex.equals(entry.getValue().getUserInfo().getSex())
									&& entry.getValue().getUserCondition().getFilter_sex().equals("0")
							|| filter_sex.equals("0")
									&& entry.getValue().getUserCondition().getFilter_sex().equals(self_sex)
							|| filter_sex.equals("0")
									&& entry.getValue().getUserCondition().getFilter_sex().equals("0"))) {
				// 记录匹配历史

				MatchHistoryUtil.recordRealHistory(uid, entry.getKey());

				// 记录过滤历史
				MatchHistoryUtil.recordFilterReal(uid, entry.getKey());

				// 从等待集合中删除

				AritubeVideoCollection.remove_wait(uid);
				AritubeVideoCollection.remove_wait(entry.getKey());

				// 加入房间

				AritubeVideoCollection.createRoom(uid, entry.getKey());

				realMsg = new RealMsg();
				realMsg.setUid(uid);
				realMsg.setOther_uid(entry.getKey());
				System.out.println("真人匹配成功 uid_uid---"+uid+"_"+entry.getKey());

				break;

			}
		}
		return realMsg;

	}

	@Override
	public FakerMsg match_faker(String uid, WaitInfo waitInfo) {
		// List<RecordVideo> recordVideos = null;
		// 过滤的性别
		String filter_sex = waitInfo.getUserCondition().getFilter_sex();
		// 过滤的假视频
		Set<String> filter_fakerHistory = waitInfo.getUserCondition().getFilter_fakerHistory();

		String quality = waitInfo.getUserCondition().getIsHiVideo();
		StringBuilder param = new StringBuilder("");
		if (filter_fakerHistory.size() == 0) {
			param.append("0");
		}
		/*
		 * for (int i = 0; i < filter_fakerHistory.size(); i++) { // 如果不是最后一个 if
		 * (filter_fakerHistory.size() != i+1) {
		 * param.append(filter_fakerHistory.get(i)).append(","); } else {
		 * param.append(filter_fakerHistory.get(i)); } }
		 */
		else {
			param.append(filter_fakerHistory.toString().substring(1, filter_fakerHistory.toString().length() - 1));
		}
		if("0".equals(filter_sex)){
			filter_sex = "1 or obj.sex = 2";
		}
		// 是否是高质量
		System.out.println("select obj from RecordVideo obj where ( obj.sex = " + filter_sex + ") and obj.isHiVideo = "
				+ quality + " and obj.id not in (" + param + ")");
		List<RecordVideo> recordVideos = recordVideoService.queryHQL("select obj from RecordVideo obj where ( obj.sex = "
				+ filter_sex + ") and obj.isHiVideo = " + quality + " and obj.id not in (" + param + ")");
		// 边界情况
		if (recordVideos.size() == 0) {
			//recordVideos = recordVideoService.findAll();
			return null;
		}

		Random random = new Random();
		int nextInt = random.nextInt(recordVideos.size());
		RecordVideo recordVideo = recordVideos.get(nextInt);
		FakerMsg fakerMsg = new FakerMsg();
		fakerMsg.setUid(uid);
		fakerMsg.setRecordVideo(recordVideo);

		// 记录匹配历史
		MatchHistoryUtil.recordFakerHistory(uid, recordVideo);
		// 记录过滤历史
		MatchHistoryUtil.recordFilterfaker(uid, recordVideo.getId().toString());

		// 从等待集合中移除
		AritubeVideoCollection.remove_wait(uid);

		System.out.println("假数据匹配成功 当前在线人数:" + AritubeVideoCollection.getSessionsSize() + "  uid:" + uid);
		System.out.println("假数据匹配成功 当前等待集合中:" + AritubeVideoCollection.getwaitSize() + "  uid:" + uid);
		System.out.println("假数据匹配成功 当前房间集合人数:" + AritubeVideoCollection.getRoomSize() + "  uid:" + uid);
		return fakerMsg;

	}

	private boolean waitTimeout(Long addTime) {
		// TODO Auto-generated method stub
		return (new Date().getTime() - addTime) / 1000 > 5 ? true : false;
	}
}
