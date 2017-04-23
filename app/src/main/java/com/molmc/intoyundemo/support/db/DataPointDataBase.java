package com.molmc.intoyundemo.support.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.molmc.intoyunsdk.bean.DataPointBean;
import com.molmc.intoyunsdk.utils.IntoUtil;
import com.molmc.intoyunsdk.utils.IntoYunSharedPrefs;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hehui on 17/3/27.
 */

public class DataPointDataBase {

    private static final String DB_NAME = "intoyun_sdk_demo";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "datapoint";
    private static DBOpenHelper dbOpenHelper;

    private static final String PRIMARY_KEY = "primary_key";
    private static final String PRODUCT_ID = "product_id";
    private static final String MAX = "ml_max";
    private static final String DPID = "dpId";
    private static final String NAME_CN = "nameCN";
    private static final String NAME_EN = "nameEn";
    private static final String DIRECTION = "direction";
    private static final String UNIT = "ml_unit";
    private static final String TYPE = "type";
    private static final String RESOLUTION = "resolution";
    private static final String DESCRIPTION = "description";
    private static final String MIN = "ml_min";
    private static final String MAXLENGHT = "maxLength";
    private static final String ML_ENUM = "ml_enum";
    private static final String UID = "uid";

    private Context sContext;
    private static DataPointDataBase deviceDataBase;

    public DataPointDataBase(Context context) {
        sContext = context;
        dbOpenHelper = DBOpenHelper.getInstance(context, DB_NAME, DB_VERSION);
    }

    public static DataPointDataBase getInstance(Context context) {
        if (deviceDataBase == null) {
            deviceDataBase = new DataPointDataBase(context);
        }
        return deviceDataBase;
    }


    public static List<String> getTableSql(){
        List<String> table = new ArrayList<>();
        String tableSql = new StringBuffer().
                append("CREATE TABLE IF NOT EXISTS ").append(TABLE_NAME).
                append("(").
                append(PRIMARY_KEY).append(" TEXT PRIMARY KEY NOT NULL,").
                append(PRODUCT_ID).append(" TEXT ,").
                append(MAX).append(" INTEGER DEFAULT 0,").
                append(DPID).append(" INTEGER DEFAULT 0,").
                append(NAME_CN).append(" TEXT,").
                append(NAME_EN).append(" TEXT,").
                append(UNIT).append(" TEXT,").
                append(TYPE).append(" TEXT,").
                append(DIRECTION).append(" INTEGER DEFAULT 0,").
                append(DESCRIPTION).append(" TEXT,").
                append(RESOLUTION).append(" INTEGER DEFAULT 0,").
                append(MIN).append(" INTEGER DEFAULT 0, ").
                append(MAXLENGHT).append(" INTEGER DEFAULT 0, ").
                append(ML_ENUM).append(" TEXT, ").
                append(UID).append(" TEXT ").
                append(");").toString();
        table.add(tableSql);
        Logger.i("sql : " + tableSql);
        return table;
    }

    // 保存设备列表
    public void saveDataPoints(Map<String, List<DataPointBean>> dataPoints) {
        if (IntoUtil.Empty.check(dataPoints)){
            return;
        }
        for (String productId : dataPoints.keySet()) {
            //调用insert()方法插入数据
            for (DataPointBean dataPoint : dataPoints.get(productId)){
                dbOpenHelper.replace(TABLE_NAME, getDataPointContentValue(dataPoint, productId));
            }
        }
    }

    public void updataDataPoint(DataPointBean dataPoint, String productId){
        if (IntoUtil.Empty.check(dataPoint)){
            return;
        }
        //修改条件
        String whereClause = String.format("%s = ? and %s = ?", PRIMARY_KEY, UID);
        //修改添加参数
        String[] whereArgs={productId+dataPoint.getDpId(), IntoYunSharedPrefs.getUserInfo(sContext).getUid()};

        dbOpenHelper.update(TABLE_NAME, getDataPointContentValue(dataPoint, productId), whereClause, whereArgs);
    }

