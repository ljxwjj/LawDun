package com.yunfa365.lawservice.app.pojo.base;

public class BaseBean {
    public int ID;
    public String Title;

    public BaseBean(){}

    public BaseBean(int i, String t) {
        ID = i;
        Title = t;
    }

    @Override
    public String toString() {
        return Title;
    }
}
