package com.yunfa365.lawservice.app.pojo;

import com.yunfa365.lawservice.app.pojo.base.CommonItem;

import java.io.Serializable;

public class OfficialRecord implements CommonItem, Serializable {

    public int ID;
    public int LawId;
    public int Uid;
    public int FileId;
    public int FileYear;
    public int CaseId;
    public String CaseIdTxt;
    public int SCols;
    public String Title;
    public String Make;
    public int ZNums;
    public int GNums;    // 盖章次数
    public int WGNums;   //剩余盖章次数
    public int Stat;
    public String Addtime;
    public String UsersFullName;
    public String FileIdTxt;
    public String SColsTxt;
    public String StatTxt;

    @Override
    public String getTitle() {
        return FileIdTxt;
    }

    @Override
    public String getDesc() {
        String desc = "用印类型：%s\n用印名称：%s\n案号：%s\n申请盖章：%d次\n申请日期：%s";
        return String.format(desc, SColsTxt, Title, CaseIdTxt, ZNums, Addtime);
    }

    @Override
    public String getStatus() {
        return StatTxt;
    }
}
