package com.yunfa365.lawservice.app.pojo;

import com.yunfa365.lawservice.app.pojo.base.CommonItem;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/4/21.
 */
public class Custom implements Serializable, CommonItem {

    public int ID;
    public int Uid;
    public String Title;
    public String CaseUName;
    public String Phone;
    public String Mobile;
    public int ProvinceId;
    public String ProvinceIdTxt;
    public int CityId;
    public String CityIdTxt;
    public int AreaId;
    public String AreaIdTxt;
    public int CustCols;              // 客户类型 公民个人、公司企业……
    public String CustColsTxt;
    public String Addtime;
    public String UNums;              // 身份证号/营业执照号
    public String Model = "";
    public String Model1;
    public String Model2;
    public String Model3;
    public String FilePath;

    public String UsersFullName;
    public String YwRen;              // 业务联系人
    public String YwRenZhiWu;         // 职务
    public String FzRen;              //   主要负责人
    public String YingXiangLi;        //  地区影响力
    public String Email;              //     邮件
    public String Address;            //   详细地址
    public String Make;               // 备注

    @Override
    public String getTitle() {
        return Title;
    }

    @Override
    public String getDesc() {
        String desc = "当事人：%s\n手机号码：%s\n所属区域：%s-%s-%s\n入库时间：%s";
        return String.format(desc, CaseUName, Mobile, ProvinceIdTxt, CityIdTxt, AreaIdTxt, Addtime);
    }

    @Override
    public String getStatus() {
        return "";
    }
}
