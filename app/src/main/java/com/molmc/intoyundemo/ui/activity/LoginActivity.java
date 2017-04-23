package com.molmc.intoyundemo.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.molmc.intoyunsdk.network.IntoYunListener;
import com.molmc.intoyunsdk.network.NetError;
import com.molmc.intoyunsdk.network.model.response.AppTokenResult;
import com.molmc.intoyunsdk.network.model.response.UserResult;
import com.molmc.intoyunsdk.openapi.IntoYunSdk;
import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.utils.AppSharedPref;
import com.molmc.intoyundemo.utils.DialogUtil;
import com.orhanobut.logger.Logger;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * features: 用户登录
 * Author：  hhe on 16-7-27 22:27
 * Email：   hhe@molmc.com
 */
public class LoginActivity extends BaseActivity {

	public static void launch(Activity from) {
		Intent intent = new Intent(from, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		from.startActivity(intent);
	}

	@Bind(R.id.editAccount)
	EditText editAccount;
	@Bind(R.id.editPassword)
	EditText editPassword;
	@Bind(R.id.btnRegister)
	Button btnLogin;
	@Bind(R.id.toolbar)
	Toolbar toolbar;
	@Bind(R.id.btnToRegister)
	TextView btnToRegister;
	@Bind(R.id.btnToForgetPwd)
	TextView btnToForgetPwd;


	private String account;
	private String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ButterKnife.bind(this);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle(R.string.login);
		account = AppSharedPref.getInstance(this).getUserAccount();
		password = AppSharedPref.getInstance(this).getUserPassword();
		if (!TextUtils.isEmpty(account)) {
			editAccount.setText(account);
			editAccount.setSelection(account.length());
		}
		if (!TextUtils.isEmpty(password)) {
			editPassword.setText(password);
		}
	}

	private void requestAppToken() {
		IntoYunSdk.getAppToken(new IntoYunListener<AppTokenResult>() {
			@Override
			public void onSuccess(AppTokenResult result) {

			}

			@Override
			public void onFail(NetError error) {

			}
		});
	}

	private void userLogin() {
		account = editAccount.getText().toString().trim();
		password = editPassword.getText().toString().trim();
		if (TextUtils.isEmpty(account)) {
			showToast(R.string.err_account_empty);
			return;
		}
		if (TextUtils.isEmpty(password)) {
			showToast(R.string.err_password_empty);
			return;
		}
		DialogUtil.createProgressDialog(this, R.string.loading).show();

		IntoYunSdk.userLogin(account, password, new IntoYunListener<UserResult>() {
			@Override
			public void onSuccess(UserResult result) {
				Logger.i("onSuccess: " + new Gson().toJson(result));
				DialogUtil.dismissProgressDialog();
				AppSharedPref.getInstance(LoginActivity.this).saveUserAccount(account);
				AppSharedPref.getInstance(LoginActivity.this).saveUserPassword(password);
				MainActivity.launch(LoginActivity.this);
				finish();
			}

			@Override
			public void onFail(NetError exception) {
				DialogUtil.dismissProgressDialog();
				Logger.i(exception.getCode() + " : " + exception.getMessage());
				showToast(exception.getMessage());
			}
		});
	}


	@OnClick({R.id.btnRegister, R.id.btnToRegister, R.id.btnToForgetPwd})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btnRegister:
				userLogin();
				break;
			case R.id.btnToRegister:
				RegisterActivity.launch(this);
				break;
			case R.id.btnToForgetPwd:
				ResetPwdActivity.launch(this);
				break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
