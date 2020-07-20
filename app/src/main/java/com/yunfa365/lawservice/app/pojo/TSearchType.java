package com.yunfa365.lawservice.app.pojo;

/**
 * Created by Administrator on 2016/4/21.
 */
public class TSearchType {
    public int id;
    public String type;

    public TSearchType(int id, String type) {
        this.id = id;
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
