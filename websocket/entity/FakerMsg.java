package com.hk.business.websocket.entity;

import com.hk.business.entity.RecordVideo;

public class FakerMsg {
	private String uid;
	private RecordVideo recordVideo;
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public RecordVideo getRecordVideo() {
		return recordVideo;
	}
	public void setRecordVideo(RecordVideo recordVideo) {
		this.recordVideo = recordVideo;
	}
	@Override
	public String toString() {
		return "FakerMsg [uid=" + uid + ", recordVideo=" + recordVideo + "]";
	}
	
}
