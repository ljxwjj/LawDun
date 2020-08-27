package com.yunfa365.lawservice.app.pojo;

/**
 * Created by Administrator on 2016/4/21.
 */
public class CusTomCols {
    public int ID;
    public String Title;

    public CusTomCols(){}

    public CusTomCols(int i, String t) {
        ID = i;
        Title = t;
    }

    @Override
    public String toString() {
        return Title;
    }
}
