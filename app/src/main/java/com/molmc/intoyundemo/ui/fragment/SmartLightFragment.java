package com.molmc.intoyundemo.ui.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.bean.FragmentArgs;
import com.molmc.intoyundemo.support.db.DataPointDataBase;
import com.molmc.intoyundemo.ui.activity.BaseActivity;
import com.molmc.intoyundemo.ui.activity.FragmentCommonActivity;
import com.molmc.intoyundemo.utils.AppSharedPref;
import com.molmc.intoyunsdk.bean.DataPointBean;
import com.molmc.intoyunsdk.bean.DeviceBean;
import com.molmc.intoyunsdk.mqtt.PublishListener;
import com.molmc.intoyunsdk.mqtt.ReceiveListener;
import com.molmc.intoyunsdk.network.IntoYunListener;
import com.molmc.intoyunsdk.network.NetError;
import com.molmc.intoyunsdk.openapi.IntoYunSdk;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.zjy.actionsheet.ActionSheet;

/**
 * Created by hehui on 17/6/3.
 */

public class SmartLightFragment extends BaseFragment implements ReceiveListener, PublishListener, SeekBar.OnSeekBarChangeListener {


    public static void launch(Activity from, DeviceBean deviceBean) {
        FragmentArgs args = new FragmentArgs();
        args.add("deviceBean", deviceBean);
        FragmentCommonActivity.launch(from, SmartLightFragment.class, args);
    }

    @Bind(R.id.dataContainer)
    RelativeLayout dataContainer;
    @Bind(R.id.scrollView)
    ScrollView mScrollView;
    @Bind(R.id.layoutLightness)
    RelativeLayout layoutLightness;
    @Bind(R.id.smartLight)
    ImageView smartLight;
    @Bind(R.id.smartLightBg)
    ImageView smartLightBg;
    @Bind(R.id.smartLightSwitch)
    CheckBox smartLightSwitch;
    @Bind(R.id.lightnessSeekBar)
    SeekBar lightnessSeekBar;
    @Bind(R.id.btnColor)
    Button btnColor;
    @Bind(R.id.btnMode)
    Button btnMode;
    @Bind(R.id.btnLightness)
    Button btnLightness;


