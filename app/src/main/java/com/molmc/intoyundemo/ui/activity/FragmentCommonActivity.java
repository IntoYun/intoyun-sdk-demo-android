package com.molmc.intoyundemo.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.FrameLayout;

import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.bean.FragmentArgs;

import java.lang.reflect.Method;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by hhe on 15-12-23.
 */
public class FragmentCommonActivity extends BaseActivity {

	public static final String FRAGMENT_TAG = "FRAGMENT_CONTAINER";
	@Bind(R.id.toolbar)
	Toolbar toolbar;
	@Bind(R.id.frameContent)
	FrameLayout frameContent;

	/**
	 * 启动一个界面
	 *
	 * @param activity
	 * @param clazz
	 * @param args
	 */
	public static void launch(Activity activity, Class<? extends Fragment> clazz, FragmentArgs args) {
		Intent intent = new Intent(activity, FragmentCommonActivity.class);
		intent.putExtra("className", clazz.getName());
		if (args != null)
			intent.putExtra("args", args);
		activity.startActivity(intent);
	}

	public static void launchForResult(Fragment fragment, Class<? extends Fragment> clazz, FragmentArgs args, int requestCode) {
		if (fragment.getActivity() == null)
			return;
		Activity activity = fragment.getActivity();

		Intent intent = new Intent(activity, FragmentCommonActivity.class);
		intent.putExtra("className", clazz.getName());
		if (args != null)
			intent.putExtra("args", args);
		fragment.startActivityForResult(intent, requestCode);
	}

	public static void launchForResult(BaseActivity from, Class<? extends Fragment> clazz, FragmentArgs args, int requestCode) {
		Intent intent = new Intent(from, FragmentCommonActivity.class);
		intent.putExtra("className", clazz.getName());
		if (args != null)
			intent.putExtra("args", args);
		from.startActivityForResult(intent, requestCode);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		String className = getIntent().getStringExtra("className");
		if (TextUtils.isEmpty(className)) {
			finish();
			return;
		}

		int contentId = R.layout.common_fragment_content;

		FragmentArgs values = (FragmentArgs) getIntent().getSerializableExtra("args");

		Fragment fragment = null;
		if (savedInstanceState == null) {
			try {
				Class clazz = Class.forName(className);
				fragment = (Fragment) clazz.newInstance();
				// 设置参数给Fragment
				if (values != null) {
					try {
						Method method = clazz.getMethod("setArguments", Bundle.class);
						method.invoke(fragment, FragmentArgs.transToBundle(values));
					} catch (Exception e) {
					}
				}
				// 重写Activity的contentView
				try {
					Method method = clazz.getMethod("setActivityContentView");
					if (method != null)
						contentId = Integer.parseInt(method.invoke(fragment).toString());
				} catch (Exception e) {
				}
			} catch (Exception e) {
				e.printStackTrace();
				finish();
				return;
			}
		}

		super.onCreate(savedInstanceState);
		setContentView(contentId);
		ButterKnife.bind(this);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		if (fragment != null) {
			getSupportFragmentManager().beginTransaction().add(R.id.frameContent, fragment, FRAGMENT_TAG).commit();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Fragment f = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
		if (f != null) {
			f.onActivityResult(requestCode, resultCode, data);
		}
	}
}
