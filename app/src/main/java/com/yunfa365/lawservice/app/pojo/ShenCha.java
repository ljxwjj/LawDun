package com.yunfa365.lawservice.app.pojo;

import com.yunfa365.lawservice.app.pojo.base.CommonItem;

public class ShenCha implements CommonItem {

    public int ID;
    public String CaseIdTxt;
    public String AyMake;
    public int CustId;
    public int DCustId;
    public String UsersList;
    public String BegTime;
    public int EndStat;
    public String CustName;
    public String DCustName;
    public String UsersListName;
    public String EndStatTxt;

    @Override
    public String getTitle() {
        return CaseIdTxt;
    }

    @Override
    public String getDesc() {
        String desc = "执业人员：%s\n案件：%s\n委托人：%s\n对方当事人：%s\n收案日期：%s";
        return String.format(desc, UsersListName, AyMake, CustName, DCustName, BegTime);
    }

    @Override
    public String getStatus() {
        return EndStatTxt;
    }
}
