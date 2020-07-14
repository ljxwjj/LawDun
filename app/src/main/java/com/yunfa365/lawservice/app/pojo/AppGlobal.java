package com.yunfa365.lawservice.app.pojo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/11.
 */
public class AppGlobal {
    public static User mUser;
    public static String mBaiDuUserId;
    public static String mBaiDuChannelId;

    private static Map<String, Object> SSDW_CACHE = new HashMap<>();
    private static Map<String, Object> SSJD_CACHE = new HashMap<>();

    public static Object getSSDW(String cols) {
        return SSDW_CACHE.get(cols);
    }

    public static void putSSDW(String cols, Object obj) {
        SSDW_CACHE.put(cols, obj);
    }

    public static Object getSSJD(String cols) {
        return SSJD_CACHE.get(cols);
    }

    public static void putSSJD(String cols, Object obj) {
        SSJD_CACHE.put(cols, obj);
    }
}
