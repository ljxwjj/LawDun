package com.yunfa365.lawservice.app.constant;

import com.android.agnetty.constant.AgnettyCst;

/**
 * Created by wangjianjie on 16/4/6.
 */
public class AppCst extends BaseCst {

    public static final int PERSONAL_EDITION_WID = 999;  // 个人版标记

    // 是否是调试模式
    public static final boolean DEBUG = true;
    //API接口地址
    private final static String HTTP_ROOT = "https://api.lawdun.com/";
    private final static String HTTP_ROOT_DEBUG = "http://api.lawdun.com/";
    static {
		AgnettyCst.DEBUG = DEBUG;
    }

    public static final String getHttpUrl() {
        return DEBUG?HTTP_ROOT_DEBUG:HTTP_ROOT;
    }


    public static final String WEIXIN_APP_ID = "wxa1b2e2836d3fb682";
    public static final String BH_SDK_APP_KEY = "200611845681";
    public static final String BH_SDK_SECRET = "fbeac04a00d73e1ab3a3548b4d141b1a";
}
