package com.molmc.intoyundemo.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.LocalMediaLoader;
import com.luck.picture.lib.model.PictureConfig;
import com.molmc.intoyunsdk.network.IntoYunListener;
import com.molmc.intoyunsdk.network.NetError;
import com.molmc.intoyunsdk.network.model.response.UserResult;
import com.molmc.intoyunsdk.openapi.Constant;
import com.molmc.intoyunsdk.openapi.IntoYunSdk;
import com.molmc.intoyunsdk.utils.IntoUtil;
import com.molmc.intoyunsdk.utils.IntoYunSharedPrefs;
import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.ui.activity.LoginActivity;
import com.molmc.intoyundemo.utils.DialogUtil;
import com.molmc.intoyundemo.utils.Utils;
import com.orhanobut.logger.Logger;
import com.yalantis.ucrop.entity.LocalMedia;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by hehui on 17/3/21.
 */

public class MineFragment extends BaseFragment {

    public static final String TAG = "MineFragment";
    @Bind(R.id.userHead)
    ImageView userHead;
    @Bind(R.id.userName)
    TextView userName;
    @Bind(R.id.lay_change_password)
    RelativeLayout layChangePassword;
    @Bind(R.id.lay_logout)
    RelativeLayout layLogout;

    public static MineFragment newInstance() {
        MineFragment fragment = new MineFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        setHasOptionsMenu(false);
        getUserInfo();
//        UserResult userInfo = IntoYunSharedPrefs.getUserInfo(getActivity());
//        userName.setText(userInfo.getUsername());
//        Glide.with(this).load(Constant.INTOYUN_HTTP_HOST + userInfo.getImgSrc()).fitCenter()
//                .bitmapTransform(new RoundedCornersTransformation(getActivity(), Utils.dip2px(40), 0)).into(userHead);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.userHead, R.id.userName, R.id.lay_change_password, R.id.lay_logout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.userHead:
                changeUserHeader();
                break;
            case R.id.userName:
                changeUserName();
                break;
            case R.id.lay_change_password:
                ChangePwdFragment.launch(getActivity());
                break;
            case R.id.lay_logout:
                logout();
                break;
        }
    }

    private void changeUserHeader() {
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

    ;

    /**
     * 图片回调方法
     */

    private PictureConfig.OnSelectResultCallback resultCallback = new PictureConfig.OnSelectResultCallback() {

        @Override
        public void onSelectSuccess(List<LocalMedia> resultList) {
            Logger.i(new Gson().toJson(resultList));
            LocalMedia media = resultList.get(0);
            final UserResult userInfoBean = IntoYunSharedPrefs.getUserInfo(getActivity());
            if (media.isCompressed()) {
                // 注意：如果压缩过，在上传的时候，取 media.getCompressPath();
                // 压缩图compressPath
                if (TextUtils.isEmpty(userInfoBean.getImgSrc())) {
                    IntoYunSdk.uploadAvatar(media.getCompressPath(), "user", IntoYunSharedPrefs.getUserInfo(getActivity()).getUid(), new IntoYunListener<String>() {
                        @Override
                        public void onSuccess(String result) {
                            String image = "v1/avatar/" + result + "?t=" + IntoUtil.getCurrentTimeMillis();
                            userInfoBean.setImgSrc(image);
                            IntoYunSharedPrefs.saveUserInfo(getActivity(), userInfoBean);
                            Glide.with(getActivity()).load(Constant.INTOYUN_HTTP_HOST + image).placeholder(R.mipmap.ic_default_1).fitCenter().bitmapTransform(new RoundedCornersTransformation(getActivity(), Utils.dip2px(40), 0)).into(userHead);
                            ;
                        }

                        @Override
                        public void onFail(NetError error) {
                            showToast(error.getMessage());
                        }
                    });
                } else {
                    String avatarId = Utils.getAvatarId(userInfoBean.getImgSrc());
                    IntoYunSdk.updateAvatar(media.getCompressPath(), avatarId, "user", new IntoYunListener() {
                        @Override
                        public void onSuccess(Object result) {
                            getUserInfo();
                        }

                        @Override
                        public void onFail(NetError error) {
                            showToast(error.getMessage());
                        }
                    });
                }

            } else {
                // 注意：没有压缩过，在上传的时候，取 media.getPath();
                // 原图path
                // 注意：如果media.getCatPath();不为空的话 就代表裁剪的图片，上传时可取，但是如果又压缩过，则取最终压缩过的compressPath
            }
        }
    };

    /**
     * 获取用户信息
     */
    private void getUserInfo() {
        IntoYunSdk.getUserInfo(new IntoYunListener<UserResult>() {
            @Override
            public void onSuccess(UserResult result) {
                Logger.i(new Gson().toJson(result));
                userName.setText(result.getNickname());
                Glide.with(MineFragment.this).load(Constant.INTOYUN_HTTP_HOST + result.getImgSrc()).placeholder(R.mipmap.ic_default_1).fitCenter().bitmapTransform(new RoundedCornersTransformation(getActivity(), Utils.dip2px(40), 0)).into(userHead);
                ;
            }

            @Override
            public void onFail(NetError error) {
                Logger.i(error.getMessage());
            }
        });
    }

    private void changeUserName() {
        DialogUtil.inputDialog(getActivity(), R.string.change_username, new MaterialDialog.InputCallback() {
            @Override
            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                changeUserInfo(input.toString());
            }
        });
    }

    /**
     * 修改用户昵称
     *
     * @param nickname
     */
    private void changeUserInfo(String nickname) {
        if (TextUtils.isEmpty(nickname)) {
            DialogUtil.showToast(R.string.err_nickname_empty);
            return;
        }
        IntoYunSdk.updateUserInfo(nickname, nickname, new IntoYunListener() {
            @Override
            public void onSuccess(Object result) {
                showToast(R.string.suc_change);
            }

            @Override
            public void onFail(NetError exception) {
                showToast(exception.getMessage());
            }
        });
    }

    private void logout() {
        IntoYunSdk.userLogout(new IntoYunListener() {
            @Override
            public void onSuccess(Object result) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }

            @Override
            public void onFail(NetError error) {
                showToast(error.getMessage());
            }
        });
    }

}
