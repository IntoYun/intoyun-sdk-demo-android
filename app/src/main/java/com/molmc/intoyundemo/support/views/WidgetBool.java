package com.molmc.intoyundemo.support.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.support.eventbus.DataPointEvent;
import com.molmc.intoyundemo.utils.Constant;
import com.molmc.intoyundemo.utils.Utils;
import com.molmc.intoyunsdk.bean.DataPointBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Map;

/**
 * features: 开关控件
 * Author：  hhe on 16-8-6 11:03
 * Email：   hhe@molmc.com
 */

public class WidgetBool extends LinearLayout implements CompoundButton.OnCheckedChangeListener {

	private ToggleButton mSwitch;
	private TextView tvTitle;
	private TextView tvValue;
	private DataPointBean dataPoint;
	private OnChangeListener mListener;


	public WidgetBool(Context context) {
		this(context, null);
	}

	public WidgetBool(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WidgetBool(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, (int)getResources().getDimension(R.dimen.data_point_height));
		params.setMargins(2, Utils.dip2px(5), 2, Utils.dip2px(5));
		params.gravity= Gravity.CENTER_VERTICAL;
		setLayoutParams(params);
		ViewGroup view = (ViewGroup) View.inflate(context, R.layout.datapoint_bool, null);
		tvTitle = (TextView) view.findViewById(R.id.txtTitle);
		mSwitch = (ToggleButton) view.findViewById(R.id.swSwitch);
		tvValue = (TextView) view.findViewById(R.id.txtStatus);
		addView(view, params);
		EventBus.getDefault().register(this);
	}

	public void initData(DataPointBean dataPoint, OnChangeListener onCheckedListener) {
		this.dataPoint = dataPoint;
		this.mListener = onCheckedListener;
		if (tvTitle !=null){
			tvTitle.setText(Utils.getDatapointName(getContext(), dataPoint));
		}
		if (mSwitch != null) {
			mSwitch.setOnCheckedChangeListener(this);
		}

		if (dataPoint.getDirection() == Constant.TRANSFROM_DATA) {
			tvValue.setVisibility(VISIBLE);
			mSwitch.setVisibility(GONE);
		} else {
			tvValue.setVisibility(GONE);
			mSwitch.setVisibility(VISIBLE);
		}
	}


	@Subscribe
	public void onEventMainThread(DataPointEvent event) {
		receiveData(event.getPayload());
	}


	public void receiveData(Map<Integer, Object> data) {
		if (data.containsKey(dataPoint.getDpId())){
			boolean value = (boolean) data.get(dataPoint.getDpId());
			mSwitch.setChecked(value);
			if (value){
				tvValue.setText("开");
			} else {
				tvValue.setText("关");
			}
		}
	}


	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		this.mListener.onChanged(isChecked, dataPoint);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		EventBus.getDefault().unregister(this);
	}

}
