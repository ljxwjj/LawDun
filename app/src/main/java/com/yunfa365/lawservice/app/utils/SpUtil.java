package com.yunfa365.lawservice.app.utils;

import android.content.Context;

import com.android.agnetty.utils.PrefUtil;

/**
 * Created by Administrator on 2016/4/12.
 */
public class SpUtil {
    private static final String CURRENT_USER_KEY                      = "current_user_info_key_3.0";
    private static final String REMEMBER_USERNAME_KEY                 = "remember_username_key";
    private static final String USERNAME_KEY                          = "username_key";
    private static final String NO_DISTURB_KEY                        = "no_disturb_key";
    private static final String SCHEDULE_SYNC_TIME_KEY                = "schedule_sync_time_key";
    private static final String USER_AGREEMENT_FLAG_KEY               = "user_agreement_flag_key";
    private static final String LAUNCHER_INFO_KEY                     = "launcher_info_key";
    private static final String HOME_BANNER_INFO_KEY                  = "home_banner_info_key";

    public static final void setCurrentUser(Context context, String userInfo) {
        PrefUtil.putString(context, CURRENT_USER_KEY, userInfo);
    }

    public static final String getCurrentUser(Context context) {
        return PrefUtil.getString(context, CURRENT_USER_KEY, null);
    }

    public static final void setRememberUsername(Context context, boolean remember) {
        PrefUtil.putBoolean(context, REMEMBER_USERNAME_KEY, remember);
    }

    public static final boolean getRememberUsername(Context context) {
        return PrefUtil.getBoolean(context, REMEMBER_USERNAME_KEY, true);
    }

    public static final void setUsername(Context context, String username) {
        PrefUtil.putString(context, USERNAME_KEY, username);
    }

    public static final String getUsername(Context context) {
        return PrefUtil.getString(context, USERNAME_KEY, "");
    }

    public static final void setNoDisturb(Context context, String value) {
        PrefUtil.putString(context, NO_DISTURB_KEY, value);
    }

    public static final String getNoDisturb(Context context) {
        return PrefUtil.getString(context, NO_DISTURB_KEY, "");
    }

    public static final void setScheduleSyncTime(Context mContext, long value) {
        PrefUtil.putLong(mContext, SCHEDULE_SYNC_TIME_KEY, value);
    }

    public static final long getScheduleSyncTime(Context mContext) {
        return PrefUtil.getLong(mContext, SCHEDULE_SYNC_TIME_KEY, 0);
    }

    public static boolean getUserAgreementFlag(Context context) {
        return PrefUtil.getBoolean(context, USER_AGREEMENT_FLAG_KEY, false);
    }

    public static void setUserAgreementFlag(Context context) {
        PrefUtil.putBoolean(context, USER_AGREEMENT_FLAG_KEY, true);
    }

    public static void setLauncherInfo(Context context, String value) {
        PrefUtil.putString(context, LAUNCHER_INFO_KEY, value);
    }

    public static String getLauncherInfo(Context context) {
        return PrefUtil.getString(context, LAUNCHER_INFO_KEY, "");
    }

    public static void setHomeBannerInfo(Context context, String value) {
        PrefUtil.putString(context, HOME_BANNER_INFO_KEY, value);
    }

    public static String getHomeBannerInfo(Context context) {
        return PrefUtil.getString(context, HOME_BANNER_INFO_KEY, "");
    }
}
