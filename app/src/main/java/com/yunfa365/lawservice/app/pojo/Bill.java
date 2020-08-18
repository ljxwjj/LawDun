package com.yunfa365.lawservice.app.pojo;

import com.yunfa365.lawservice.app.pojo.base.CommonItem;

public class Bill implements CommonItem {
    public int ID;
    public int LawId;
    public int Uid;
    public int Cid;
    public int CaseId;
    public String CaseIdTxt;
    public String BTitle;
    public double Price;
    public int BillCols;
    public String Make;
    public String Addtime;
    public int Stat;
    public int SUid;
    public String BillTime;
    public String BillNums;
    public String UidTxt;
    public String BillColsTxt;
    public String StatTxt;
    public String SUidTxt;

    @Override
    public String getTitle() {
        return BTitle;
    }

    @Override
    public String getDesc() {
        String desc = "发票类型：%s\n开票金额：%.0f元\n案号：%s\n申请人：%s\n申请日期：%s";
        return String.format(desc, BillColsTxt, Price, CaseIdTxt, UidTxt, Addtime);

    }

    @Override
    public String getStatus() {
        return StatTxt;
    }
}
