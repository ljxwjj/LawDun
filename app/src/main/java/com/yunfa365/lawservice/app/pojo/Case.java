package com.yunfa365.lawservice.app.pojo;

import com.google.gson.annotations.JsonAdapter;
import com.yunfa365.lawservice.app.pojo.base.CommonItem;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/4/29.
 */
public class Case implements Serializable, CommonItem {

    // 主要信息  （在列表中显示）
    public String ID;
    public String CaseIdTxt;
    public int ColsV1;
    public String AyMake;
    public int CustId;
    public int DCustId;
    public double CasePrice;
    public String BegTime;
    public int Stat;
    public int EndStat;
    public String ColsV1Txt;
    public String CustIdTxt;
    public String DCustIdTxt;
    public String DCustName;
    public String StatTxt;
    public String EndStatTxt;
    public String TempCols; // "MS"

    public double BuTiePrice;
    public double Price;
    public String CaseID;
    public String Title;
    public String LxRen;

    // 详细信息

    public String Slfy;
    public String Ssbd;
    public String Province;
    public String City;
    public String Sscx;
    public int Ssdw;
    public String Begtime;
    public String Des;
    public String CaseTime1;
    public String CaseTime2;
    public String PayCols;
    public String FengXianMake;
    public String IsBuTie;
    public String TWtr;
    public String AyTxt; // 案件分类
    public String TSscx;
    public String TDfdsr;
    public String DfdsrDW;
    public String TSsdw;
    public String Slfy1;   // 公安
    public String Slfy2;   // 检察院
    public String Slfy3;   // 法院
    public String Slfy4;   // 看守所

    public String AjXz;         //案件性质
    public String AjLy;         //案件来源
    public String DLShenFen;    //代理律师身份
    public int Lyct;            //0无冲突  1有冲突
    public String UserDefId;    //专属案号
    public String DiSanRen;     //第三人
    public String TongAnFan;    //同案犯
    public String AnYuanRen;    //案源人

    @Override
    public String getTitle() {
        return CaseIdTxt;
    }

    @Override
    public String getDesc() {
        String desc = "代理费：%.0f\n案由：%s\n委托人：%s\n对方当事人：%s\n收案日期：%s";
        return String.format(desc, CasePrice, AyMake, CustIdTxt, DCustIdTxt, BegTime);
    }

    @Override
    public String getStatus() {
        return StatTxt;
    }
}
