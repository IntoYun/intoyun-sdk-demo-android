package com.molmc.intoyundemo.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.molmc.intoyunsdk.network.IntoYunListener;
import com.molmc.intoyunsdk.network.NetError;
import com.molmc.intoyunsdk.openapi.IntoYunSdk;
import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.ui.activity.FragmentCommonActivity;
import com.molmc.intoyundemo.ui.activity.LoginActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * features: 修改密码
 * Author：  hhe on 16-8-25 14:52
 * Email：   hhe@molmc.com
 */

public class ChangePwdFragment extends BaseFragment {

    public static void launch(Activity from) {
        FragmentCommonActivity.launch(from, ChangePwdFragment.class, null);
    }

    @Bind(R.id.editOldPwd)
    EditText editOldPwd;
    @Bind(R.id.editNewPwd1)
    EditText editNewPwd1;
    @Bind(R.id.editNewPwd2)
    EditText editNewPwd2;
    @Bind(R.id.btnSend)
    Button btnSend;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_pwd, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.btnSend)
    public void onClick() {
        changePassword();
    }

    private void changePassword() {
        String oldPwd = editOldPwd.getText().toString();
        String newPwd1 = editNewPwd1.getText().toString();
        String newPwd2 = editNewPwd2.getText().toString();
        if (TextUtils.isEmpty(oldPwd)) {
            showToast(R.string.err_current_pwd_empty);
            return;
        }
        if (TextUtils.isEmpty(newPwd1)) {
            showToast(R.string.err_new_pwd_empty);
            return;
        }
        if (!newPwd1.equals(newPwd2)) {
            showToast(R.string.err_new_pwd_diff);
            return;
        }
        if (oldPwd.equals(newPwd2)) {
            showToast(R.string.err_old_pwd_diff);
            return;
        }
        IntoYunSdk.changePassword(oldPwd, newPwd1, new IntoYunListener() {
            @Override
            public void onSuccess(Object result) {
                IntoYunSdk.userLogout(new IntoYunListener() {
                    @Override
                    public void onSuccess(Object result) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }

                    @Override
                    public void onFail(NetError exception) {
                        showToast(exception.getMessage());
                    }
                });
            }

            @Override
            public void onFail(NetError error) {
                showToast(R.string.suc_change);
            }
        });
    }
}
