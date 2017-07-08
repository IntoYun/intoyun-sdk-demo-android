package com.molmc.intoyundemo.support.views;

import android.app.Activity;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.molmc.intoyunsdk.bean.RecipeBean;
import com.molmc.intoyunsdk.utils.IntoUtil;
import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.support.RecipeChangeListener;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.zjy.actionsheet.ActionSheet;

/**
 * features: 开关控件
 * Author：  hhe on 16-8-6 11:03
 * Email：   hhe@molmc.com
 */

public class RecipeTriggerTimer extends LinearLayout implements TimePickerDialog.OnTimeSetListener {


    @Bind(R.id.tvTime)
    TextView tvTime;
    @Bind(R.id.tvRepeat)
    TextView tvRepeat;

    private RecipeBean.CrontabBean crontab;
    private RecipeChangeListener listener;
    private Activity mContext;
    private String[] list;

    public RecipeTriggerTimer(Activity context) {
        this(context, null);
        mContext = context;
    }

    public RecipeTriggerTimer(Activity context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecipeTriggerTimer(Activity context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
        ViewGroup view = (ViewGroup) View.inflate(context, R.layout.recipe_timer, null);
        addView(view, params);
        ButterKnife.bind(this, view);
    }

    public void initData(RecipeBean.CrontabBean crontab, RecipeChangeListener listener) {
        list = getResources().getStringArray(R.array.dayOfWeek);
        this.crontab = crontab;
        this.listener = listener;

        if (!IntoUtil.Empty.check(crontab)) {
            if ("*".equals(crontab.getDay_of_week())) {
                tvRepeat.setText(list[getDayOfWeek()]);
                this.crontab.setDay_of_week(String.valueOf(getDayOfWeek()));
            } else {
                tvRepeat.setText(list[Integer.parseInt(crontab.getDay_of_week())]);
            }
            if ("*".equals(crontab.getHour())) {
                tvTime.setText(getCurrentTime());
            } else {
                String hour = crontab.getHour();
                String min = crontab.getMinute();
                tvTime.setText(hour + ":" + min);
            }
        }

    }

    private String getCurrentTime() {
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int min = now.get(Calendar.MINUTE);
        this.crontab.setHour(String.valueOf(hour));
        this.crontab.setMinute(String.valueOf(min));
        return hour + ":" + min;
    }

    private int getDayOfWeek() {
        Calendar now = Calendar.getInstance();
        boolean isFirstSunday = (now.getFirstDayOfWeek() == Calendar.SUNDAY);
        int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
        //若一周第一天为星期天，则-1
        if (isFirstSunday) {
            dayOfWeek = dayOfWeek - 1;
        } else {
            if (dayOfWeek == 7) {
                dayOfWeek = 0;
            }
        }
        return dayOfWeek;
    }

    private void showActionSheet() {
        int size = list.length;
        int[] colors = new int[size];
        for (int i = 0; i < size; i++) {
            colors[i] = getResources().getColor(R.color.colorPrimary);
        }

        ActionSheet actionSheet = new ActionSheet.Builder()
                .setOtherBtn(list, colors)
                .setCancelBtn(getResources().getString(R.string.cancel), Color.RED)
                .setCancelableOnTouchOutside(true)
                .setActionSheetListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isByBtn) {
                    }

                    @Override
                    public void onButtonClicked(ActionSheet actionSheet, int index) {
                        if (index < list.length) {
                            tvRepeat.setText(list[index]);
                            if (index == 7) {
                                crontab.setDay_of_week("*");
                            } else {
                                crontab.setDay_of_week(String.valueOf(index));
                            }
                            listener.onCrontabChange(crontab);
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

    private void showTimePicker() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog dpd = TimePickerDialog.newInstance(this, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
        dpd.show(mContext.getFragmentManager(), "Timepickerdialog");

    }

    @OnClick({R.id.tvTime, R.id.tvRepeat})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvTime:
                showTimePicker();
                break;
            case R.id.tvRepeat:
                showActionSheet();
                break;
        }
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        tvTime.setText(hourOfDay + ":" + minute);
        crontab.setHour(String.valueOf(hourOfDay));
        crontab.setMinute(String.valueOf(minute));
        listener.onCrontabChange(crontab);
    }
}