    private DeviceBean deviceBean;
    private List<DataPointBean> dataPoints;
    private Boolean showLightness;
    private Boolean showModeGrid;
    private Boolean lightSwitch;
    private int colorValue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_smartlight, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        if (getArguments() != null) {
            deviceBean = (DeviceBean) getArguments().get("deviceBean");
            dataPoints = DataPointDataBase.getInstance(getActivity()).getDataPoints(deviceBean.getPidImp());
        }
        showLightness = false;
        lightSwitch = false;
        showModeGrid = false;
        colorValue = 0xffffffff;
        deviceBean.setAccessMode(AppSharedPref.getInstance(getActivity()).getBoarInfo(deviceBean.getBoard()).getAccessMode());
        BaseActivity baseActivity = (BaseActivity) getActivity();
        baseActivity.getSupportActionBar().setTitle(deviceBean.getName());
        setHasOptionsMenu(false);
        setDeviceWidget();
        lightnessSeekBar.setOnSeekBarChangeListener(this);
        IntoYunSdk.subscribeDataFromDevice(deviceBean, dataPoints, this);
    }

    private void setDeviceWidget() {
        lightnessSeekBar.setMax(dataPoints.get(2).getMax());
    }

    @Override
    public void onReceive(String topic, byte[] message) {
        String result = new String(message);
        Logger.i(result);
        Map<Integer, Object> map = new Gson().fromJson(result, new TypeToken<Map<Integer, Object>>() {
        }.getType());
        for (Integer integer : map.keySet()) {
            if (integer == 1) {
                boolean value = (boolean) map.get(integer);
                setLightSwitch(value);
            } else if(integer == 2){
                boolean value = (boolean) map.get(integer);
                smartLightSwitch.setChecked(value);
                lightSwitch = value;
            } else if (integer == 3) {
                float value = Float.parseFloat(String.valueOf(map.get(integer)));
                lightnessSeekBar.setProgress(Math.round(value));
            } else if (integer == 4) {
                float value = Float.parseFloat(String.valueOf(map.get(integer)));
                Logger.i("receive:{" + integer + ": " + value + "};");
                colorValue = (int) value;
            } else if (integer == 5) {
                double value = (double) map.get(integer);
                Logger.i("receive:{" + integer + ": " + value + "};");
            }
        }
    }

    @Override
    public void onFailed(NetError error) {
        showToast(error.getMsg());
    }

    @Override
    public void onSuccess(String topic) {
        IntoYunSdk.getDeviceStatus(deviceBean);
    }

    @Override
    public void onFailed(String topic, String errMsg) {
        showToast(errMsg);
    }


    private void sendData(Object payload, DataPointBean dataPoint) {
        if (deviceBean.getAccessMode().equals("LoRa")) {
            IntoYunSdk.sendCmdToDevice(payload, deviceBean, dataPoint, new IntoYunListener() {
                @Override
                public void onSuccess(Object result) {

                }

                @Override
                public void onFail(NetError error) {
                    showToast(error.getMessage());
                }
            });
        } else {
            IntoYunSdk.sendDataToDevice(deviceBean, payload, dataPoint, new PublishListener() {
                @Override
                public void onSuccess(String topic) {
                    Logger.i("publish success");
                }

                @Override
                public void onFailed(String topic, String errMsg) {
                    showToast(errMsg);
                }
            });
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        IntoYunSdk.unSubscribeDataFromDevice(deviceBean);
    }

    @OnClick({R.id.smartLightSwitch, R.id.btnColor, R.id.btnMode, R.id.btnLightness})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.smartLightSwitch:
                switchChange(smartLightSwitch.isChecked());
                break;
            case R.id.btnColor:
                if (!lightSwitch) {
                    return;
                }
                showColorPicker();
                break;
            case R.id.btnMode:
                if (!lightSwitch) {
                    return;
                }
                toggleModeGrid();
                break;
            case R.id.btnLightness:
                if (!lightSwitch) {
                    return;
                }
                toggleLightness();
                break;
        }
    }

    private void switchChange(boolean status) {
        setLightSwitch(status);
        sendData(status, dataPoints.get(1));
    }

    private void setLightSwitch(boolean status) {
        if (status) {
            smartLight.setImageResource(R.drawable.smart_light_on);
            smartLightBg.setVisibility(View.VISIBLE);
        } else {
            smartLight.setImageResource(R.drawable.smart_light_off);
            smartLightBg.setVisibility(View.INVISIBLE);
            layoutLightness.setVisibility(View.INVISIBLE);
        }
        lightSwitch = status;
    }


    private void showColorPicker() {
        ColorPickerDialogBuilder
                .with(getActivity())
                .setTitle(R.string.color_dialog_title)
                .initialColor((0xff000000 | colorValue))
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorChangedListener(new OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int selectedColor) {
                        // Handle on color change
                        Logger.i("onColorChanged: 0x" + Integer.toHexString(selectedColor));
                    }
                })
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        Logger.i("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                        colorValue = (selectedColor & 0x00FFFFFF);
                        sendData(colorValue, dataPoints.get(3));
                    }
                })
                .setPositiveButton(getString(R.string.confirm), new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        if (allColors != null) {
                            StringBuilder sb = null;

                            for (Integer color : allColors) {
                                if (color == null)
                                    continue;
                                if (sb == null)
                                    sb = new StringBuilder("Color List:");
                                sb.append("\r\n#" + Integer.toHexString(color).toUpperCase());
                            }

                            if (sb != null) {
                                Logger.i("onColorConfirm: 0x" + Integer.toHexString(selectedColor));
                                Logger.i("onColorConfirm:" + (selectedColor & 0x00FFFFFF));
                            }
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .showColorEdit(false)
                .showAlphaSlider(false)
                .showLightnessSlider(false)
                .setColorEditTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary))
                .build()
                .show();
    }


    private void toggleLightness() {
        if (showLightness) {
            layoutLightness.setVisibility(View.VISIBLE);
        } else {
            layoutLightness.setVisibility(View.INVISIBLE);
        }
        showLightness = !showLightness;
    }

    private void toggleModeGrid() {
        showActionSheet(dataPoints.get(4));
        showModeGrid = !showModeGrid;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Logger.i("stop seek bar: " + seekBar.getProgress());
        sendData(seekBar.getProgress(), dataPoints.get(2));
    }

    private void showActionSheet(final DataPointBean dataPoint) {
        List<?> list = dataPoint.get_enum();
        int size = list.size();
        String[] xEnum = new String[size];
        int[] colors = new int[size];
        for (int i = 0; i < size; i++) {
            xEnum[i] = String.valueOf(list.get(i));
            colors[i] = getResources().getColor(R.color.colorPrimary);
        }


        ActionSheet actionSheet = new ActionSheet.Builder()
//				.setTitle("Title", Color.BLUE)
                //.setTitleTextSize(20)
                .setOtherBtn(xEnum, colors)
                //.setOtherBtnTextSize(30)
//				.setOtherBtnSub(new String[]{null, "Btn1 sub", ""}, new int[]{Color.BLACK, Color.BLUE, Color.GREEN})
                //.setOtherBtnSubTextSize(20)
                .setCancelBtn(getResources().getString(R.string.cancel), Color.RED)
                //.setCancelBtnTextSize(30)
                .setCancelableOnTouchOutside(true)
                .setActionSheetListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isByBtn) {
                    }

                    @Override
                    public void onButtonClicked(ActionSheet actionSheet, int index) {
                        Logger.i("index: " + index);
                        if (index < dataPoint.get_enum().size()) {
                            sendData(index, dataPoint);
                        }
                    }
                }).build();

        actionSheet.show(getActivity().getFragmentManager());
    }
}
