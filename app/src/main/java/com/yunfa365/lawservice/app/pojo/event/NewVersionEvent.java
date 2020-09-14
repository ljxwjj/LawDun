package com.yunfa365.lawservice.app.pojo.event;


import com.yunfa365.lawservice.app.application.AppApplication;
import com.yunfa365.lawservice.app.pojo.NewVersionInfo;

/**
 * Created by Administrator on 2016/5/24.
 */
public class NewVersionEvent {
    public int status; // 1 有版本更新
    public String message;
    public NewVersionInfo versionInfo;

//    public NewVersionEvent() {
//
//    }

    public NewVersionEvent(int s, String m) {
        status = s;
        message = m;
    }

    public NewVersionEvent(int s, int m) {
        status = s;
        message = AppApplication.getInstance().getString(m);
    }
}
