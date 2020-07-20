package com.yunfa365.lawservice.app.pojo;

import com.google.gson.annotations.JsonAdapter;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/4/21.
 */
public class Custom implements Serializable {

    public int ID;
    public int Uid;
    public String Title;
    @Deprecated // 可能要被删掉
    public String LxRen;
    public String Phone;
    public String Province;
    public String City;
    public int CustCols;
    public String Model;
    public String Make;
    public String FilePath;
    public String Addtime;
    public String Sign;
    public String IdCard;
    public String UNums;

    public String YwRen;              // 业务联系人
    public String YwRenZhiWu;         // 职务
    public String FzRen;              //   主要负责人
    public String YingXiangLi;        //  地区影响力
    public String Phone2;             //   固定电话
    public String Email;              //     邮件
    public String Address;            //   详细地址


    public String CustColsName;
}
