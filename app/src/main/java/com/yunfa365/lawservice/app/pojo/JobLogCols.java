package com.yunfa365.lawservice.app.pojo;

public class JobLogCols {
    public int ID;
    public int Fid;
    public String Title;
    public int Sort;
    public int SelectCase;
    public int SelectCust;

    @Override
    public String toString() {
        return Title;
    }
}
