package com.molmc.intoyundemo.bean;

import java.io.Serializable;

/**
 * features: 扫描二维码数据格式
 * Author：  hhe on 16-8-8 12:11
 * Email：   hhe@molmc.com
 */

public class QrDeviceBean implements Serializable {
	private static final long serialVersionUID = -7155466527789256446L;
	private String productId;
	private String deviceKey;

	public String getDeviceKey() {
		return deviceKey;
	}

	public void setDeviceKey(String deviceKey) {
		this.deviceKey = deviceKey;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	@Override
	public String toString() {
		return "QrDeviceBean{" +
				"deviceKey='" + deviceKey + '\'' +
				", productId='" + productId + '\'' +
				'}';
	}
}
