package com.hk.business.websocket.entity;

import java.util.List;
import java.util.Set;

//用户匹配条件
public class UserCondition {
	//筛选性别
	private String filter_sex;
	//是否匹配真人 或假视频
	private String trueOrFaker;
	//是否匹配高质量视频
	private String isHiVideo;
	//过滤的假视频
	private Set<String> filter_trueHistory;
	//过滤的真人
	private Set<String> filter_fakerHistory;
	public String getFilter_sex() {
		return filter_sex;
	}
	public void setFilter_sex(String filter_sex) {
		this.filter_sex = filter_sex;
	}
	public String getTrueOrFaker() {
		return trueOrFaker;
	}
	public void setTrueOrFaker(String trueOrFaker) {
		this.trueOrFaker = trueOrFaker;
	}
	public String getIsHiVideo() {
		return isHiVideo;
	}
	public void setIsHiVideo(String isHiVideo) {
		this.isHiVideo = isHiVideo;
	}
	public Set<String> getFilter_trueHistory() {
		return filter_trueHistory;
	}
	public void setFilter_trueHistory(Set<String> filter_trueHistory) {
		this.filter_trueHistory = filter_trueHistory;
	}
	public Set<String> getFilter_fakerHistory() {
		return filter_fakerHistory;
	}
	public void setFilter_fakerHistory(Set<String> filter_fakerHistory) {
		this.filter_fakerHistory = filter_fakerHistory;
	}
	@Override
	public String toString() {
		return "UserCondition [filter_sex=" + filter_sex + ", trueOrFaker=" + trueOrFaker + ", isHiVideo=" + isHiVideo
				+ ", filter_trueHistory=" + filter_trueHistory + ", filter_fakerHistory=" + filter_fakerHistory + "]";
	}
	
	
	
}
