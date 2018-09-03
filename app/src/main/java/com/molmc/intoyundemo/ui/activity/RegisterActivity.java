package com.molmc.intoyundemo.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.molmc.intoyundemo.utils.Utils;
import com.molmc.intoyunsdk.network.IntoYunListener;
import com.molmc.intoyunsdk.network.NetError;
import com.molmc.intoyunsdk.network.model.BaseModel;
import com.molmc.intoyunsdk.network.model.request.RegisterReq;
import com.molmc.intoyunsdk.network.model.response.UserResult;
import com.molmc.intoyunsdk.openapi.IntoYunSdk;
import com.molmc.intoyundemo.R;
import com.molmc.intoyunsdk.utils.IntoUtil;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.internal.Util;

/**
 * features: 用户注册
 * Author：  hhe on 16-7-28 12:01
 * Email：   hhe@molmc.com
 */

public class RegisterActivity extends BaseActivity {

    public static void launch(Activity from) {
        Intent intent = new Intent(from, RegisterActivity.class);
        from.startActivity(intent);
    }

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.editUsername)
    EditText editUsername;
    @Bind(R.id.editAccount)
    EditText editAccount;
    @Bind(R.id.editPassword)
    EditText editPassword;
    @Bind(R.id.editVerifyCode)
    EditText editVerifyCode;
    @Bind(R.id.btnRegister)
    Button btnRegister;
    @Bind(R.id.btnVldCode)
    Button btnVldCode;

    //用户名
    private String userName;
    //帐号
    private String account;
    //密码
    private String password;
    //验证码
    private String vldCode;

    private int countTime;
    private Timer mTimer;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1: {
                    String str = String.format(getString(R.string.resend_sms_count), msg.arg1);
                    btnVldCode.setText(str);
                    btnVldCode.setTextColor(getResources().getColor(R.color.white));
                    break;
                }
                case 2: {
                    btnVldCode.setEnabled(true);
                    btnVldCode.setText(R.string.resend_sms);
                    btnVldCode.setTextColor(getResources().getColor(R.color.white));
                    if (mTimer != null) {
                        mTimer.cancel();
                        mTimer = null;
                    }
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.register);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OnClick({R.id.btnVldCode, R.id.btnRegister})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnVldCode:
                checkAccountRegistered();
//                getVerifyCode();
                break;
            case R.id.btnRegister:
                register();
                break;
        }
    }

    // 检查账号是否已注册
    private void checkAccountRegistered(){
        account = editAccount.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            showToast(R.string.err_account_empty);
            return;
        }
        IntoYunSdk.checkAccountRegistered(account, IntoYunSdk.AccountType.PHONE, new IntoYunListener<BaseModel>() {
            @Override
            public void onSuccess(BaseModel result) {
                getVerifyCode();
            }

            @Override
            public void onFail(NetError error) {
                showToast(error.getMessage());
            }
        });
    }

    /**
     * 获取验证码,可以是手机帐号也可以时邮箱帐号，用户根据需求自行设计界面，本app已手机注册为例
     * IntoYunSdk.AccountType.PHONE 为手机注册，获取手机验证码
     * IntoYunSdk.AccountType.EMAIL 为邮箱注册，获取邮箱验证码
     */
    private void getVerifyCode() {
        IntoYunSdk.getVerifyCode(account, IntoYunSdk.AccountType.PHONE, new IntoYunListener() {

            @Override
            public void onSuccess(Object result) {
                showToast(R.string.suc_phone_vldcode);
                btnVldCode.setEnabled(false);
                TimerTask(90);
            }

            @Override
            public void onFail(NetError exception) {
                btnVldCode.setText(R.string.get_verify_code);
                showToast(exception.getMessage());
                if (mTimer != null) {
                    mTimer.cancel();
                }
            }
        });
    }


    /**
     * 注册
     * 用户可以根据需求选择手机注册或邮箱注册，或者两种都兼容，开发者在此修改代码
     *
     */
    private void register() {
        account = editAccount.getText().toString().trim();
        password = editPassword.getText().toString().trim();
        vldCode = editVerifyCode.getText().toString().trim();

        if (TextUtils.isEmpty(account)) {
            showToast(R.string.err_account_empty);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showToast(R.string.err_password_empty);
            return;
        }
        if (TextUtils.isEmpty(vldCode)) {
            showToast(R.string.err_vldcode_empty);
            return;
        }
        if (password.length() < 6) {
            showToast(R.string.err_password_length_error);
            return;
        }

        RegisterReq registerReq = new RegisterReq();
        if (IntoUtil.isEmail(account)){
            registerReq.setEmail(account);
        } else if (IntoUtil.isPhone(account)){
            registerReq.setPhone(account);
        }else {
            showToast(R.string.err_account_format);
            return;
        }
        // 用户名默认和帐号相同，开发者可以修改成更符合自己需求的字符串
        registerReq.setUsername(account);

        registerReq.setPassword(password);
        registerReq.setVldCode(vldCode);
        IntoYunSdk.registerAccount(registerReq, new IntoYunListener<UserResult>() {
            @Override
            public void onSuccess(UserResult result) {
                showToast(R.string.suc_register_account);
                finish();
            }

            @Override
            public void onFail(NetError exception) {
                showToast(exception.getMessage());
                mHandler.sendEmptyMessage(2);
                if (mTimer != null) {
                    mTimer.cancel();
                }
            }
        });
    }


    private void TimerTask(int sec) {
        countTime = sec;
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                countTime--;
                if (countTime <= 0) {
                    mHandler.sendEmptyMessage(2);
                } else {
                    Message msg = mHandler.obtainMessage();
                    msg.what = 1;
                    msg.arg1 = countTime;
                    mHandler.sendMessage(msg);
                }

            }
        }, 0, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
        }
    }
}
