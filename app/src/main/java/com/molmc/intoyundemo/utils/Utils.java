package com.molmc.intoyundemo.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.molmc.intoyunsdk.bean.DataPointBean;
import com.molmc.intoyunsdk.bean.DeviceBean;
import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.base.IntoYunApplication;
import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * features:
 * Author：  hhe on 16-7-30 15:01
 * Email：   hhe@molmc.com
 */

public class Utils {

    private static int screenWidth;

    private static int screenHeight;
    private static DeviceBean device = new DeviceBean();
    private static List<DataPointBean> systemDataPoint = new ArrayList<>();

    public static int dip2px(int dipValue) {
        float reSize = IntoYunApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) ((dipValue * reSize) + 0.5);
    }

    public static int px2dip(int pxValue) {
        float reSize = IntoYunApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) ((pxValue / reSize) + 0.5);
    }

    public static float sp2px(int spValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, IntoYunApplication.getInstance().getResources().getDisplayMetrics());
    }


    private static void setScreenInfo() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) IntoYunApplication.getInstance().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }

    public static int getScreenWidth() {
        if (screenWidth == 0)
            setScreenInfo();
        return screenWidth;
    }

    public static int getScreenHeight() {
        if (screenHeight == 0)
            setScreenInfo();
        return screenHeight;
    }

    /**
     * 从topic中截取deviceId
     *
     * @param topic
     * @return
     */
    public static String getDeviceIdFromTopic(String topic) {
        Pattern topicPattern = Pattern.compile("^v2/device\\/([\\w]+)\\/");
        Matcher uidMatcher = topicPattern.matcher(topic);
        String deviceId = "";
        if (uidMatcher.find()) {
            deviceId = uidMatcher.group(1);
        }
        return deviceId;
    }

    public static void hideSoftInput(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view == null) view = new View(activity);
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hiderSoftInput(EditText edit, Context context) {
        edit.setFocusable(true);
        edit.setFocusableInTouchMode(true);
        edit.requestFocus();
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
    }

    public static void showSoftInput(EditText edit, Context context) {
        edit.setFocusable(true);
        edit.setFocusableInTouchMode(true);
        edit.requestFocus();
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edit, 0);
    }

    public static void toggleSoftInput(Context context) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }


    public static String getAvatarId(String imgSrc) {
        Pattern avatarPatten = Pattern.compile("(\\w+)\\/(\\w+)\\/(\\w+)");
        Matcher avatarMatcher = avatarPatten.matcher(imgSrc);

        String avatarId = "";
        if (avatarMatcher.find()) {
            avatarId = avatarMatcher.group(3);
        }
        Logger.i("avatarId: " + avatarId);
        return avatarId;
    }

    public static String timestampToString(long s_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        re_StrTime = sdf.format(new Date(s_time * 1000L));
        return re_StrTime;
    }

    public static String getDatapointName(Context context, DataPointBean datapoint) {
        if (isZh(context)){
            if (!TextUtils.isEmpty(datapoint.getNameCn())){
                return datapoint.getNameCn();
            }
        } else {
            if (!TextUtils.isEmpty(datapoint.getNameEn())){
                return datapoint.getNameEn();
            }
        }
        return "";
    }


    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }

    public static DeviceBean SYSTEM_DEVICE(Context context) {
        device.setDeviceId(Constant.SYSTEM_DEVICE_ID);
        device.setName(context.getString(R.string.system_device_name));
        device.setPidImp(Constant.SYSTEM_PRODUCT_ID);
        return device;
    }

    public static List<DataPointBean> SYSTEM_DATA_POINTS(Context context) {
        systemDataPoint.clear();
        DataPointBean timer = new DataPointBean();
        timer.setDpId(Constant.SYSTEM_DATAPOINT_TIMER);
        timer.setNameCn(context.getString(R.string.system_data_point_timer));
        timer.setNameEn(context.getString(R.string.system_data_point_timer));
        timer.setDirection(Constant.TRANSFROM_DATA);
        timer.setType("timer");
        systemDataPoint.add(timer);

        DataPointBean email = new DataPointBean();
        email.setDpId(Constant.SYSTEM_DATAPOINT_EMAIL);
        email.setNameCn(context.getString(R.string.system_data_point_email));
        email.setNameEn(context.getString(R.string.system_data_point_email));
        email.setDirection(Constant.TRANSFROM_CMD);
        email.setType(Constant.RECIPE_ACTION_EMAIL);
        systemDataPoint.add(email);

        DataPointBean message = new DataPointBean();
        message.setDpId(Constant.SYSTEM_DATAPOINT_MESSAGE);
        message.setNameCn(context.getString(R.string.system_data_point_message));
        message.setNameEn(context.getString(R.string.system_data_point_message));
        message.setDirection(Constant.TRANSFROM_CMD);
        message.setType(Constant.RECIPE_ACTION_MSGBOX);
        systemDataPoint.add(message);
        return systemDataPoint;
    }

    public static String toDecimal(float value, DataPointBean dataPoint){
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

    public static int parseDataPointType(DataPointBean dataPointBean){
        if (Constant.BOOL_DT.equals(dataPointBean.getType())){
            return 0;
        } else if (Constant.NUMBER_DT.equals(dataPointBean.getType())){
            return 1;
        } else if (Constant.ENUM_DT.equals(dataPointBean.getType())){
            return 2;
        } else if (Constant.STRING_DT.equals(dataPointBean.getType())){
            return 3;
        } else if (Constant.EXTRA_DT.equals(dataPointBean.getType())){
            return 4;
        }
        return 0;
    }


    //将数值型数据装换成服务器的整形数据
    public static int parseInt(String data, DataPointBean dataPointBean){
        float fData = Float.valueOf(data);
        return (int) ((fData - dataPointBean.getMin()) * Math.pow(10, dataPointBean.getResolution()));
    }

    //将服务器的整形数据装换回数值类型数据
    public static float parseFloat(float data, DataPointBean dataPointBean){
        return (float) (data/Math.pow(10, dataPointBean.getResolution())) + dataPointBean.getMin();
    }
}
