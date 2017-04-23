package com.molmc.intoyundemo.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by hehui on 17/3/23.
 */

public class BaseSharePref {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    protected BaseSharePref(Context context, String dbName) {
        sharedPreferences = context.getSharedPreferences(dbName, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    protected boolean contains(String key) {
        return sharedPreferences.contains(key);
    }

    protected void clear() {
        editor.clear().commit();
    }

    protected void remove(String key) {
        editor.remove(key);
        editor.commit();
    }

    protected void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    protected int getInt(String key, int defValue) {
        return sharedPreferences.getInt(key, defValue);
    }

    protected void putLong(String key, Long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    protected long getLong(String key, long defValue) {
        return sharedPreferences.getLong(key, defValue);
    }


    protected void putBoolean(String key, Boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    protected boolean getBoolean(String key, boolean defValue) {
        return sharedPreferences.getBoolean(key, defValue);
    }


    protected void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    protected String getString(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }
}
