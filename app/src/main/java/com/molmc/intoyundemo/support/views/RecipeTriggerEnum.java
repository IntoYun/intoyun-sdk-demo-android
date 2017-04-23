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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.zjy.actionsheet.ActionSheet;

/**
 * features: 开关控件
 * Author：  hhe on 16-8-6 11:03
 * Email：   hhe@molmc.com
 */

public class RecipeTriggerEnum extends LinearLayout {


    @Bind(R.id.tvTitle)
    TextView tvTitle;
    @Bind(R.id.tvName)
    TextView tvName;
    @Bind(R.id.tvLogic)
    TextView tvLogic;
    @Bind(R.id.tvEnum)
    TextView tvEnum;

    private RecipeBean.TriggerValBean triggerVal;
    private DataPointBean dataPoint;
    private RecipeChangeListener listener;
    private String[] logicOp;
    private String[] logicText;
    private Activity mContext;

    public RecipeTriggerEnum(Activity context) {
        this(context, null);
        mContext = context;
    }

    public RecipeTriggerEnum(Activity context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecipeTriggerEnum(Activity context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
        ViewGroup view = (ViewGroup) View.inflate(context, R.layout.recipe_enum, null);
        addView(view, params);
        ButterKnife.bind(this, view);
    }

    public void initData(RecipeBean.TriggerValBean triggerVal, DataPointBean dataPoint, RecipeChangeListener listener) {
        this.triggerVal = triggerVal;
        this.dataPoint = dataPoint;
        this.listener = listener;
        logicOp = getResources().getStringArray(R.array.logic_op);

        String dpName = Utils.getDatapointName(getContext(), dataPoint);
        if (tvTitle != null) {
            tvTitle.setText(String.format(getResources().getString(R.string.recipe_trigger_title), dpName));
        }
        if (tvName != null) {
            tvName.setText(dpName);
        }

        if (!IntoUtil.Empty.check(dataPoint.get_enum()) && !IntoUtil.Empty.check(triggerVal.getValue())) {
            int value = (int) Float.parseFloat(String.valueOf(triggerVal.getValue()));
            tvEnum.setText((String) dataPoint.get_enum().get(value));
        } else {
            tvEnum.setText((String) dataPoint.get_enum().get(0));
        }
    }

    private void showActionSheet() {
        int size = dataPoint.get_enum().size();
        String[] xEnum = new String[size];
        int[] colors = new int[size];
        for (int i = 0; i < size; i++) {
            xEnum[i] = String.valueOf(dataPoint.get_enum().get(i));
            colors[i] = getResources().getColor(R.color.colorPrimary);
        }

        ActionSheet actionSheet = new ActionSheet.Builder()
                .setOtherBtn(xEnum, colors)
                .setCancelBtn(getResources().getString(R.string.cancel), Color.RED)
                .setCancelableOnTouchOutside(true)
                .setActionSheetListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isByBtn) {
                    }

                    @Override
                    public void onButtonClicked(ActionSheet actionSheet, int index) {
                        if (index < dataPoint.get_enum().size()) {
                            tvEnum.setText((String) dataPoint.get_enum().get(index));
                            triggerVal.setValue(index);
                            triggerVal.setOp("eq");
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


    @OnClick({R.id.tvEnum})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvEnum:
                showActionSheet();
                break;
        }
    }
}
