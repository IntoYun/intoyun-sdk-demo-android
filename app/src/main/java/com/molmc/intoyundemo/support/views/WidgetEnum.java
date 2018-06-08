package com.molmc.intoyundemo.support.views;

import android.app.Activity;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.support.eventbus.DataPointEvent;
import com.molmc.intoyundemo.utils.Constant;
import com.molmc.intoyundemo.utils.Utils;
import com.molmc.intoyunsdk.bean.DataPointBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.Map;

import cn.zjy.actionsheet.ActionSheet;

import static com.molmc.intoyundemo.utils.Constant.DF_TLV;

/**
 * features: 开关控件
 * Author：  hhe on 16-8-6 11:03
 * Email：   hhe@molmc.com
 */

public class WidgetEnum extends LinearLayout implements View.OnClickListener {

	private TextView tvTitle;
	private TextView tvValue;
	private DataPointBean dataPoint;
	private OnChangeListener mListener;
	private Activity mContext;


	public WidgetEnum(Activity context) {
		this(context, null);
		mContext = context;
	}

	public WidgetEnum(Activity context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WidgetEnum(Activity context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, (int)getResources().getDimension(R.dimen.data_point_height));
		params.setMargins(2, Utils.dip2px(5), 2, Utils.dip2px(5));
		params.gravity= Gravity.CENTER_VERTICAL;
		setLayoutParams(params);
		ViewGroup view = (ViewGroup) View.inflate(context, R.layout.datapoint_enum, null);
		tvTitle = (TextView) view.findViewById(R.id.txtTitle);
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
		if (tvValue != null) {
			tvValue.setText(String.valueOf(dataPoint.get_enum().get(0)));
			if (dataPoint.getDirection() != Constant.TRANSFROM_DATA) {
				tvValue.setOnClickListener(this);
			}
		}

	}


	private void showActionSheet() {
		List<?> list = dataPoint.get_enum();
		int size = list.size();
		String[] xEnum = new String[size];
		int[] colors = new int[size];
		for (int i=0; i < size; i ++){
			xEnum[i] = String.valueOf(list.get(i));
			colors[i] = getResources().getColor(R.color.colorPrimary);;
		}


		ActionSheet actionSheet = new ActionSheet.Builder()
//				.setTitle("Title", Color.BLUE)
				//.setTitleTextSize(20)
				.setOtherBtn(xEnum, colors)
				//.setOtherBtnTextSize(30)
//				.setOtherBtnSub(new String[]{null, "Btn1 sub", ""}, new int[]{Color.BLACK, Color.BLUE, Color.GREEN})
				//.setOtherBtnSubTextSize(20)
				.setCancelBtn(getResources().getString(R.string.cancel), Color.RED)
				//.setCancelBtnTextSize(30)
				.setCancelableOnTouchOutside(true)
				.setActionSheetListener(new ActionSheet.ActionSheetListener() {
					@Override
					public void onDismiss(ActionSheet actionSheet, boolean isByBtn) {
					}

					@Override
					public void onButtonClicked(ActionSheet actionSheet, int index) {
						if (index<dataPoint.get_enum().size()) {
							tvValue.setText(String.valueOf(dataPoint.get_enum().get(index)));
							mListener.onChanged(index, dataPoint, DF_TLV);
						}
					}
				}).build();

		actionSheet.show(mContext.getFragmentManager());
	}

	@Subscribe
	public void onEventMainThread(DataPointEvent event) {
		receiveData(event.getPayload());
	}

	public void receiveData(Map<Integer, Object> data) {
		if (data.containsKey(dataPoint.getDpId())){
			double value = (double) data.get(dataPoint.getDpId());
			tvValue.setText(String.valueOf(dataPoint.get_enum().get((int) value)));
		}
	}

	@Override
	public void onClick(View v) {
		showActionSheet();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		EventBus.getDefault().unregister(this);
	}
}
