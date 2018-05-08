package com.hk.business.websocket.entity;

public class WaitInfo {
	private UserInfo userInfo;
	private UserCondition userCondition;
	public UserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	public UserCondition getUserCondition() {
		return userCondition;
	}
	public void setUserCondition(UserCondition userCondition) {
		this.userCondition = userCondition;
	}
	@Override
	public String toString() {
		return "WaitInfo [userInfo=" + userInfo + ", userCondition=" + userCondition + "]";
	}
	
}
