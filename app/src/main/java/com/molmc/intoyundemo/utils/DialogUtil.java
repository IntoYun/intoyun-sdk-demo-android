package com.molmc.intoyundemo.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.base.IntoYunApplication;

import java.lang.reflect.Field;

/**
 * features: dialog 工具类
 * Author：  hhe on 16-7-30 16:23
 * Email：   hhe@molmc.com
 */

public class DialogUtil {

	public static ProgressDialog progressDialog;

	public static ProgressDialog createProgressDialog(Activity context, String message, int widgetColor) {
		dismissProgressDialog();
		//Theme.Material.Dialog.Alert
		progressDialog = new MProgressDialog(context, widgetColor);
		progressDialog.setMessage(message);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		return progressDialog;
	}

	public static ProgressDialog createProgressDialog(Activity context, int message) {
		return createProgressDialog(context, context.getString(message), R.color.colorPrimary);
	}

	public static void updateProgressDialog(String message) {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.setMessage(message);
		}
	}

	public static void dismissProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			try {
				progressDialog.dismiss();
			} catch (IllegalArgumentException e) {
			}
			progressDialog = null;
		}
	}

	public static class MProgressDialog extends ProgressDialog {

		private int color;

		public MProgressDialog(Context context, int color) {
			super(context);

			this.color = color;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			if (color != 0) {
				try {
					Field progressBarField = MProgressDialog.class.getSuperclass().getDeclaredField("mProgress");
					progressBarField.setAccessible(true);
					ProgressBar progressBar = (ProgressBar) progressBarField.get(this);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
						ColorStateList stateList = ColorStateList.valueOf(color);
						progressBar.setProgressTintList(stateList);
						progressBar.setSecondaryProgressTintList(stateList);
						progressBar.setIndeterminateTintList(stateList);
					} else {
						PorterDuff.Mode mode = PorterDuff.Mode.SRC_IN;
						if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
							mode = PorterDuff.Mode.MULTIPLY;
						}
						if (progressBar.getIndeterminateDrawable() != null)
							progressBar.getIndeterminateDrawable().setColorFilter(color, mode);
						if (progressBar.getProgressDrawable() != null)
							progressBar.getProgressDrawable().setColorFilter(color, mode);
					}
				} catch (Throwable throwable) {
					throwable.printStackTrace();
				}
			}
		}
	}

	public static void showDialog(Context context, String title, String msg, String negativebtn, String positivebtn, final Interface.DialogCallback callback) {
		new AlertDialogWrapper.Builder(context)
				.setTitle(title)
				.setMessage(msg)
				.setNegativeButton(negativebtn, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						callback.onNegative();
					}
				})
				.setPositiveButton(positivebtn, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						callback.onPositive();
					}
				})
				.show();
	}

	public static void showDialog(Context context, int title, int msg, int negativebtn, int positivebtn, final Interface.DialogCallback callback) {
		new AlertDialogWrapper.Builder(context)
				.setTitle(title)
				.setMessage(msg)
				.setNegativeButton(negativebtn, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						callback.onNegative();
					}
				})
				.setPositiveButton(positivebtn, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						callback.onPositive();
					}
				})
				.show();
	}

	public static void showConfirmDialog(Context context, String title, String msg, String positivebtn, final Interface.DialogCallback callback) {
		new AlertDialogWrapper.Builder(context)
				.setTitle(title)
				.setMessage(msg)
				.setPositiveButton(positivebtn, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						callback.onPositive();
					}
				})
				.show();
	}

	public static void showConfirmDialog(Context context, int title, int msg, int positivebtn, final Interface.DialogCallback callback) {
		new AlertDialogWrapper.Builder(context)
				.setTitle(title)
				.setMessage(msg)
				.setPositiveButton(positivebtn, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						callback.onPositive();
					}
				})
				.show();
	}

	/**
	 * 输入dialog
	 *
	 * @param context
	 * @param title
	 * @param callback
	 */
	public static void inputDialog(Context context, int title, MaterialDialog.InputCallback callback) {
		new MaterialDialog.Builder(context)
				.title(title)
				.inputRangeRes(4, 20, R.color.colorPrimary)
				.input(null, null, callback).show();
	}

	/**
	 * list dialog
	 * @param context
	 * @param title
	 * @param array
	 * @param callback
	 */
	public static void listDialog(Context context, int title, int array, MaterialDialog.ListCallback callback) {
		new MaterialDialog.Builder(context)
				.title(title)
				.items(array)
				.itemsCallback(callback)
				.show();
	}


	/**
	 * menu dialog
	 * @param context
	 * @param menuArr
	 * @param onItemClickListener
	 */
	public static void showMenuDialog(Context context, String[] menuArr, DialogInterface.OnClickListener onItemClickListener) {
		new AlertDialogWrapper.Builder(context)
				.setItems(menuArr, onItemClickListener)
				.show();
	}

	/**
	 * 以Toast形式显示一个消息
	 *
	 * @param msg
	 */
	public static void showToast(String msg) {
		if (!TextUtils.isEmpty(msg)) {
			Toast.makeText(IntoYunApplication.getInstance(), msg, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * @param msgId
	 */
	public static void showToast(int msgId) {
		showToast(IntoYunApplication.getInstance().getString(msgId));
	}
}
