package com.molmc.intoyundemo.support.eventbus;

/**
 * Created by hehui on 17/3/29.
 */

public class UpdateRecipe {

    private String type;

    public UpdateRecipe(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
