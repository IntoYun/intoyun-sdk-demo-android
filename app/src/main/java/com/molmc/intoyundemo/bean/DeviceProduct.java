package com.molmc.intoyundemo.bean;

import com.molmc.intoyunsdk.bean.DataPointBean;
import com.molmc.intoyunsdk.bean.DeviceBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * features: 设备产品
 * Author：  hhe on 16-8-10 17:46
 * Email：   hhe@molmc.com
 */

public class DeviceProduct implements Serializable{
	private static final long serialVersionUID = 7792658277322442433L;
	private DeviceBean device;
	private ArrayList<DataPointBean> product;

	public DeviceBean getDevice() {
		return device;
	}

	public void setDevice(DeviceBean device) {
		this.device = device;
	}

	public List<DataPointBean> getProduct() {
		return product;
	}

	public void setProduct(ArrayList<DataPointBean> product) {
		this.product = product;
	}

	@Override
	public String toString() {
		return "Device{" +
				"device=" + device +
				", product=" + product +
				'}';
	}
}
