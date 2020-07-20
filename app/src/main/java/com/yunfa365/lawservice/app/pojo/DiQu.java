package com.yunfa365.lawservice.app.pojo;

/**
 * Created by Administrator on 2016/4/21.
 */
public class DiQu {
    public int id;
    public String text;
    public DiQu[] children;

    @Override
    public String toString() {
        return text;
    }

    public String getN() {
        return text;
    }

    public String getI() {
        return id + "";
    }
}
