package com.yunfa365.lawservice.app.pojo;

import com.google.gson.annotations.JsonAdapter;
import com.yunfa365.lawservice.app.gson.FormatStringJsonAdapter;

/**
 * Created by Administrator on 2016/5/25.
 * {"flag":true,"Code":"0","Message":"操作成功","RData":[{"ID":2,"AppCode":1,"Number":"1.0.2","Url":"","Type":1,"Levels":1,"AddTime":"2020-09-14T00:00:00","Mark":"1、升级了公章使用\r\n2、升级了盖章文件上传","Size":"12"}]}
 *
 *
 * 返回这个数据是需要升级
 * Number:升级后的版本号
 * Url：下载地址
 * Levels：0：微小更新 1：普通更新  2：必须更新
 *
 *
 *
 * {"flag":false,"Code":"1002","Message":"暂无版本更新"}
 *
 * 返回这个数据是不需要更新的
 *
 */
public class NewVersionInfo {
    public int ID;
    public String Number;
    public String Url;
    public int Type;
    public int Levels;  // 更新等级    0：必须更新 1：普通更新 2：微小更新
    public String AddTime;
    @JsonAdapter(FormatStringJsonAdapter.class)
    public String Mark;
    public String Size;
}
