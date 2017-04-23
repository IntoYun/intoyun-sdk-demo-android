
### 导入SDK
在项目(**app mudule**)的build.gradle文件中添加：

```
dependencies {
	compile 'com.molmc.intoyun:intoyunsdk:0.1.0'
}
```

### 配置AndroidManifest.xml
在AndroidManifest.xml文件里添加如下权限

```
 <!-- 添加权限 -->
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
```

### 在代码中初始化SDK
（1）使用SDK之前，需要先初始化SDK，在Application中初始化SDK

```
public class IntoYunApplication extends Application {
	private String appId = "94574c9fb4e8d4a74471c988c788eabf";
	private String appSecret = "ba1b4c6e14c94d3c57d8e298ff6a7ca6";
	private static MyApplication instance;
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		//初始化SDK
		IntoYunSdk.init(getApplicationContext(), appId, appSecret);
		//打印调试信息
		IntoYunSdk.openLog(true);
	}
	public static MyApplication getInstance(){
		return instance;
	}
}
```
**【注意事项】**

* **appId** 即在开发平台上创建应用时生成的**应用ID**，**appSecret**为**appId**对应的**应用密钥**；
* **IntoYunSdk.openLog(true)** 方法设置打印调试信息，默认为false，不打印调试信息。

(2) Session管理
IntoYun SDK初始化的时候默认会从服务器上获取一次App Token，App Token用于授权AppId 访问IntoYun平台权限，有效期为30天。
用户也可以通过下面接口手动刷新App Token，示例代码如下。

```
IntoYunSdk.getAppToken(new IntoYunListener<AppTokenResult>() {
	@Override
	public void onSuccess(AppTokenResult result) {
		//result 为token数据
	}

	@Override
	public void onFail(NetError error) {
		//错误信息
	}
});
```
**【注意事项】**

* SDK初始化后，如果用户需要读取App Token信息，可以通过以下代码获取。

```
IntoYunSharedPrefs.getAppToken(context);
```

* 一旦出现App Token过期，需要用户手动调用该接口刷新App Token。
