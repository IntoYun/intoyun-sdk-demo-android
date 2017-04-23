package com.molmc.intoyundemo.bean;

/**
 * features: 传感器类型
 * Author：  hhe on 16-8-5 10:48
 * Email：   hhe@molmc.com
 */

public enum SensorType {

	CMD("cmd"), //控制类型
	DATA("data");   //数据采集了类型

	private String sensorType;
	SensorType(String sensorType){
		this.sensorType = sensorType;
	}

	public String getSensorType() {
		return sensorType;
	}

	public void setSensorType(String sensorType) {
		this.sensorType = sensorType;
	}
}
