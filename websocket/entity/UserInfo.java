package com.hk.business.websocket.entity;

public class UserInfo {
	private String sex ;
	private Long addTime;
	
	
	
	
	
	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public Long getAddTime() {
		return addTime;
	}

	public void setAddTime(Long addTime) {
		this.addTime = addTime;
	}

	@Override
	public String toString() {
		return "UserInfo [sex=" + sex + ", addTime=" + addTime + "]";
	}
	
	
}
