package com.molmc.intoyundemo.support.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.support.eventbus.DataPointEvent;
import com.molmc.intoyundemo.utils.Constant;
import com.molmc.intoyundemo.utils.DialogUtil;
import com.molmc.intoyundemo.utils.Utils;
import com.molmc.intoyunsdk.bean.DataPointBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Map;
import java.util.regex.Pattern;

import static com.molmc.intoyundemo.utils.Constant.DF_TLV;

/**
 * features:
 * Author：  hhe on 16-8-5 23:34
 * Email：   hhe@molmc.com
 */

public class WidgetExtra extends LinearLayout implements View.OnClickListener {

	private EditText etContent;
	private TextView btnSend;
	private TextView tvTitle;
	private TextView tvValue;

	private DataPointBean dataPoint;
	private OnChangeListener mListener;

	public WidgetExtra(Context context) {
		this(context, null);
	}

	public WidgetExtra(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WidgetExtra(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.setMargins(2, Utils.dip2px(5), 2, Utils.dip2px(5));
		params.gravity = Gravity.CENTER;
		ViewGroup view = (ViewGroup) View.inflate(context, R.layout.datapoint_extra, null);
		tvTitle = (TextView) view.findViewById(R.id.txtTitle);
		etContent = (EditText) view.findViewById(R.id.etContent);
		btnSend = (TextView) view.findViewById(R.id.btnSend);
		tvValue = (TextView) view.findViewById(R.id.txtValue);
		addView(view, params);
		EventBus.getDefault().register(this);
	}

	public void initData(DataPointBean dataPoint, OnChangeListener listener) {
		this.dataPoint = dataPoint;
		this.mListener = listener;
		if (tvTitle !=null){
			tvTitle.setText(Utils.getDatapointName(getContext(), dataPoint));
		}
		if (etContent != null) {
			btnSend.setOnClickListener(this);
		}

		if (dataPoint.getDirection() == Constant.TRANSFROM_DATA) {
			tvValue.setVisibility(VISIBLE);
			etContent.setVisibility(GONE);
			btnSend.setVisibility(GONE);
		} else {
			tvValue.setVisibility(GONE);
			etContent.setVisibility(VISIBLE);
			btnSend.setVisibility(VISIBLE);
		}
	}

	@Subscribe
	public void onEventMainThread(DataPointEvent event) {
		receiveData(event.getPayload());
	}

	public void receiveData(Map<Integer, Object> data) {
		if (data.containsKey(dataPoint.getDpId())){
			String value = (String) data.get(dataPoint.getDpId());
			tvValue.setText(value);
			etContent.setText(value);
			etContent.setSelection(0, etContent.getText().toString().length());
		}
	}


	@Override
	public void onClick(View v) {
		String content = etContent.getText().toString().trim();
		if (TextUtils.isEmpty(content)) {
			Toast.makeText(getContext(), R.string.err_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		Pattern p = Pattern.compile("^[0-9a-fA-F]+");
		if (content.length()%2==0 && p.matcher(content).matches()){
			Utils.hiderSoftInput(etContent, getContext());
			this.mListener.onChanged(content, dataPoint, DF_TLV);
		} else {
			DialogUtil.showToast(R.string.err_input_hex);
		}

	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		EventBus.getDefault().unregister(this);
	}

}
