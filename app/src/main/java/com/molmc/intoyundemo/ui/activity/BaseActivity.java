package com.molmc.intoyundemo.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.molmc.intoyundemo.ui.fragment.BaseFragment;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * features: 基础activity
 * Author：  hhe on 16-7-28 11:07
 * Email：   hhe@molmc.com
 */

public class BaseActivity extends AppCompatActivity{

	// 当有Fragment Attach到这个Activity的时候，就会保存
	private Map<String, WeakReference<BaseFragment>> fragmentRefs;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fragmentRefs = new HashMap<String, WeakReference<BaseFragment>>();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				if (onBackClick())
					return true;
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void addFragment(String tag, BaseFragment fragment) {
		if (fragmentRefs!=null) {
			fragmentRefs.put(tag, new WeakReference<BaseFragment>(fragment));
		}
	}

	public void removeFragment(String tag) {
		if (fragmentRefs!=null) {
			fragmentRefs.remove(tag);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (onBackClick())
				return true;
		}
		if (keyCode == KeyEvent.KEYCODE_MENU){
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean onBackClick() {
		Set<String> keys = fragmentRefs.keySet();
		for (String key : keys) {
			WeakReference<BaseFragment> fragmentRef = fragmentRefs.get(key);
			BaseFragment fragment = fragmentRef.get();
			if (fragment != null && fragment.onBackClick())
				return true;
		}
		finish();
		return true;
	}

	public void showToast(int res){
		Toast.makeText(this, res, Toast.LENGTH_SHORT).show();
	}

	public void showToast(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
}
