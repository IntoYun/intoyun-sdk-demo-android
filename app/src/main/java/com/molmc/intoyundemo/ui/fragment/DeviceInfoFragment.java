package com.molmc.intoyundemo.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.bean.FragmentArgs;
import com.molmc.intoyundemo.support.eventbus.UpdateDevice;
import com.molmc.intoyundemo.ui.activity.BaseActivity;
import com.molmc.intoyundemo.ui.activity.FragmentCommonActivity;
import com.molmc.intoyundemo.utils.Utils;
import com.molmc.intoyunsdk.bean.DeviceBean;
import com.molmc.intoyunsdk.network.IntoYunListener;
import com.molmc.intoyunsdk.network.NetError;
import com.molmc.intoyunsdk.network.model.request.DeviceReq;
import com.molmc.intoyunsdk.openapi.Constant;
import com.molmc.intoyunsdk.openapi.IntoYunSdk;
import com.molmc.intoyunsdk.utils.IntoUtil;
import com.molmc.intoyunsdk.utils.IntoYunSharedPrefs;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static android.app.Activity.RESULT_OK;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

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

    private List<LocalMedia> selectList = new ArrayList<>();

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
            showDevImage(mDevice.getImgSrc());
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
            PictureSelector.create(getActivity())
                    .openGallery(PictureMimeType.ofImage())
                    .maxSelectNum(1)
                    .minSelectNum(1)
                    .imageSpanCount(4)
                    .enableCrop(true)
                    .withAspectRatio(1, 1)
                    .selectionMode(PictureConfig.SINGLE)
                    .cropWH(300, 300)
                    .compress(true)
                    .previewImage(true)
                    .isZoomAnim(true)
                    .forResult(PictureConfig.CHOOSE_REQUEST);
        }
    }


    private void showDevImage(String imgSrc){
        RequestOptions opts = new RequestOptions();
        opts.placeholder(R.mipmap.ic_default_1);
        opts.fitCenter();

        Glide.with(DeviceInfoFragment.this)
                .load(Constant.getHttpHost() + imgSrc)
                .apply(opts)
                .apply(bitmapTransform(new RoundedCornersTransformation(Utils.dip2px(40), 0)))
                .into(deviceHead);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
                    LocalMedia media = selectList.get(0);

                    if (TextUtils.isEmpty(mDevice.getImgSrc())) {
                        IntoYunSdk.uploadAvatar(media.getCompressPath(), media.getPictureType(), "device", mDevice.getDeviceId(), new IntoYunListener<String>() {
                            @Override
                            public void onSuccess(String result) {
                                String image = "v1/avatar/" + result + "?t=" + IntoUtil.getCurrentTimeMillis();
                                mDevice.setImgSrc(image);
                                UpdateDevice updateDevice = new UpdateDevice(mDevice);
                                EventBus.getDefault().post(updateDevice);
                                showDevImage(image);
                            }

                            @Override
                            public void onFail(NetError error) {
                                showToast(error.getMessage());
                            }
                        });

                    } else {
                        String avatarId = Utils.getAvatarId(mDevice.getImgSrc());
                        IntoYunSdk.updateAvatar(media.getCompressPath(), media.getPictureType(), avatarId, "device", new IntoYunListener() {
                            @Override
                            public void onSuccess(Object result) {
                                String image = "v1/avatar/" + avatarId + "?t=" + IntoUtil.getCurrentTimeMillis();
                                mDevice.setImgSrc(image);
                                UpdateDevice updateDevice = new UpdateDevice(mDevice);
                                EventBus.getDefault().post(updateDevice);
                                showDevImage(image);
                            }

                            @Override
                            public void onFail(NetError error) {
                                showToast(error.getMessage());
                            }
                        });
                    }
                    break;
            }
        }
    }

}
