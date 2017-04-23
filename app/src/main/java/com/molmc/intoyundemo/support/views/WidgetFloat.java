package com.molmc.intoyundemo.support.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.support.eventbus.DataPointEvent;
import com.molmc.intoyundemo.utils.Constant;
import com.molmc.intoyundemo.utils.Utils;
import com.molmc.intoyunsdk.bean.DataPointBean;
import com.xw.repo.BubbleSeekBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Map;

import static com.molmc.intoyundemo.utils.Constant.EVENT_SCROLL;

/**
 * features: 表盘控件
 * Author：  hhe on 16-8-5 16:13
 * Email：   hhe@molmc.com
 */

public class WidgetFloat extends LinearLayout implements BubbleSeekBar.OnProgressChangedListener {

    private BubbleSeekBar bubbleSeekBar;
    private DataPointBean dataPoint;
    private TextView tvTitle;
    private TextView tvValue;
    private OnChangeListener mListener;
    private ProgressBar progressBar;
    private String unit;

    public WidgetFloat(Context context) {
        this(context, null);
    }

    public WidgetFloat(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WidgetFloat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.data_point_height));
        params.setMargins(2, Utils.dip2px(5), 2, Utils.dip2px(5));
        params.gravity = Gravity.CENTER_VERTICAL;
        setLayoutParams(params);
        ViewGroup view = (ViewGroup) View.inflate(context, R.layout.datapoint_float, null);
        bubbleSeekBar = (BubbleSeekBar) view.findViewById(R.id.bubbleSeekBar);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        tvTitle = (TextView) view.findViewById(R.id.txtTitle);
        tvValue = (TextView) view.findViewById(R.id.txtValue);
        addView(view, params);
        EventBus.getDefault().register(this);
    }

    public void initData(DataPointBean dataPoint, OnChangeListener listener) {
        this.mListener = listener;
        this.dataPoint = dataPoint;
        if (dataPoint.getDirection() == Constant.TRANSFROM_DATA) {
            bubbleSeekBar.setVisibility(GONE);
            tvValue.setVisibility(VISIBLE);
            progressBar.setVisibility(VISIBLE);
            progressBar.setProgress(dataPoint.getMin());
        } else {
            bubbleSeekBar.setVisibility(VISIBLE);
//            tvValue.setVisibility(GONE);
            progressBar.setVisibility(GONE);
            bubbleSeekBar.getConfigBuilder()
                    .min(dataPoint.getMin())
                    .max(dataPoint.getMax())
                    .build();
            bubbleSeekBar.setOnProgressChangedListener(this);
        }
        unit = TextUtils.isEmpty(dataPoint.getUnit()) ? "" : dataPoint.getUnit();
        tvValue.setText(String.valueOf(dataPoint.getMin()) + unit);
        tvTitle.setText(Utils.getDatapointName(getContext(), dataPoint));
    }

    @Subscribe
    public void onEventMainThread(DataPointEvent event) {
        receiveData(event.getPayload());
    }

    @Subscribe
    public void onEventMainThread(String event) {
        if (event.equals(EVENT_SCROLL)) {
            bubbleSeekBar.correctOffsetWhenContainerOnScrolling();
        }
    }

    public void receiveData(Map<Integer, Object> data) {
        if (data.containsKey(dataPoint.getDpId())) {
            float value = Float.parseFloat(String.valueOf(data.get(dataPoint.getDpId())));
            String valueStr = toDecimal(value);

            if (dataPoint.getDirection() == Constant.TRANSFROM_DATA) {
                progressBar.setProgress((int) value);
            } else {
                bubbleSeekBar.setProgress(Float.parseFloat(valueStr));
            }

            tvValue.setText(valueStr + unit);
        }
    }

    @Override
    public void onProgressChanged(int progress, float progressFloat) {
        String sendData = toDecimal(progressFloat);
        tvValue.setText(sendData + unit);
        if (dataPoint.getDirection() != Constant.TRANSFROM_DATA) {
            this.mListener.onChanged(Float.valueOf(sendData), dataPoint);
        }
    }

    @Override
    public void getProgressOnActionUp(int progress, float progressFloat) {
    }

    @Override
    public void getProgressOnFinally(int progress, float progressFloat) {
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    private String toDecimal(float value){
        String valueStr = String.valueOf(value);

        int indexDot = valueStr.indexOf(".");

        if (indexDot < 0){
            if (dataPoint.getResolution()>0){
                valueStr = valueStr + ".";
            } else {
                return valueStr;
            }
        } else {
            if (dataPoint.getResolution()<=0){
                valueStr = valueStr.split("\\.")[0];
                return valueStr;
            }
        }
        while (valueStr.length() <= indexDot + dataPoint.getResolution()){
            valueStr = valueStr + "0";
        }
        return valueStr;
    }
}
