package com.yunfa365.lawservice.app.ui.notification;

import android.app.Notification;
import android.content.Context;

import com.baidu.android.pushservice.BasicPushNotificationBuilder;

/**
 * Created by Administrator on 2016/6/14.
 */
public class PushNotificationBuilder extends BasicPushNotificationBuilder {

    @Override
    public Notification construct(Context context) {
        Notification notification = super.construct(context);
        notification.tickerText = mNotificationTitle + ":" + mNotificationText;
        return notification;
    }
}
