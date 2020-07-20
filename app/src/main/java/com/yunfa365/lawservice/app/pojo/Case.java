package com.yunfa365.lawservice.app.pojo;

import com.google.gson.annotations.JsonAdapter;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/4/29.
 */
public class Case implements Serializable {

    // 主要信息  （在列表中显示）
    public String ID;
    public int Cols;
    public String AyMake;
    public int Wtr;
    public int Dfdsr;
    public String AddTime;
    public double BuTiePrice;
    public int Uid;
    public String UserList;
    public double Price;
    public String CaseID;
    public String ZBLS;
    public String CustId;
    public String Title;
    public String LxRen;
    public String DfTxt;
    public String WtrName;

    // 详细信息
    public int Ay;
    public int Cbr;
    public int Cbls;
    public String Slfy;
    public String Ssbd;
    public double ZPrice;
    public int IsFrom;
    public String Province;
    public String City;
    public String Sscx;
    public int Ssdw;
    public String Begtime;
    public String Des;
    public String EndTime;
    public int EndCols;
    public String GdPriceStat;
    public String NCaseId;
    public String CaseTime1;
    public String CaseTime2;
    public String CaseTime3;
    public String CaseTime4;
    public int IsYuLiu;
    public int Stat;
    public String SpUsers;
    public int GongXiang;
    public String WPrice;
    public String PayCols;
    public String FengXianMake;
    public String IsBuTie;
    public String Model;
    public int PostData;
    public int IsGd;
    public String PostId;
    public int IsZzGd;
    public double SPrice;
    public double PPrice;
    public int IsDaGd;
    public String GdNums;
    public String TWtr;
    public String ColsTxt; // 案件类型
    public String AyTxt; // 案件分类
    public String TSscx;
    public String TDfdsr;
    public String DfdsrDW;
    public String TempID;
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
}
