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
	private String appId = "36c125683434195b8c1ce306887daf3c";
	private String appSecret = "e3b0b621301b4e0d2e60f5f1bba2b410";

	//test intoyun
//	private String appId = "94574c9fb4e8d4a74471c988c788eabf";
//	private String appSecret = "ba1b4c6e14c94d3c57d8e298ff6a7ca6";
	
	private static IntoYunApplication instance;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		//初始化SDK
		IntoYunSdk.init(getApplicationContext(), appId, appSecret);
		//打印调试信息
		IntoYunSdk.openLog(true);

	}

	public static IntoYunApplication getInstance(){
		return instance;
	}

}
