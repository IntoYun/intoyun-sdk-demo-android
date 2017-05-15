package com.molmc.intoyundemo.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.molmc.intoyundemo.R;
import com.molmc.intoyunsdk.network.model.response.UserResult;
import com.molmc.intoyunsdk.openapi.IntoYunSdk;
import com.molmc.intoyunsdk.utils.IntoYunSharedPrefs;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hehui on 17/3/21.
 */

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        UserResult result = IntoYunSharedPrefs.getUserInfo(this);
        if (result != null && !TextUtils.isEmpty(result.getUid())){
            IntoYunSdk.createConnection(result.getToken(), result.getUid());
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    MainActivity.launch(SplashActivity.this);
                    SplashActivity.this.finish();
                }
            }, 2000);
        } else {
            LoginActivity.launch(this);
            this.finish();
        }
    }
}
