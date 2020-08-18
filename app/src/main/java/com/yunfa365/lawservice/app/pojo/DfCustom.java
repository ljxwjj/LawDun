package com.yunfa365.lawservice.app.pojo;

import com.yunfa365.lawservice.app.pojo.base.CommonItem;

public class DfCustom implements CommonItem {

    public int ID;
    public int Cols;
    public int LawId;
    public int Uid;
    public String Title;
    public int CustCols;
    public String CaseUName;
    public String Addtime;
    public String CaseCount;

    @Override
    public String getTitle() {
        return Title;
    }

    @Override
    public String getDesc() {
        String desc = "关联案件：%s件\n入库时间：%s";
        return String.format(desc, CaseCount, Addtime);
    }

    @Override
    public String getStatus() {
        return "";
    }
}
