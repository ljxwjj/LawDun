package com.yunfa365.lawservice.app.pojo;

import com.google.gson.annotations.JsonAdapter;
import com.yunfa365.lawservice.app.gson.FormatDateJsonAdapter;
import com.yunfa365.lawservice.app.pojo.base.CommonItem;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/4/29.
 */
public class Case implements Serializable, CommonItem {

    // 主要信息  （在列表中显示）
    public int ID;
    public int CaseYear;
    public int CaseNums;
    public String CaseIdTxt;
    public int LawId;
    public int Uid;
    public int ColsV1;
    public int ColsV2;
    public String AyMake;
    public int CustId;
    public int DCustId;

    public double CasePrice;
    public double ZPrice;
    public int IsFrom;    //是否指派
    @JsonAdapter(FormatDateJsonAdapter.class)
    public String BegTime;
    public int Stat;
    public int EndStat;
    public String ColsV1Txt;
    public String ColsV2Txt;
    public String CustIdTxt;
    public String DCustIdTxt;
    public String DCustName;
    public String StatTxt;
    public String EndStatTxt;
    public String TempCols; // "MS"

    public String CaseID;
    public String Title;
    public String LxRen;

    // 详细信息

    public String Slfy;    // 受理法院
    public String Ssbd;    // 诉讼地位
    public int ProvinceId, CityId,  AreaId;
    public String Sscx; // 诉讼阶段
    public String SscxTxt;
    public int Ssdw;    // 诉讼地位
    public String SsdwTxt;
    public String Des;
    public int PayCols;
    public String FengXianMake;
    public int IsBuTie;     // 就否政府补助
    public double BuTiePrice;  // 补助金额
    public int BillStat;       // 开票状态
    @JsonAdapter(FormatDateJsonAdapter.class)
    public String CaseTime1;
    @JsonAdapter(FormatDateJsonAdapter.class)
    public String CaseTime2;
    @JsonAdapter(FormatDateJsonAdapter.class)
    public String CaseTime3;
    @JsonAdapter(FormatDateJsonAdapter.class)
    public String CaseTime4;
    public String UsersList; // 执业人员ID
    public String UsersListTxt; // 执业人员
    public String AddTime;
    public int GdStat;       // ?
    public double SPrice;    // ?
    public double PPrice;    // ?
    public String GdNums;    // ?
    public int JinDuId;      // ?
    public String ProvinceIdTxt, CityIdTxt, AreaIdTxt;
    public String PayColsTxt;

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

    public String getCaseInfo() {
        String desc = "案号：%s\n代理费：%.0f\n案由：%s\n委托人：%s\n对方当事人：%s\n收案日期：%s";
        return String.format(desc,CaseIdTxt, CasePrice, AyMake, CustIdTxt, DCustIdTxt, BegTime);
    }
}
