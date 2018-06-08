package com.molmc.intoyundemo.support.eventbus;

import java.util.Map;

/**
 * Created by hehui on 17/3/18.
 */

public class DataPointEvent {

    private String result;

    private Map<Integer, Object> payload;


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Map<Integer, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<Integer, Object> payload) {
        this.payload = payload;
    }
}
