package com.yunfa365.lawservice.app.pojo;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/4/20.
 * 案件分类
 */
public class CaseCols implements Serializable {
    public int ID;
    public int Fid; // 父级ID
    public String Title;
    public String STitle;
    public int Sort;
    public int IsChongTu;

    /**
     * MS：诉讼类模板
     * FS：非诉类模板
     * GW：顾问类模板
     * DS：咨询代写文书模板
     */
    public String TempCols;


    @Override
    public String toString() {
        return Title;
    }
}
