package com.molmc.intoyundemo.support.views;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.bean.DataContent;
import com.molmc.intoyundemo.support.eventbus.DataPointEvent;
import com.molmc.intoyundemo.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.molmc.intoyundemo.utils.Constant.DF_CUSTOM;

/**
 * features:
 * Author：  hhe on 16-8-5 23:34
 * Email：   hhe@molmc.com
 */

public class WidgetCustom extends LinearLayout implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private CheckBox cbRecv;
    private Button btnClean;
    private TextView tvRecv;
    private CheckBox cbSend;
    private Button btnSend;
    private EditText etContent;
    private LinearLayout layContent;

    private boolean recvHex = true;
    private boolean sendHex = true;
    private List<DataContent> hisData = new ArrayList<>();

    private OnChangeListener mListener;

    public WidgetCustom(Context context) {
        this(context, null);
    }

    public WidgetCustom(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WidgetCustom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(2, Utils.dip2px(5), 2, Utils.dip2px(5));
        params.gravity = Gravity.CENTER;
        ViewGroup view = (ViewGroup) View.inflate(context, R.layout.datapoint_custom, null);
        cbRecv = view.findViewById(R.id.cbRecv);
        btnClean = view.findViewById(R.id.btnClean);
        tvRecv = view.findViewById(R.id.tvRecv);
        cbSend = view.findViewById(R.id.cbSend);
        btnSend = view.findViewById(R.id.btnSend);
        etContent = view.findViewById(R.id.etContent);
        layContent = view.findViewById(R.id.layContent);
        addView(view, params);
        EventBus.getDefault().register(this);
    }

    public void initData(OnChangeListener listener) {
        this.mListener = listener;
        if (cbRecv != null) {
            cbRecv.setOnCheckedChangeListener(this);
        }

        if (cbSend != null) {
            cbSend.setOnCheckedChangeListener(this);
        }

        if (btnSend != null) {
            btnSend.setOnClickListener(this);
        }

        if (btnClean != null) {
            btnClean.setOnClickListener(this);
        }
    }

    @Subscribe
    public void onEventMainThread(DataPointEvent event) {
        receiveData(event.getResult());
    }

    public void receiveData(String data) {
        DataContent dataContent = new DataContent();
        dataContent.setValue(data);
        dataContent.setTs(Utils.timestampToString(new Date().getTime() / 1000));
        dataContent.setSend(false);
        hisData.add(dataContent);
        updateContent(dataContent);
    }

    private void cleanRecv() {
        hisData.clear();
        layContent.removeAllViews();
    }

    private void sendMsg() {
        String content = etContent.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(getContext(), R.string.err_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        Utils.hiderSoftInput(etContent, getContext());
        DataContent dataContent = new DataContent();
        dataContent.setValue(content);
        dataContent.setTs(Utils.timestampToString(new Date().getTime() / 1000));
        dataContent.setSend(true);
        hisData.add(dataContent);
        updateContent(dataContent);
        etContent.setText("");

        this.mListener.onChanged(sendHex ? Utils.hexStr2Str(content) : content, null, DF_CUSTOM);
    }

    private void updateContent(DataContent content) {
        TextView tv = new TextView(getContext());
        tv.setTextColor(getResources().getColor(R.color.body_text_color));
        if (content.isSend()) {
            tv.setGravity(Gravity.END);
            String txt = content.getTs() + "\n";
            if (sendHex) {
                txt += Utils.hexStr2Str(content.getValue());
            } else {
                txt += content.getValue();
            }
            tv.setText(txt);
        } else {
            tv.setGravity(Gravity.START);
            String txt = content.getTs() + "\n";
            if (recvHex) {
                txt += Utils.str2HexStr(content.getValue());
            } else {
                txt += content.getValue();
            }
            tv.setText(txt);
        }
        layContent.addView(tv);
    }

    private void renderContent() {
        if (hisData != null) {
            layContent.removeAllViews();
            for (DataContent dataContent : hisData) {
                updateContent(dataContent);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClean:
                cleanRecv();
                break;
            case R.id.btnSend:
                sendMsg();
                break;
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cbRecv:
                recvHex = isChecked;
                renderContent();
                break;
            case R.id.cbSend:
                sendHex = isChecked;
                renderContent();
                break;
        }
    }
}
