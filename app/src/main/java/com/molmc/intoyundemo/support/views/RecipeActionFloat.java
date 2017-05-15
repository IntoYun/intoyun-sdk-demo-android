package com.molmc.intoyundemo.support.views;

import android.app.Activity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.molmc.intoyunsdk.bean.DataPointBean;
import com.molmc.intoyunsdk.bean.RecipeBean;
import com.molmc.intoyunsdk.utils.IntoUtil;
import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.support.RecipeChangeListener;
import com.molmc.intoyundemo.utils.Utils;
import com.xw.repo.BubbleSeekBar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * features: 开关控件
 * Author：  hhe on 16-8-6 11:03
 * Email：   hhe@molmc.com
 */

public class RecipeActionFloat extends LinearLayout implements BubbleSeekBar.OnProgressChangedListener {


    @Bind(R.id.tvTitle)
    TextView tvTitle;
    @Bind(R.id.tvName)
    TextView tvName;
    @Bind(R.id.tvLogic)
    TextView tvLogic;
    @Bind(R.id.seekbarValue)
    BubbleSeekBar seekbarValue;
    @Bind(R.id.txtValue)
    TextView textValue;

    private RecipeBean.ActionValBean actionVal;
    private DataPointBean dataPoint;
    private RecipeChangeListener listener;
    private String unit;

    public RecipeActionFloat(Activity context) {
        this(context, null);
    }

    public RecipeActionFloat(Activity context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecipeActionFloat(Activity context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
        ViewGroup view = (ViewGroup) View.inflate(context, R.layout.recipe_float, null);
        addView(view, params);
        ButterKnife.bind(this, view);
    }

    public void initData(RecipeBean.ActionValBean actionVal, DataPointBean dataPoint, RecipeChangeListener listener) {
        this.actionVal = actionVal;
        this.dataPoint = dataPoint;
        this.listener = listener;
        unit = TextUtils.isEmpty(dataPoint.getUnit()) ? "" : dataPoint.getUnit();

        String dpName = Utils.getDatapointName(getContext(), dataPoint);
        if (tvTitle != null) {
            tvTitle.setText(String.format(getResources().getString(R.string.recipe_action_title), dpName));
        }
        if (tvName != null) {
            tvName.setText(dpName);
        }

        tvLogic.setVisibility(GONE);

        seekbarValue.getConfigBuilder()
                .min(dataPoint.getMin())
                .max(dataPoint.getMax())
                .build();

        if (!IntoUtil.Empty.check(actionVal.getValue())) {
            float value = Utils.parseFloat(Float.parseFloat(String.valueOf(actionVal.getValue())), dataPoint);
            seekbarValue.setProgress(value);
            textValue.setText(value + unit);
        } else {
            seekbarValue.setProgress(dataPoint.getMin());
            textValue.setText(dataPoint.getMin() + unit);
        }
        seekbarValue.setOnProgressChangedListener(this);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ButterKnife.unbind(this);
    }

    @Override
    public void onProgressChanged(int progress, float progressFloat) {
        String sendData = Utils.toDecimal(progressFloat, dataPoint);
        textValue.setText(sendData + unit);
        actionVal.setValue(Utils.parseInt(sendData, dataPoint));
        listener.onActionChange(actionVal);
    }

    @Override
    public void getProgressOnActionUp(int progress, float progressFloat) {

    }

    @Override
    public void getProgressOnFinally(int progress, float progressFloat) {

    }
}
