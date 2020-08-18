package com.yunfa365.lawservice.app.pojo;

import com.yunfa365.lawservice.app.pojo.base.CommonItem;

public class Audit implements CommonItem {

    public int ID;
    public int LawId;
    public int Gid;
    public int FUid;
    public int Sid;
    public int CaseId;
    public int Gdid;
    public String UsersList;
    public String Make;
    public String AddTime;
    public int Stat;
    public int Suid;
    public String SMake;
    public String STime;
    public String UsersFullName;
    public String UsersListTxt;
    public String CaseIdTxt;
    public String GidTxt;
    public String StatTxt;
    public String SUidTxt;

    @Override
    public String getTitle() {
        return GidTxt;
    }

    @Override
    public String getDesc() {
        return Make.replaceAll("；", "\n");
    }

    @Override
    public String getStatus() {
        return StatTxt;
    }
}
