package com.yunfa365.lawservice.app.future;

import android.content.Context;
import android.text.TextUtils;

import com.android.agnetty.constant.CharsetCst;
import com.android.agnetty.core.event.ExceptionEvent;
import com.android.agnetty.core.event.MessageEvent;
import com.android.agnetty.utils.LogUtil;
import com.yunfa365.lawservice.app.constant.AppCst;
import com.yunfa365.lawservice.app.pojo.event.LogoutEvent;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

public class HttpJsonDefaultHandler extends HttpJsonHandler {

    public HttpJsonDefaultHandler(Context context) {
        super(context);

    }

    @Override
    public boolean onStart(MessageEvent evt) throws Exception {
        return false;
    }

    @Override
    public boolean onDecompress(MessageEvent evt) throws Exception {
        return false;
    }

    @Override
    public boolean onDecode(MessageEvent evt) throws Exception {
        return false;
    }

    @Override
    public boolean onEncode(MessageEvent evt) throws Exception {
        AppRequest request = (AppRequest) evt.getData();
        request.signWithJson();

        HttpJsonFuture future = (HttpJsonFuture) evt.getFuture();
        future.setUrl(AppCst.getHttpUrl() + request.getUrlPath());
        LogUtil.d("CommonPostHandler url: " + future.getUrl() + " \nparam:" + request.paramToString());
        for (String key : request.getHeader().keySet()) {
            future.setProperty(key, request.getHeader().get(key));
        }

        evt.setData(request.paramToString().getBytes(CharsetCst.UTF_8));
        return false;
    }

    @Override
    public void onHandle(MessageEvent evt) throws Exception {
        String response = new String((byte[]) evt.getData(), "utf-8");
        LogUtil.d("URL:" + ((HttpJsonFuture)evt.getFuture()).getUrl());
        LogUtil.d("CommonPostHandler" + response);

        if (!TextUtils.isEmpty(response)) {
            response = response.replaceAll("\\\\\\\\", "\\\\");
            JSONObject json = new JSONObject(response);
            AppResponse res = new AppResponse();
            res.Message = json.getString("Message");
            res.Status = json.getInt("Status");
            res.Results = json.getString("Results");
            res.response = response;

            if (res.Status == -1) {
                EventBus.getDefault().post(new LogoutEvent());
            }
            evt.getFuture().commitComplete(res);
        }
    }

    @Override
    public void onException(ExceptionEvent evt) {
        evt.getFuture().commitException(null, evt.getException());
    }

    @Override
    public void onDispose() {

    }
}

