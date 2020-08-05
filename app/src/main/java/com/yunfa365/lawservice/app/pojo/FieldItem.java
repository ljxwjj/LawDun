package com.yunfa365.lawservice.app.pojo;

/**
 * Created by Administrator on 2017/12/7.
 */

public class FieldItem {
    public int ID;
    public String Title;
    public int Sort;

    public FieldItem(){}

    public FieldItem(int id, String title) {
        ID = id;
        Title = title;
    }

    @Override
    public String toString() {
        return Title;
    }
}
