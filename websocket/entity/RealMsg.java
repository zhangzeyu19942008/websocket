package com.hk.business.websocket.entity;

public class RealMsg {
	private String uid;
	private String other_uid;
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getOther_uid() {
		return other_uid;
	}
	public void setOther_uid(String other_uid) {
		this.other_uid = other_uid;
	}
	@Override
	public String toString() {
		return "RealMsg [uid=" + uid + ", other_uid=" + other_uid + "]";
	}
	
}
