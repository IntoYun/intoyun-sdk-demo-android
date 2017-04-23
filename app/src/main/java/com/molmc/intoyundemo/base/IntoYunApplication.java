package com.molmc.intoyundemo.base;

import android.app.Application;

import com.molmc.intoyunsdk.openapi.IntoYunSdk;
import com.molmc.intoyunsdk.utils.IntoYunSharedPrefs;

/**
 * features:
 * Author：  hhe on 16-7-28 11:29
 * Email：   hhe@molmc.com
 */

public class IntoYunApplication extends Application {

	private String appId = "94574c9fb4e8d4a74471c988c788eabf";
	private String appSecret = "ba1b4c6e14c94d3c57d8e298ff6a7ca6";

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
