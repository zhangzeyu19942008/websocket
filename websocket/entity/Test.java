package com.hk.business.websocket.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.hk.util.StringtoALL;

public class Test {
	public static void main(String[] args) {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String nextDate = sdf.format(d);
		System.out.println(new StringtoALL().toHexString(nextDate + "aritubeToken"));
	}
	}

