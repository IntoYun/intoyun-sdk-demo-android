package com.molmc.intoyundemo.support;

import com.molmc.intoyunsdk.bean.RecipeBean;

/**
 * Created by hehui on 17/3/27.
 */

public interface RecipeChangeListener {

    void onTriggerChange(RecipeBean.TriggerValBean triggerVal);

    void onActionChange(RecipeBean.ActionValBean actionVal);

    void onCrontabChange(RecipeBean.CrontabBean crontab);
}
