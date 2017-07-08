package com.molmc.intoyundemo.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.support.adapter.DeviceAdapter;
import com.molmc.intoyundemo.support.db.DeviceDataBase;
import com.molmc.intoyundemo.support.eventbus.UpdateDevice;
import com.molmc.intoyundemo.ui.activity.LoginActivity;
import com.molmc.intoyundemo.ui.activity.QRCaptureActivity;
import com.molmc.intoyundemo.utils.DialogUtil;
import com.molmc.intoyundemo.utils.Interface;
import com.molmc.intoyundemo.utils.Utils;
import com.molmc.intoyunsdk.bean.DeviceBean;
import com.molmc.intoyunsdk.mqtt.ReceiveListener;
import com.molmc.intoyunsdk.network.IntoYunListener;
import com.molmc.intoyunsdk.network.NetError;
import com.molmc.intoyunsdk.openapi.IntoYunSdk;
import com.molmc.intoyunsdk.utils.IntoUtil;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

/**
 * features: 设备列表
 * Author：  hhe on 16-7-30 10:10
 * Email：   hhe@molmc.com
 */

public class DeviceListFragment extends BaseRefreshFragment implements IntoYunListener<List<DeviceBean>>, ReceiveListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = "DeviceListFragment";
    public static final int PERMISSION_CAMERA = 1988;

    public static DeviceListFragment newInstance() {
        DeviceListFragment fragment = new DeviceListFragment();
        return fragment;
    }


    private DeviceAdapter deviceAdapter;
    private List<DeviceBean> devices = new ArrayList<>();
    private Map<String, Boolean> deviceStatus = new HashMap<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void requestData() {
        IntoYunSdk.getDevices(this);
    }

    @Override
    public void onRefresh() {
        requestData();
    }

    @Override
    protected void doURV(UltimateRecyclerView urv) {
        deviceAdapter = new DeviceAdapter(getActivity(), new ArrayList<DeviceBean>());
        configGridLayoutManager(ultimateRecyclerView, deviceAdapter);
        ultimateRecyclerView.setHasFixedSize(true);
        ultimateRecyclerView.setDefaultOnRefreshListener(this);
        enableEmptyViewPolicy();
        ultimateRecyclerView.setAdapter(deviceAdapter);
        requestData();
    }

    @Override
    protected void onLoadmore() {

    }

    @Override
    protected void onFireRefresh() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unsubDeviceStatus();
        ButterKnife.unbind(this);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_device_add) {
            ImlinkNetworkFragment.launch(getActivity());
            return true;
        } else if (id == R.id.menu_qr_scan) {
            String requestPermission[] = new String[]{Manifest.permission.CAMERA};
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(requestPermission, PERMISSION_CAMERA);
            } else {
                QRCaptureActivity.launch(getActivity());
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CAMERA) {
            QRCaptureActivity.launch(getActivity());
        }
    }

    @Override
    public void onSuccess(List<DeviceBean> result) {
        if (!IntoUtil.Empty.check(deviceStatus.keySet()) && !IntoUtil.Empty.check(result)) {
            for (String deviceId : deviceStatus.keySet()) {
                result = setDeviceStatus(deviceId, deviceStatus.get(deviceId), result);
            }
            devices = result;
            deviceAdapter.changeData(result);
            DeviceDataBase.getInstance(getActivity()).saveDevices(devices);
            subDeviceStatus();
        } else {
            finishRefresh();
        }
    }


    @Override
    public void onFail(NetError exception) {
        finishRefresh();
        Logger.i(exception.getMessage());
        showToast(exception.getMessage());
        if (exception.getCode() == 40009) {
            DialogUtil.showConfirmDialog(getActivity(), R.string.err_tips, R.string.err_login_expire, R.string.confirm, new Interface.DialogCallback() {
                @Override
                public void onNegative() {

                }

                @Override
                public void onPositive() {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
        }
    }

    private void subDeviceStatus() {
        if (devices != null) {
            for (DeviceBean device : devices) {
                IntoYunSdk.subscribeDeviceInfo(device, this);
            }
        }
    }


    @Subscribe
    public void onEventMainThread(UpdateDevice event) {
        if (event.getDeviceBean() != null) {
            onRefresh();
        }
    }


    @Override
    public void onReceive(String topic, byte[] message) {
        String payload = new String(message);
        String deviceId = Utils.getDeviceIdFromTopic(topic);
        Logger.i("topic: " + topic + "; payload: " + payload);
        try {
            boolean status = new JSONObject(payload).getBoolean("online");
            deviceStatus.put(deviceId, status);
            devices = setDeviceStatus(deviceId, status, devices);
            deviceAdapter.changeData(devices);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private List<DeviceBean> setDeviceStatus(String deviceId, boolean status, List<DeviceBean> devices) {
        if (!IntoUtil.Empty.check(devices)) {
            for (int i = 0, size = devices.size(); i < size; i++) {
                if (devices.get(i).getDeviceId().equals(deviceId)) {
                    devices.get(i).setStatus(status);
                    break;
                }
            }
        }
        return devices;
    }


    /**
     * 取消订阅设备在线状态
     */
    private void unsubDeviceStatus() {
        if (devices == null) {
            return;
        }
        for (DeviceBean device : devices) {
            IntoYunSdk.unSubscribeDataFromDevice(device);
        }
    }


    @Override
    public void onFailed(NetError error) {

    }

    @Override
    public void onSuccess(String topic) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
