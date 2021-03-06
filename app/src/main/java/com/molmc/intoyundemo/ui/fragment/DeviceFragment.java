package com.molmc.intoyundemo.ui.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.bean.FragmentArgs;
import com.molmc.intoyundemo.support.db.DataPointDataBase;
import com.molmc.intoyundemo.support.eventbus.DataPointEvent;
import com.molmc.intoyundemo.support.views.OnChangeListener;
import com.molmc.intoyundemo.support.views.WidgetBool;
import com.molmc.intoyundemo.support.views.WidgetCustom;
import com.molmc.intoyundemo.support.views.WidgetEnum;
import com.molmc.intoyundemo.support.views.WidgetExtra;
import com.molmc.intoyundemo.support.views.WidgetFloat;
import com.molmc.intoyundemo.support.views.WidgetString;
import com.molmc.intoyundemo.ui.activity.BaseActivity;
import com.molmc.intoyundemo.ui.activity.FragmentCommonActivity;
import com.molmc.intoyundemo.utils.Constant;
import com.molmc.intoyunsdk.bean.DataPointBean;
import com.molmc.intoyunsdk.bean.DeviceBean;
import com.molmc.intoyunsdk.bean.ProductBean;
import com.molmc.intoyunsdk.mqtt.PublishListener;
import com.molmc.intoyunsdk.mqtt.ReceiveListener;
import com.molmc.intoyunsdk.network.IntoYunListener;
import com.molmc.intoyunsdk.network.NetError;
import com.molmc.intoyunsdk.openapi.DataProtocol;
import com.molmc.intoyunsdk.openapi.IntoYunSdk;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.molmc.intoyundemo.utils.Constant.BOOL_DT;
import static com.molmc.intoyundemo.utils.Constant.DF_CUSTOM;
import static com.molmc.intoyundemo.utils.Constant.DF_TLV;
import static com.molmc.intoyundemo.utils.Constant.ENUM_DT;
import static com.molmc.intoyundemo.utils.Constant.EXTRA_DT;
import static com.molmc.intoyundemo.utils.Constant.NUMBER_DT;
import static com.molmc.intoyundemo.utils.Constant.STRING_DT;
import static com.molmc.intoyunsdk.openapi.Constant.TCP_WS_RECEIVE_META_ACTION;
import static com.molmc.intoyunsdk.openapi.Constant.TCP_WS_RECEIVE_SMS_ACTION;
import static com.molmc.intoyunsdk.tcpws.TcpwsProtocol.SEND_META;
import static com.molmc.intoyunsdk.tcpws.TcpwsProtocol.SEND_SMS;

/**
 * features: 设备展示界面
 * Author：  hhe on 16-8-3 16:51
 * Email：   hhe@molmc.com
 */

