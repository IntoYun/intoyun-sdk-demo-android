package com.molmc.intoyundemo.support.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.molmc.intoyunsdk.bean.DataPointBean;
import com.molmc.intoyunsdk.bean.RecipeBean;
import com.molmc.intoyunsdk.utils.IntoUtil;
import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.support.RecipeChangeListener;
import com.molmc.intoyundemo.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * features: 开关控件
 * Author：  hhe on 16-8-6 11:03
 * Email：   hhe@molmc.com
 */

public class RecipeActionBool extends LinearLayout implements CompoundButton.OnCheckedChangeListener {


    @Bind(R.id.tvTitle)
    TextView tvTitle;
    @Bind(R.id.tvName)
    TextView tvName;
    @Bind(R.id.swSwitch)
    ToggleButton swSwitch;

    private RecipeBean.ActionValBean actionVal;
    private DataPointBean dataPoint;
    private RecipeChangeListener listener;

    public RecipeActionBool(Context context) {
        this(context, null);
    }

    public RecipeActionBool(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecipeActionBool(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
        ViewGroup view = (ViewGroup) View.inflate(context, R.layout.recipe_bool, null);
        addView(view, params);
        ButterKnife.bind(this, view);
    }

    public void initData(RecipeBean.ActionValBean actionVal, DataPointBean dataPoint, RecipeChangeListener listener) {
        this.actionVal = actionVal;
        this.dataPoint = dataPoint;
        this.listener = listener;
        String dpName = Utils.getDatapointName(getContext(), dataPoint);
        if (tvTitle != null) {
            tvTitle.setText(String.format(getResources().getString(R.string.recipe_action_title), dpName));
        }

        if (tvName !=null){
            tvName.setText(dpName);
        }
        if (swSwitch != null) {
            swSwitch.setOnCheckedChangeListener(this);
            if (!IntoUtil.Empty.check(actionVal)){
                try {
                    boolean status = ((int)Float.parseFloat(String.valueOf(actionVal.getValue()))) == 1;
                    swSwitch.setChecked(status);
                } catch (Exception e){
                    boolean status = false;
                    swSwitch.setChecked(status);
                }
            } else {
                swSwitch.setChecked(true);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        actionVal.setValue(isChecked ? 1: 0);
        this.listener.onActionChange(actionVal);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ButterKnife.unbind(this);
    }
}
