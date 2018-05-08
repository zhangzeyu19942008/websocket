package com.hk.business.websocket.entity;

public class ClientCondition {
	private String filter_sex;

	public String getFilter_sex() {
		return filter_sex;
	}

	public void setFilter_sex(String filter_sex) {
		this.filter_sex = filter_sex;
	}

	@Override
	public String toString() {
		return "ClientCondition [filter_sex=" + filter_sex + "]";
	}
	
}
