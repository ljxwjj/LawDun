package com.yunfa365.lawservice.app.pojo;

/**
 * Created by Administrator on 2016/4/21.
 */
public class YesNo {
    public int id;
    public String text;

    public YesNo(int id, String text) {
        this.id = id;
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
