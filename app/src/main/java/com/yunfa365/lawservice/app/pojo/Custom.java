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
    public String Mobile;
    public int ProvinceId;
    public String ProvinceIdTxt;
    public int CityId;
    public String CityIdTxt;
    public int CustCols;              // 客户类型 公民个人、公司企业……
    public String CustColsTxt;
    public String Addtime;
    public String UNums;              // 身份证号/营业执照号
    public String Model = "";

    public String YwRen;              // 业务联系人
    public String YwRenZhiWu;         // 职务
    public String FzRen;              //   主要负责人
    public String YingXiangLi;        //  地区影响力
    public String Email;              //     邮件
    public String Address;            //   详细地址
    public String Make;               // 备注
}
