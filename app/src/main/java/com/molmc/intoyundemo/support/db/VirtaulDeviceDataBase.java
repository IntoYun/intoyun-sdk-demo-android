package com.molmc.intoyundemo.support.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.molmc.intoyunsdk.bean.DeviceBean;
import com.molmc.intoyunsdk.utils.IntoUtil;
import com.molmc.intoyunsdk.utils.IntoYunSharedPrefs;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehui on 17/3/27.
 */

public class VirtaulDeviceDataBase {

    private static final String DB_NAME = "intoyun_sdk_demo";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "virtual_device";
    private static DBOpenHelper dbOpenHelper;

    private static final String DEVICE_ID = "deviceId";
    private static final String BIND_USER = "bindUser";
    private static final String BOARD = "board";
    private static final String TOKEN = "token";
    private static final String IMAGE_SRC = "imgSrc";
    private static final String NAME = "name";
    private static final String PIDIMP = "pidImp";
    private static final String BINDAT = "bindAt";
    private static final String ACCESS_MODE = "accessMode";
    private static final String DESCRIPTION = "description";
    private static final String STATUS = "status";
    private static final String UID = "uid";

    private Context sContext;
    private static VirtaulDeviceDataBase deviceDataBase;

    public VirtaulDeviceDataBase(Context context) {
        sContext = context;
        dbOpenHelper = DBOpenHelper.getInstance(context, DB_NAME, DB_VERSION);
    }

    public static VirtaulDeviceDataBase getInstance(Context context) {
        if (deviceDataBase == null) {
            deviceDataBase = new VirtaulDeviceDataBase(context);
        }
        return deviceDataBase;
    }


    public static List<String> getTableSql(){
        List<String> table = new ArrayList<>();
        String tableSql = new StringBuffer().
                append("CREATE TABLE IF NOT EXISTS ").append(TABLE_NAME).
                append("(").
                append(DEVICE_ID).append(" TEXT PRIMARY KEY NOT NULL,").
                append(BIND_USER).append(" TEXT,").
                append(BOARD).append(" TEXT,").
                append(TOKEN).append(" TEXT,").
                append(IMAGE_SRC).append(" TEXT,").
                append(NAME).append(" TEXT,").
                append(PIDIMP).append(" TEXT,").
                append(BINDAT).append(" INTEGER DEFAULT 0,").
                append(DESCRIPTION).append(" TEXT,").
                append(ACCESS_MODE).append(" TEXT,").
                append(STATUS).append(" BOOLEAN, ").
                append(UID).append(" TEXT ").
                append(");").toString();
        table.add(tableSql);
        Logger.i("sql : " + tableSql);
        return table;
    }

    // 保存设备列表
    public void saveDevices(List<DeviceBean> devices) {
        if (IntoUtil.Empty.check(devices)){
            return;
        }
        dbOpenHelper.clear();
        for (DeviceBean device: devices) {
            //调用insert()方法插入数据
//            updateDevice(device);
            dbOpenHelper.replace(TABLE_NAME, getDeviceContentValue(device));
        }
    }


    public void saveDevice(DeviceBean device){
        dbOpenHelper.replace(TABLE_NAME, getDeviceContentValue(device));
    }

    public void updateDevice(DeviceBean device){
        if (IntoUtil.Empty.check(device)){
            return;
        }
        //修改条件
        String whereClause = "deviceId = ? and UID = ?";
        //修改添加参数
        String[] whereArgs={device.getDeviceId(), IntoYunSharedPrefs.getUserInfo(sContext).getUid()};

        dbOpenHelper.update(TABLE_NAME, getDeviceContentValue(device), whereClause, whereArgs);
    }

    public DeviceBean getDeviceById(String deviceId){
        //修改条件
        String whereClause = "deviceId = ? and UID = ?";
        //修改添加参数
        String[] whereArgs={deviceId, IntoYunSharedPrefs.getUserInfo(sContext).getUid()};

        try {
            Cursor cursor = dbOpenHelper.query(TABLE_NAME, null, whereClause, whereArgs, null, null, null);
            while(cursor.moveToNext()){
                DeviceBean device = parseData(cursor);
                cursor.close();
                return device;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<DeviceBean> getDevices(){
        //修改条件
        String whereClause = "UID = ?";
        //修改添加参数
        String[] whereArgs={IntoYunSharedPrefs.getUserInfo(sContext).getUid()};

        try {
            Cursor cursor = dbOpenHelper.query(TABLE_NAME, null, whereClause, whereArgs, null, null, null);
            List<DeviceBean> deviceList = new ArrayList<>();
            while(cursor.moveToNext()){
                deviceList.add(parseData(cursor));
            }
            cursor.close();
            return deviceList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteDeviceById(String deviceId){
        //修改条件
        String whereClause = "deviceId = ? and UID = ?";
        //修改添加参数
        String[] whereArgs={deviceId, IntoYunSharedPrefs.getUserInfo(sContext).getUid()};

        dbOpenHelper.delete(TABLE_NAME, whereClause, whereArgs);
    }

    private DeviceBean parseData(Cursor cursor){
        DeviceBean device = new DeviceBean();
        device.setDeviceId(cursor.getString(cursor.getColumnIndex(DEVICE_ID)));
        device.setBindUser(cursor.getString(cursor.getColumnIndex(BIND_USER)));
        device.setBoard(cursor.getString(cursor.getColumnIndex(BOARD)));
        device.setToken(cursor.getString(cursor.getColumnIndex(TOKEN)));
        device.setImgSrc(cursor.getString(cursor.getColumnIndex(IMAGE_SRC)));
        device.setName(cursor.getString(cursor.getColumnIndex(NAME)));
        device.setPidImp(cursor.getString(cursor.getColumnIndex(PIDIMP)));
        device.setBindAt(cursor.getLong(cursor.getColumnIndex(BINDAT)));
        device.setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
        device.setAccessMode(cursor.getString(cursor.getColumnIndex(ACCESS_MODE)));
        device.setStatus(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(STATUS))));
        return device;
    }

    private ContentValues getDeviceContentValue(DeviceBean device){
        //实例化常量值
        ContentValues cValue = new ContentValues();
        cValue.put(DEVICE_ID, device.getDeviceId());
        cValue.put(BIND_USER, device.getBindUser());
        cValue.put(BOARD, device.getBoard());
        cValue.put(TOKEN, device.getToken());
        cValue.put(IMAGE_SRC, device.getImgSrc());
        cValue.put(NAME, device.getName());
        cValue.put(PIDIMP, device.getPidImp());
        cValue.put(BINDAT, device.getBindAt());
        cValue.put(DESCRIPTION, device.getDescription());
        cValue.put(ACCESS_MODE, device.getAccessMode());
        cValue.put(STATUS, device.getStatus());
        cValue.put(UID, IntoYunSharedPrefs.getUserInfo(sContext).getUid());
        return cValue;
    }

    public void closeDb(){
        dbOpenHelper.close();
    }

}
