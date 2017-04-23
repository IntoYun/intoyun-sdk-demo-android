package com.molmc.intoyundemo.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.bean.FragmentArgs;
import com.molmc.intoyundemo.ui.activity.BaseActivity;
import com.molmc.intoyundemo.ui.activity.FragmentCommonActivity;
import com.molmc.intoyunsdk.imlink.ImlinkConfig;
import com.molmc.intoyunsdk.imlink.ImlinkListener;
import com.molmc.intoyunsdk.network.NetError;
import com.skyfishjy.library.RippleBackground;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * features: 配置网络
 * Author：  hhe on 16-8-1 18:21
 * Email：   hhe@molmc.com
 */

public class ImlinkNetworkFragment extends BaseFragment implements ImlinkListener {

    public static ImlinkNetworkFragment newInstance() {
        ImlinkNetworkFragment fragment = new ImlinkNetworkFragment();
        return fragment;
    }

    public static void launch(Activity from) {
        FragmentArgs args = new FragmentArgs();
        FragmentCommonActivity.launch(from, ImlinkNetworkFragment.class, args);
    }

    @Bind(R.id.configWifiName)
    TextView configWifiName;
    @Bind(R.id.configWifiPassword)
    EditText configWifiPassword;
    @Bind(R.id.startConfig)
    TextView startConfig;
    @Bind(R.id.rippleButton)
    RippleBackground rippleButton;


    //手机当前连接的wifi ssid
    private String currentConnectWifi;

    //正在配置标志位
    private boolean isStarting = false;

    private String wifiSSid;
    private String wifiPassword;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImlinkConfig.init(getActivity(), this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_imlink, container, false);
        if (getArguments() != null) {
            initView();
            ButterKnife.bind(this, view);
            configWifiPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        startConfig.performClick();
                    }
                    return false;
                }
            });
        }
        return view;
    }

    private void initView() {
        BaseActivity baseActivity = (BaseActivity) getActivity();
        baseActivity.getSupportActionBar().setTitle(R.string.device_wifi_config);
    }

    @Override
    public void onResume() {
        super.onResume();
        currentConnectWifi = ImlinkConfig.getInstance().getSsid();
        configWifiName.setText(currentConnectWifi);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /**
     * 开始配置设备
     */
    private void startConfig() {
        if (!isStarting) {
            wifiSSid = configWifiName.getText().toString();
            wifiPassword = configWifiPassword.getText().toString();
            if (TextUtils.isEmpty(wifiSSid)) {
                showToast(R.string.err_ssid_empty);
                return;
            }
            if (TextUtils.isEmpty(wifiPassword)) {
                wifiPassword = "";
            }
            isStarting = true;
            configWifiPassword.setEnabled(false);
            rippleButton.startRippleAnimation();
            startConfig.setText(R.string.config_config_running);
            ImlinkConfig.getInstance().startConfig(wifiSSid, wifiPassword);
        } else {
            isStarting = false;
            configWifiName.setEnabled(true);
            configWifiPassword.setEnabled(true);
            startConfig.setText(R.string.config_start);
            rippleButton.stopRippleAnimation();
            finishConfig();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ImlinkConfig.getInstance().destroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        isStarting = false;
        rippleButton.stopRippleAnimation();
        configWifiPassword.setEnabled(true);
        finishConfig();
        startConfig.setText(R.string.config_start);
    }

    @OnClick(R.id.startConfig)
    public void onClick() {
        startConfig();
    }

    @Override
    public void onSuccess() {
        showToast(R.string.suc_device_config);
        rippleButton.stopRippleAnimation();
        configWifiPassword.setEnabled(true);
        isStarting = false;
        startConfig.setText(R.string.config_start);
        finishConfig();
        getActivity().finish();
    }

    @Override
    public void onFailed(NetError exception) {
        showToast(exception.getCode() + " : " + exception.getMessage());
        rippleButton.stopRippleAnimation();
        configWifiPassword.setEnabled(true);
        isStarting = false;
        startConfig.setText(R.string.config_start);
        finishConfig();
    }

    private void finishConfig() {
        ImlinkConfig.getInstance().interruptConfig();
    }
}
