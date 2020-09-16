package com.yunfa365.lawservice.app.pojo;

import com.google.gson.annotations.JsonAdapter;
import com.yunfa365.lawservice.app.gson.UrlDecoderJsonAdapter;
import com.yunfa365.lawservice.app.pojo.base.CommonItem;

import java.io.Serializable;

public class JobLog implements Serializable, CommonItem {
    public int ID;
    public int LawId;
    public int Uid;
    public int V1;
    public int V2;
    public int CaseId;
    public String CaseIdTxt;
    public int CustId;
    public String CustName;
    public String BegTime;
    public String EndTime;
    @JsonAdapter(UrlDecoderJsonAdapter.class)
    public String FilePath;
    public String Des;
    public String AddTime;
    public String UsersFullName;
    public String V1Txt;
    public String V2Txt;
    public String LongTimeTxt;

    @Override
    public String getTitle() {
        return String.format("%s —— %s", V1Txt, V2Txt);
    }

    @Override
    public String getDesc() {
        String des = "案件：%s \n客户：%s \n开始时间：%s \n工作时长：%s \n";
        return String.format(des, CaseIdTxt, CustName, BegTime, LongTimeTxt);
    }

    @Override
    public String getStatus() {
        return "";
    }
}
