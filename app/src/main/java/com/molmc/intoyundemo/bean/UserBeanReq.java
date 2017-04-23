package com.molmc.intoyundemo.bean;

/**
 * features:
 * Author：  hhe on 16-8-6 14:02
 * Email：   hhe@molmc.com
 */

public class UserBeanReq {
	private String email;
	private String username;
	private String password;
	private String vldCode;
	private String phone;
	private String zone;
	private String type;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getVldCode() {
		return vldCode;
	}

	public void setVldCode(String vldCode) {
		this.vldCode = vldCode;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "UserTokenBeanReq{" +
				"email='" + email + '\'' +
				", username='" + username + '\'' +
				", password='" + password + '\'' +
				", vldCode='" + vldCode + '\'' +
				", phone='" + phone + '\'' +
				", zone='" + zone + '\'' +
				", type='" + type + '\'' +
				'}';
	}
}
