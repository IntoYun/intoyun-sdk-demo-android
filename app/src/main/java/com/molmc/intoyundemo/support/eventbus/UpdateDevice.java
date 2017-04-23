package com.molmc.intoyundemo.support.eventbus;

import com.molmc.intoyunsdk.bean.DeviceBean;

/**
 * features: eventbus 更新设备信息
 * Author：  hhe on 16-8-25 14:25
 * Email：   hhe@molmc.com
 */

public class UpdateDevice {
	private DeviceBean mDeviceBean;

	public UpdateDevice(){

	};

	public UpdateDevice(DeviceBean deviceBean) {
		mDeviceBean = deviceBean;
	}

	public DeviceBean getDeviceBean() {
		return mDeviceBean;
	}

	public void setDeviceBean(DeviceBean deviceBean) {
		mDeviceBean = deviceBean;
	}
}
