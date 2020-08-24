package com.yunfa365.lawservice.app.pojo;

import com.yunfa365.lawservice.app.pojo.base.CommonItem;

public class PushMessage implements CommonItem {
    public int ID;
    public int Lawid;
    public int Uid;
    public int SCols;
    public int Sid;
    public int CaseId;
    public String Title;
    public String Make;
    public String STime;
    public String BegTime;
    public int Stat;
    public int WStat;
    public String Addtime;
    public String SColsTxt;
    public String WxUrl;

    @Override
    public String getTitle() {
        return SColsTxt;
    }

    @Override
    public String getDesc() {
        return Make.replaceAll("；", "\n");
    }

    @Override
    public String getStatus() {
        return BegTime;
    }
}
