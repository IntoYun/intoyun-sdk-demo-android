package com.molmc.intoyundemo.support.views;

import com.molmc.intoyunsdk.bean.DataPointBean;

/**
 * Created by hehui on 17/3/17.
 */

public interface OnChangeListener {

    void onChanged(Object payload, DataPointBean dataPoint, String dataFormat);
}
