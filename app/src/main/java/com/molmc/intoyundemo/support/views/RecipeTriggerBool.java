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

public class RecipeTriggerBool extends LinearLayout implements CompoundButton.OnCheckedChangeListener {


    @Bind(R.id.tvTitle)
    TextView tvTitle;
    @Bind(R.id.tvName)
    TextView tvName;
    @Bind(R.id.swSwitch)
    ToggleButton swSwitch;

    private RecipeBean.TriggerValBean triggerVal;
    private DataPointBean dataPoint;
    private RecipeChangeListener listener;

    public RecipeTriggerBool(Context context) {
        this(context, null);
    }

    public RecipeTriggerBool(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecipeTriggerBool(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
        ViewGroup view = (ViewGroup) View.inflate(context, R.layout.recipe_bool, null);
        addView(view, params);
        ButterKnife.bind(this, view);
    }

    public void initData(RecipeBean.TriggerValBean triggerVal, DataPointBean dataPoint, RecipeChangeListener listener) {
        this.triggerVal = triggerVal;
        this.dataPoint = dataPoint;
        this.listener = listener;
        String dpName = Utils.getDatapointName(getContext(), dataPoint);
        if (tvTitle != null) {
            tvTitle.setText(String.format(getResources().getString(R.string.recipe_trigger_title), dpName));
        }

        if (tvName !=null){
            tvName.setText(dpName);
        }
        if (swSwitch != null) {
            swSwitch.setOnCheckedChangeListener(this);
            if (!IntoUtil.Empty.check(triggerVal)){
                boolean status = (String.valueOf(triggerVal.getValue())).equals("true");
                swSwitch.setChecked(status);
            } else {
                swSwitch.setChecked(true);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        RecipeBean.TriggerValBean mTrigger = new RecipeBean.TriggerValBean();
        mTrigger.setDpId(triggerVal.getDpId());
        mTrigger.setFrom(triggerVal.getFrom());
        mTrigger.setOp("eq");
        mTrigger.setValue(isChecked);
        this.listener.onTriggerChange(mTrigger);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ButterKnife.unbind(this);
    }
}
