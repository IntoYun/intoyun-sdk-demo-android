package com.molmc.intoyundemo.bean;

/**
 * features: 传感器数据类型
 * Author：  hhe on 16-8-5 10:50
 * Email：   hhe@molmc.com
 */

public enum DataType {

	ENUM("enum"),   //枚举数据类型
	FLOAT("float"),     //数值类型(浮点)
	INTEGER("int"),     //数值类型(整型)
	STRING("string"),   //字符窜类型
	BOOL("bool");       //布尔类型


	private String dataType;

	DataType(String dataType){
		this.dataType = dataType;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
}
