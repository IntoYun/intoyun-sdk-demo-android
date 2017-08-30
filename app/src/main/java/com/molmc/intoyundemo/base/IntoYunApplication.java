package com.molmc.intoyundemo.base;

import android.app.Application;

import com.molmc.intoyunsdk.openapi.IntoYunSdk;

/**
 * features:
 * Author：  hhe on 16-7-28 11:29
 * Email：   hhe@molmc.com
 */

public class IntoYunApplication extends Application {
	//intoyun
	private String appkey = "36c125683434195b8c1ce306887daf3c";
	private String appSecret = "e3b0b621301b4e0d2e60f5f1bba2b410";

	private static IntoYunApplication instance;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		/** 初始化SDK
		 * 设置设置mqtt and websocket协议
		 * 如果只使用intoyun的mqtt通讯协议，则设置protoType: PROTO_MQTT（默认）
		 * 如果只使用intoyun的tcp协议，则设置protoType: PROTO_TCP
		 * 如果只使用intoyun的websocket协议，则设置protoType: PROTO_WS
		 * 如果使用了intoyun的mqtt和tcp两种协议，则设置protoType: PROTO_MQTT_TCP
		 * 如果使用了intoyun的mqtt和websocket两种协议，则设置protoType: PROTO_MQTT_WS
		 */
		IntoYunSdk.init(getApplicationContext(), appkey, appSecret, IntoYunSdk.Protocol.mqtt_ws);
		//打印调试信息
		IntoYunSdk.openLog(true);

	}

	public static IntoYunApplication getInstance(){
		return instance;
	}

}
