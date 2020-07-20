package com.yunfa365.lawservice.app.pojo;

/**
 * Created by Administrator on 2016/4/21.
 */
public class CusTomCols {
    public int ID;
    public int Fid;
    public String Title;
    public int Cols;
    public int Sort;
    public CusTomCols[] Children;
    public CusTomCols[] Item;

    @Override
    public String toString() {
        return Title;
    }
}