    public List<DataPointBean> getDataPoints(String productId){
        //修改条件
        String whereClause = String.format("%s = ? and %s = ?", PRODUCT_ID, UID);
        //修改添加参数
        String[] whereArgs={productId, IntoYunSharedPrefs.getUserInfo(sContext).getUid()};

        try {
            Cursor cursor = dbOpenHelper.query(TABLE_NAME, null, whereClause, whereArgs, null, null, null);
            List<DataPointBean> dataPointList = new ArrayList<>();
            while(cursor.moveToNext()){
                dataPointList.add(parseData(cursor));
            }
            cursor.close();
            return dataPointList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public DataPointBean getDataPoint(String productId, int dpid){
        //修改条件
        String whereClause = String.format("%s = ? and %s = ?", PRIMARY_KEY, UID);
        //修改添加参数
        String[] whereArgs={productId+dpid, IntoYunSharedPrefs.getUserInfo(sContext).getUid()};

        try {
            Cursor cursor = dbOpenHelper.query(TABLE_NAME, null, whereClause, whereArgs, null, null, null);
            while(cursor.moveToNext()){
                DataPointBean dataPoint = parseData(cursor);
                cursor.close();
                return dataPoint;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void deleteDataPoints(String productId){
        //修改条件
        String whereClause = String.format("%s = ? and %s = ?");
        //修改添加参数
        String[] whereArgs={productId, IntoYunSharedPrefs.getUserInfo(sContext).getUid()};

        dbOpenHelper.delete(TABLE_NAME, whereClause, whereArgs);
    }

    private DataPointBean parseData(Cursor cursor){
        DataPointBean dataPoint = new DataPointBean();
        dataPoint.setMax(cursor.getInt(cursor.getColumnIndex(MAX)));
        dataPoint.setDpId(cursor.getInt(cursor.getColumnIndex(DPID)));
        dataPoint.setNameCn(cursor.getString(cursor.getColumnIndex(NAME_CN)));
        dataPoint.setNameEn(cursor.getString(cursor.getColumnIndex(NAME_EN)));
        dataPoint.setDirection(cursor.getInt(cursor.getColumnIndex(DIRECTION)));
        dataPoint.setUnit(cursor.getString(cursor.getColumnIndex(UNIT)));
        dataPoint.setResolution(cursor.getInt(cursor.getColumnIndex(RESOLUTION)));
        dataPoint.setType(cursor.getString(cursor.getColumnIndex(TYPE)));
        dataPoint.setMin(cursor.getInt(cursor.getColumnIndex(MIN)));
        dataPoint.setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
        dataPoint.setMaxLength(cursor.getInt(cursor.getColumnIndex(MAXLENGHT)));
        List<Object> list = new Gson().fromJson(cursor.getString(cursor.getColumnIndex(ML_ENUM)), new TypeToken<List<Object>>(){}.getType());
        dataPoint.set_enum(list);
        return dataPoint;
    }


    private ContentValues getDataPointContentValue(DataPointBean dataPoint, String productId){
        //实例化常量值
        ContentValues cValue = new ContentValues();
        cValue.put(PRIMARY_KEY, productId + dataPoint.getDpId());
        cValue.put(PRODUCT_ID, productId);
        cValue.put(MAX, dataPoint.getMax());
        cValue.put(DPID, dataPoint.getDpId());
        cValue.put(NAME_CN, dataPoint.getNameCn());
        cValue.put(NAME_EN, dataPoint.getNameEn());
        cValue.put(DIRECTION, dataPoint.getDirection());
        cValue.put(UNIT, dataPoint.getUnit());
        cValue.put(TYPE, dataPoint.getType());
        cValue.put(RESOLUTION, dataPoint.getResolution());
        cValue.put(MIN, dataPoint.getMin());
        cValue.put(DESCRIPTION, dataPoint.getDescription());
        cValue.put(MAXLENGHT, dataPoint.getMaxLength());
        cValue.put(ML_ENUM, new Gson().toJson(dataPoint.get_enum()));
        cValue.put(UID, IntoYunSharedPrefs.getUserInfo(sContext).getUid());
        return cValue;
    }

    public void closeDb(){
        dbOpenHelper.close();
    }

}
