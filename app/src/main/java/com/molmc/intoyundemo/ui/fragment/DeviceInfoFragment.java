package com.molmc.intoyundemo.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.LocalMediaLoader;
import com.luck.picture.lib.model.PictureConfig;
import com.molmc.intoyunsdk.bean.DeviceBean;
import com.molmc.intoyunsdk.network.IntoYunListener;
import com.molmc.intoyunsdk.network.NetError;
import com.molmc.intoyunsdk.network.model.request.DeviceReq;
import com.molmc.intoyunsdk.openapi.Constant;
import com.molmc.intoyunsdk.openapi.IntoYunSdk;
import com.molmc.intoyunsdk.utils.IntoUtil;
import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.bean.FragmentArgs;
import com.molmc.intoyundemo.support.eventbus.UpdateDevice;
import com.molmc.intoyundemo.ui.activity.BaseActivity;
import com.molmc.intoyundemo.ui.activity.FragmentCommonActivity;
import com.molmc.intoyundemo.utils.Utils;
import com.orhanobut.logger.Logger;
import com.yalantis.ucrop.entity.LocalMedia;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * features: 设备信息
 * Author：  hhe on 16-8-25 12:00
 * Email：   hhe@molmc.com
 */

public class DeviceInfoFragment extends BaseFragment implements View.OnClickListener {


    public static void launch(Activity from, DeviceBean device) {
        FragmentArgs args = new FragmentArgs();
        args.add("device", device);
        FragmentCommonActivity.launch(from, DeviceInfoFragment.class, args);
    }

    @Bind(R.id.deviceHead)
    ImageView deviceHead;
    @Bind(R.id.deviceName)
    EditText deviceName;
    @Bind(R.id.deviceDesc)
    EditText deviceDesc;

    private DeviceBean mDevice;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deviceinfo, container, false);
        ButterKnife.bind(this, view);
        if (getArguments().getSerializable("device") == null) {
            mDevice = (DeviceBean) savedInstanceState.getSerializable("device");
        } else {
            mDevice = (DeviceBean) getArguments().getSerializable("device");
        }
        initView();
        return view;
    }

    private void initView() {
        if (mDevice != null) {
            deviceName.setText(mDevice.getName());
            deviceDesc.setText(mDevice.getDescription());
            Glide.with(this).load(com.molmc.intoyunsdk.openapi.Constant.INTOYUN_HTTP_HOST + mDevice.getImgSrc()).placeholder(R.mipmap.ic_default_1)
                    .bitmapTransform(new RoundedCornersTransformation(getActivity(), Utils.dip2px(40), 0)).into(deviceHead);
        }
        deviceHead.setOnClickListener(this);
        BaseActivity baseActivity = (BaseActivity) getActivity();
        baseActivity.getSupportActionBar().setTitle(R.string.device_info_title);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("device", mDevice);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_save, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_save).setVisible(true);
        menu.findItem(R.id.menu_add).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_save) {
            saveDeviceInfo();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 不存设备信息
     */
    private void saveDeviceInfo() {
        final String devName = deviceName.getText().toString();
        final String devDesc = deviceDesc.getText().toString();
        if (TextUtils.isEmpty(devName)) {
            showToast(R.string.err_devname_empty);
            return;
        }
        if (TextUtils.isEmpty(devDesc)) {
            showToast(R.string.err_devdesc_empty);
            return;
        }

        if (devDesc.equals(mDevice.getDescription()) && devName.equals(mDevice.getName())) {
            return;
        }
        DeviceReq mDeviceReq = new DeviceReq();
        mDeviceReq.setDeviceId(mDevice.getDeviceId());
        mDeviceReq.setDescription(devDesc);
        mDeviceReq.setName(devName);
        IntoYunSdk.updateDeviceInfo(mDeviceReq, new IntoYunListener() {
            @Override
            public void onSuccess(Object result) {
                showToast(R.string.suc_save);
                mDevice.setName(devName);
                mDevice.setDescription(devDesc);
                EventBus.getDefault().post(new UpdateDevice(mDevice));
                getActivity().finish();

            }

            @Override
            public void onFail(NetError error) {
                showToast(error.getMessage());

            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        if (v == deviceHead) {
            FunctionConfig config = new FunctionConfig();
            config.setType(LocalMediaLoader.TYPE_IMAGE);
            config.setCopyMode(FunctionConfig.CROP_MODEL_1_1);
            config.setCompress(true);
            config.setEnableCrop(true); //是否裁剪
            config.setShowCamera(true); //是否显示相机
            config.setCropW(120); //裁剪宽
            config.setCropH(120); //裁剪高
            config.setMaxSelectNum(1);

            // 先初始化参数配置，在启动相册
            PictureConfig.init(config);
            PictureConfig.getPictureConfig().openPhoto(getActivity(), resultCallback);
        }
    }


    /**
     * 图片回调方法
     */

    private PictureConfig.OnSelectResultCallback resultCallback = new PictureConfig.OnSelectResultCallback() {

        @Override
        public void onSelectSuccess(List<LocalMedia> resultList) {
            Logger.i(new Gson().toJson(resultList));
            LocalMedia media = resultList.get(0);
            if (media.isCompressed()) {
                // 注意：如果压缩过，在上传的时候，取 media.getCompressPath();
                // 压缩图compressPath
//				if (TextUtils.isEmpty(mDevice.getImgSrc())) {
                IntoYunSdk.uploadAvatar(media.getCompressPath(), "device", mDevice.getDeviceId(), new IntoYunListener<String>() {
                    @Override
                    public void onSuccess(String result) {
                        String image = "v1/avatar/" + result + "?t=" + IntoUtil.getCurrentTimeMillis();
                        mDevice.setImgSrc(image);
                        UpdateDevice updateDevice = new UpdateDevice(mDevice);
                        EventBus.getDefault().post(updateDevice);
                        Glide.with(DeviceInfoFragment.this).load(Constant.INTOYUN_HTTP_HOST + image).placeholder(R.mipmap.ic_default_1)
                                .bitmapTransform(new RoundedCornersTransformation(getActivity(), Utils.dip2px(40), 0)).fitCenter().bitmapTransform(new RoundedCornersTransformation(getActivity(), Utils.dip2px(40), 0)).into(deviceHead);
                    }

                    @Override
                    public void onFail(NetError error) {
                        showToast(error.getMessage());
                    }
                });
//				} else {
//					final String avatarId = Utils.getAvatarId(mDevice.getImgSrc());
//					IntoYunSdk.updateAvatar(media.getCompressPath(), avatarId, "device", new IntoYunListener() {
//						@Override
//						public void onSuccess(Object result) {
//							String image = "v1/avatar/" + avatarId + "?t=" + IntoUtil.getCurrentTimeMillis();
//							mDevice.setImgSrc(image);
//							UpdateDevice updateDevice = new UpdateDevice(mDevice);
//							EventBus.getDefault().post(updateDevice);
//						}
//
//						@Override
//						public void onFail(NetError error) {
//							showToast(error.getMessage());
//						}
//					});
//				}

            } else {
                // 注意：没有压缩过，在上传的时候，取 media.getPath();
                // 原图path
                // 注意：如果media.getCatPath();不为空的话 就代表裁剪的图片，上传时可取，但是如果又压缩过，则取最终压缩过的compressPath
            }
        }
    };

}
