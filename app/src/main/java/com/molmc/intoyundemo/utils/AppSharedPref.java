package com.molmc.intoyundemo.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.molmc.intoyunsdk.bean.BoardInfoBean;
import com.orhanobut.logger.Logger;

import java.util.Map;

/**
 * Created by hehui on 17/3/16.
 */

public class AppSharedPref extends BaseSharePref {

    private static final String SP_NAME = "intoyun_sdk_dome";
    private static final String USER_ACCOUNT = "USER_ACCOUNT";
    private static final String USER_PASSWORD = "USER_PASSWORD";
    private static final String BOARD_INFO = "BOARD_INFO";

    private static AppSharedPref instance;

    private AppSharedPref(Context context) {
        super(context, SP_NAME);
    }

    public static AppSharedPref getInstance(Context context) {
        if (instance == null) {
            synchronized (AppSharedPref.class) {
                if (instance == null) {
                    instance = new AppSharedPref(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public void saveUserAccount(String account) {
        putString(USER_ACCOUNT, account);
    }

    public String getUserAccount() {
        return getString(USER_ACCOUNT, "");
    }

    public void saveUserPassword(String password) {
        putString(USER_PASSWORD, password);
    }

    public String getUserPassword() {
        return getString(USER_PASSWORD, "");
    }

    public void saveMessageBadge(String uid, int badge) {
        Logger.i("saveMessageBadge: " + uid + "; " + badge);
        putInt("msgbadge" + uid, badge);
    }

    public int getMessageBadge(String uid) {
        int result = getInt("msgbadge" + uid, 0);
        Logger.i("getMessageBadge: " + uid + "; " + result);
        return result;
    }

    /**
     * 存储boardinfo
     * @param boardInfoBeanMap
     */
    public void saveBoardInfo(Map<String, BoardInfoBean> boardInfoBeanMap){
        putString(BOARD_INFO, new Gson().toJson(boardInfoBeanMap));
    }

    /**
     * 获取BoardInfoBean
     * @return
     */
    public Map<String, BoardInfoBean>  getBoarInfo(){
        String result = getString(BOARD_INFO, "");
        Map<String, BoardInfoBean> boardInfoBeanMap = new Gson().fromJson(result, new TypeToken<Map<String, BoardInfoBean>>(){}.getType());
        return boardInfoBeanMap;
    }

    /**
     * 获取BoardInfoBean by board
     * @param board
     * @return
     */
    public BoardInfoBean getBoarInfo(String board){
        String result = getString(BOARD_INFO, "");
        Map<String, BoardInfoBean> boardInfoBeanMap = new Gson().fromJson(result, new TypeToken<Map<String, BoardInfoBean>>(){}.getType());
        return boardInfoBeanMap.get(board);
    }
}
