package com.yunfa365.lawservice.app.pojo;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/4/20.
 * 案件分类
 */
public class CaseCols implements Serializable {
    public String ID;
    public String Title;
    public String FTitle;
    public String TempID;

    public String Fid; // 父级ID
    public CaseCols[] Children;


    @Override
    public String toString() {
        return Title;
    }
}
