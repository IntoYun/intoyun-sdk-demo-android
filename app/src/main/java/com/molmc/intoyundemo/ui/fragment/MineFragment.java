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
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.ui.activity.LoginActivity;
import com.molmc.intoyundemo.utils.DialogUtil;
import com.molmc.intoyundemo.utils.Utils;
import com.molmc.intoyunsdk.network.IntoYunListener;
import com.molmc.intoyunsdk.network.NetError;
import com.molmc.intoyunsdk.network.model.response.UserResult;
import com.molmc.intoyunsdk.openapi.Constant;
import com.molmc.intoyunsdk.openapi.IntoYunSdk;
import com.molmc.intoyunsdk.utils.IntoUtil;
import com.molmc.intoyunsdk.utils.IntoYunSharedPrefs;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static android.app.Activity.RESULT_OK;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

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


    private List<LocalMedia> selectList = new ArrayList<>();

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
        UserResult userInfo = IntoYunSharedPrefs.getUserInfo(getActivity());
        userName.setText(userInfo.getNickname());
        showUserImage(userInfo.getImgSrc());
        getUserInfo();
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



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    Logger.i(new Gson().toJson(selectList));
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
                    LocalMedia media = selectList.get(0);
                    if (media.isCut()) {
                        // 注意：如果压缩过，在上传的时候，取 media.getCompressPath();
                        // 压缩图compressPath
                        final UserResult userInfoBean = IntoYunSharedPrefs.getUserInfo(getActivity());
                        if (TextUtils.isEmpty(userInfoBean.getImgSrc())) {
                            IntoYunSdk.uploadAvatar(media.getCompressPath(), media.getPictureType(), "user", IntoYunSharedPrefs.getUserInfo(getActivity()).getUid(), new IntoYunListener<String>() {
                                @Override
                                public void onSuccess(String result) {
                                    String image = "v1/avatar/" + result + "?t=" + IntoUtil.getCurrentTimeMillis();
                                    userInfoBean.setImgSrc(image);
                                    IntoYunSharedPrefs.saveUserInfo(getActivity(), userInfoBean);

                                    showUserImage(image);
                                }

                                @Override
                                public void onFail(NetError error) {
                                    showToast(error.getMessage());
                                }
                            });
                        } else {
                            String avatarId = Utils.getAvatarId(userInfoBean.getImgSrc());
                            IntoYunSdk.updateAvatar(media.getCompressPath(), media.getPictureType(), avatarId, "user", new IntoYunListener() {
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
                    break;
            }
        }
    }


    /**
     * 获取用户信息
     */
    private void getUserInfo() {
        IntoYunSdk.getUserInfo(new IntoYunListener<UserResult>() {
            @Override
            public void onSuccess(UserResult result) {
                Logger.i(new Gson().toJson(result));
                userName.setText(TextUtils.isEmpty(result.getNickname())? result.getUsername() : result.getNickname());

                showUserImage(result.getImgSrc());
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


    private void showUserImage(String imgSrc){
        RequestOptions opts = new RequestOptions();
        opts.placeholder(R.mipmap.ic_default_1);
        opts.fitCenter();

        Glide.with(MineFragment.this)
                .load(Constant.getHttpHost() + imgSrc)
                .apply(opts)
                .apply(bitmapTransform(new RoundedCornersTransformation(Utils.dip2px(40), 0)))
                .into(userHead);
    }

    /**
     * 修改用户昵称
     *
     * @param nickname
     */
    private void changeUserInfo(final String nickname) {
        if (TextUtils.isEmpty(nickname)) {
            DialogUtil.showToast(R.string.err_nickname_empty);
            return;
        }
        IntoYunSdk.updateUserInfo(nickname, nickname, new IntoYunListener() {
            @Override
            public void onSuccess(Object result) {
                showToast(R.string.suc_change);
                userName.setText(nickname);
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
                gotoLogin();
            }

            @Override
            public void onFail(NetError error) {
                showToast(error.getMessage());
                gotoLogin();
            }
        });
    }

    private void gotoLogin(){
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

}
