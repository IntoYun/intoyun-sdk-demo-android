package com.molmc.intoyundemo.support.views;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.molmc.intoyunsdk.bean.DataPointBean;
import com.molmc.intoyunsdk.bean.RecipeBean;
import com.molmc.intoyunsdk.utils.IntoUtil;
import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.support.RecipeChangeListener;
import com.molmc.intoyundemo.utils.Constant;
import com.molmc.intoyundemo.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * features: 开关控件
 * Author：  hhe on 16-8-6 11:03
 * Email：   hhe@molmc.com
 */

public class RecipeActionMessage extends LinearLayout {


    @Bind(R.id.tvTitle)
    TextView tvTitle;
    @Bind(R.id.emailContent)
    EditText emailContent;

    private RecipeBean.ActionValBean actionVal;
    private DataPointBean dataPoint;
    private RecipeChangeListener listener;
    private Context mContext;

    public RecipeActionMessage(Context context) {
        this(context, null);
        mContext = context;
    }

    public RecipeActionMessage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecipeActionMessage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
        ViewGroup view = (ViewGroup) View.inflate(context, R.layout.recipe_message, null);
        addView(view, params);
        ButterKnife.bind(this, view);
    }

    public void initData(RecipeBean.ActionValBean actionVal, DataPointBean dataPoint, RecipeChangeListener listener) {
        this.actionVal = actionVal;
        this.dataPoint = dataPoint;
        this.listener = listener;
        if (!Constant.RECIPE_ACTION_MSGBOX.equals(dataPoint.getType())){
            tvTitle.setText(String.format(getResources().getString(R.string.recipe_string_title), Utils.getDatapointName(mContext, dataPoint)));
        }

        if (!IntoUtil.Empty.check(actionVal.getValue())) {
            emailContent.setText(String.valueOf(actionVal.getValue()));
        }

        emailContent.addTextChangedListener(onTextChangeListener(emailContent));
    }

    private TextWatcher onTextChangeListener(final TextView view) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (view == emailContent) {
                    if (TextUtils.isEmpty(s.toString())) {
                        return;
                    }
                    actionVal.setValue(s.toString());
                }
                listener.onActionChange(actionVal);
            }
        };
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ButterKnife.unbind(this);
    }
}