public class DeviceFragment extends BaseFragment implements OnChangeListener,
                                    ReceiveListener, ViewTreeObserver.OnGlobalLayoutListener {

    public static void launch(Activity from, DeviceBean deviceBean) {
        FragmentArgs args = new FragmentArgs();
        args.add("deviceBean", deviceBean);
        FragmentCommonActivity.launch(from, DeviceFragment.class, args);
    }

    @Bind(R.id.dataContainer)
    LinearLayout dataContainer;
    @Bind(R.id.scrollView)
    ScrollView mScrollView;
    @Bind(R.id.layoutEmpty)
    RelativeLayout layEmpty;

    private DeviceBean deviceBean;
    private List<DataPointBean> dataPoints;
    private LocalBroadcastReceiver mLocalBroadcastReceiver;
    private ProductBean product;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        if (getArguments() != null) {
            deviceBean = (DeviceBean) getArguments().get("deviceBean");
            dataPoints = DataPointDataBase.getInstance(getActivity()).getDataPoints(deviceBean.getPidImp());
        }
        deviceBean.setAccessMode(IntoYunSdk.boardToName(deviceBean.getBoard()));
        IntoYunSdk.getProductById(deviceBean.getPidImp(), new IntoYunListener<ProductBean>() {
            @Override
            public void onSuccess(ProductBean result) {
                product = result;
                dataPoints = result.getDatapoints();
                setDeviceWidget();
            }

            @Override
            public void onFail(NetError error) {
                showToast(R.string.err_data_point_not_found);
                layEmpty.setVisibility(View.VISIBLE);
            }
        });
        BaseActivity baseActivity = (BaseActivity) getActivity();
        baseActivity.getSupportActionBar().setTitle(deviceBean.getName());
        setHasOptionsMenu(false);
        IntoYunSdk.subscribeDataFromDevice(deviceBean, dataPoints, this);
        mScrollView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        //实例化广播接收机
        mLocalBroadcastReceiver = new LocalBroadcastReceiver();
        //设置设置意图过滤
        IntentFilter intentFilter = new IntentFilter(TCP_WS_RECEIVE_SMS_ACTION);
        intentFilter.addAction(TCP_WS_RECEIVE_META_ACTION);
        //注册广播接收
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mLocalBroadcastReceiver, intentFilter);
    }

    private void setDeviceWidget() {
        if (DF_CUSTOM.equals(product.getDataformat())){
            layEmpty.setVisibility(View.GONE);
            WidgetCustom view = new WidgetCustom(getContext());
            view.initData(this);
            dataContainer.addView(view);
        } else if (DF_TLV.equals(product.getDataformat())) {
            if (dataPoints != null && dataPoints.size() > 0) {
                layEmpty.setVisibility(View.GONE);
                for (DataPointBean dataPoint : dataPoints) {
                    switch (dataPoint.getType()) {
                        case NUMBER_DT: {
                            WidgetFloat view = new WidgetFloat(getContext());
                            view.initData(dataPoint, this);
                            dataContainer.addView(view);
                            break;
                        }
                        case BOOL_DT: {
                            WidgetBool view = new WidgetBool(getContext());
                            view.initData(dataPoint, this);
                            dataContainer.addView(view);
                            break;
                        }
                        case STRING_DT: {
                            WidgetString view = new WidgetString(getContext());
                            view.initData(dataPoint, this);
                            dataContainer.addView(view);
                            break;
                        }
                        case EXTRA_DT: {
                            WidgetExtra view = new WidgetExtra(getContext());
                            view.initData(dataPoint, this);
                            dataContainer.addView(view);
                            break;
                        }
                        case ENUM_DT: {
                            WidgetEnum view = new WidgetEnum(getActivity());
                            view.initData(dataPoint, this);
                            dataContainer.addView(view);
                            break;
                        }
                    }
                }
            } else {
                layEmpty.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mScrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        IntoYunSdk.unSubscribeDataFromDevice(deviceBean);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mLocalBroadcastReceiver);
    }

    @Override
    public void onChanged(Object payload, DataPointBean dataPoint, String dataFormat) {
        Logger.i(String.valueOf(payload));
        if (DF_CUSTOM.equals(product.getDataformat())){
            IntoYunSdk.sendDataToCustomDevice(deviceBean, ((String) payload).getBytes(), new PublishListener() {
                @Override
                public void onSuccess(String topic) {

                }

                @Override
                public void onFailed(String topic, String errMsg) {

                }
            });
        } else if (DF_TLV.equals(product.getDataformat())) {
            IntoYunSdk.sendDataToDevice(deviceBean, payload, dataPoint, new PublishListener() {
                @Override
                public void onSuccess(String topic) {

                }

                @Override
                public void onFailed(String topic, String errMsg) {
                    showToast(errMsg);
                }
            });
        }
    }

    @Override
    public void onReceive(String topic, byte[] message) {
        String result = new String(message);
        Logger.i(result);
        DataPointEvent dataPointEvent = new DataPointEvent();
        if (DF_CUSTOM.equals(product.getDataformat())){ //自定义数据格式
            dataPointEvent.setResult(result);
        } else if (DF_TLV.equals(product.getDataformat())){ //tlv数据点格式
            Map<Integer, Object> map = new Gson().fromJson(result, new TypeToken<Map<Integer, Object>>() {
            }.getType());
            dataPointEvent.setPayload(map);
        }
        EventBus.getDefault().post(dataPointEvent);
    }

    @Override
    public void onFailed(NetError error) {

    }

    @Override
    public void onSuccess(String topic) {
        IntoYunSdk.getDeviceStatus(deviceBean);
    }


    @Override
    public void onGlobalLayout() {
        //比较根布局与当前布局的大小
        int heightDiff = mScrollView.getRootView().getHeight() - mScrollView.getHeight();
        if (heightDiff > 100) {
            //大小超过100时，一般为显示虚拟键盘事件
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //将ScrollView滚动到底
//                    if (mScrollView != null) mScrollView.fullScroll(View.FOCUS_DOWN);
                    EventBus.getDefault().post(Constant.EVENT_SCROLL);
                }
            }, 100);
        } else {
            //大小小于100时，为不显示虚拟键盘或虚拟键盘隐藏
        }
    }

    class LocalBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int type = intent.getIntExtra("type", -1);
            String deviceId = intent.getStringExtra("deviceId");
            if (type == SEND_SMS) {
                byte[] msg = intent.getByteArrayExtra("msg");
                Logger.i("type: " + type + "\ndeviceId: " + deviceId + "\nmsg: " + new String(msg));

                //tlv数据解码成json格式数据
                String result = new String(DataProtocol.decode(msg, dataPoints));

                Logger.i(result);

                DataPointEvent dataPointEvent = new DataPointEvent();
                if (DF_CUSTOM.equals(product.getDataformat())){//自定义数据格式
                    dataPointEvent.setResult(result);
                } else if (DF_TLV.equals(product.getDataformat())){ //tlv数据点格式
                    Map<Integer, Object> map = new Gson().fromJson(result, new TypeToken<Map<Integer, Object>>() {
                    }.getType());
                    dataPointEvent.setPayload(map);
                }
                //更新界面数据
                EventBus.getDefault().post(dataPointEvent);
            } else if (type == SEND_META) {
                String msg = intent.getStringExtra("msg");
                Logger.i("type: " + type + "\ndeviceId: " + deviceId + "\nmsg: " + msg);
            }
        }
    }

}
