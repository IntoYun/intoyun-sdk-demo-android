package com.molmc.intoyundemo.support.views;

import android.app.Activity;
import android.graphics.Color;
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
import butterknife.OnClick;
import cn.zjy.actionsheet.ActionSheet;

/**
 * features: 开关控件
 * Author：  hhe on 16-8-6 11:03
 * Email：   hhe@molmc.com
 */

public class RecipeTriggerFloat extends LinearLayout implements BubbleSeekBar.OnProgressChangedListener {


    @Bind(R.id.tvTitle)
    TextView tvTitle;
    @Bind(R.id.tvName)
    TextView tvName;
    @Bind(R.id.tvLogic)
    TextView tvLogic;
    @Bind(R.id.seekbarValue)
    BubbleSeekBar seekbarValue;

    private RecipeBean.TriggerValBean triggerVal;
    private DataPointBean dataPoint;
    private RecipeChangeListener listener;
    private String[] logicOp;
    private String[] logicText;
    private Activity mContext;

    public RecipeTriggerFloat(Activity context) {
        this(context, null);
        mContext = context;
    }

    public RecipeTriggerFloat(Activity context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecipeTriggerFloat(Activity context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
        ViewGroup view = (ViewGroup) View.inflate(context, R.layout.recipe_float, null);
        addView(view, params);
        ButterKnife.bind(this, view);
    }

    public void initData(RecipeBean.TriggerValBean triggerVal, DataPointBean dataPoint, RecipeChangeListener listener) {
        this.triggerVal = triggerVal;
        this.dataPoint = dataPoint;
        this.listener = listener;
        logicOp = getResources().getStringArray(R.array.logic_op);
        logicText = getResources().getStringArray(R.array.logic);

        String dpName = Utils.getDatapointName(getContext(), dataPoint);
        if (tvTitle != null) {
            tvTitle.setText(String.format(getResources().getString(R.string.recipe_trigger_title), dpName));
        }
        if (tvName != null) {
            tvName.setText(dpName);
        }
        seekbarValue.getConfigBuilder()
                .min(dataPoint.getMin())
                .max(dataPoint.getMax())
                .build();

        if (!IntoUtil.Empty.check(triggerVal)) {
            for (int i = 0; i < logicOp.length; i++) {
                if (logicOp[i].equals(triggerVal.getOp())) {
                    tvLogic.setText(logicText[i]);
                    break;
                }
            }
            seekbarValue.setProgress(Float.parseFloat(String.valueOf(triggerVal.getValue())));
        } else {
            tvLogic.setText(logicText[0]);
            seekbarValue.setProgress(dataPoint.getMin());
        }
        seekbarValue.setOnProgressChangedListener(this);
    }

    private void showActionSheet() {
        int size = logicText.length;
        int[] colors = new int[size];
        for (int i = 0; i < size; i++) {
            colors[i] = getResources().getColor(R.color.colorPrimary);
        }

        ActionSheet actionSheet = new ActionSheet.Builder()
                .setOtherBtn(logicText, colors)
                .setCancelBtn(getResources().getString(R.string.cancel), Color.RED)
                .setCancelableOnTouchOutside(true)
                .setActionSheetListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isByBtn) {
                    }

                    @Override
                    public void onButtonClicked(ActionSheet actionSheet, int index) {
                        if (index<logicOp.length) {
                            tvLogic.setText(logicText[index]);
                            triggerVal.setOp(logicOp[index]);
                            listener.onTriggerChange(triggerVal);
                        }
                    }
                }).build();

        actionSheet.show(mContext.getFragmentManager());
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.tvLogic)
    public void onClick() {
        showActionSheet();
    }

    @Override
    public void onProgressChanged(int progress, float progressFloat) {
        triggerVal.setValue(progressFloat);
        listener.onTriggerChange(triggerVal);
    }

    @Override
    public void getProgressOnActionUp(int progress, float progressFloat) {

    }

    @Override
    public void getProgressOnFinally(int progress, float progressFloat) {

    }
}
