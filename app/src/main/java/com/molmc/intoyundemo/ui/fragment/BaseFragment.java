package com.molmc.intoyundemo.ui.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.Toast;

import com.molmc.intoyundemo.ui.activity.BaseActivity;

/**
 * features: 基础 fragment
 * Author：  hhe on 16-7-28 15:17
 * Email：   hhe@molmc.com
 */

public class BaseFragment extends Fragment {

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof BaseActivity)
			((BaseActivity) context).addFragment(toString(), this);
	}


	@Override
	public void onDetach() {
		super.onDetach();
		if (getActivity() != null && getActivity() instanceof BaseActivity)
			((BaseActivity) getActivity()).removeFragment(this.toString());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 返回按键被点击了
	 *
	 * @return
	 */
	public boolean onBackClick() {
		return false;
	}

	/**
	 * 以Toast形式显示一个消息
	 *
	 * @param msg
	 */
	protected void showToast(String msg) {
		if (!TextUtils.isEmpty(msg)&&getActivity()!=null) {
			Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
		}
	}

	/**
	 *
	 *
	 * @param msgId
	 */
	protected void showToast(int msgId) {
		showToast(getString(msgId));
	}

}
