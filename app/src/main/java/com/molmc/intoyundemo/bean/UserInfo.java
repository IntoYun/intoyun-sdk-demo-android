package com.molmc.intoyundemo.bean;

/**
 * features:
 * Author：  hhe on 16-8-6 15:00
 * Email：   hhe@molmc.com
 */

public class UserInfo {
	private String phone;
	private String email;
	private String username;
	private String description;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "UserInfo{" +
				"description='" + description + '\'' +
				", phone='" + phone + '\'' +
				", email='" + email + '\'' +
				", username='" + username + '\'' +
				'}';
	}
}
