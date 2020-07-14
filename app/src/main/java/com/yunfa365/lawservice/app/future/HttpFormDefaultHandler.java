package com.yunfa365.lawservice.app.future;

import android.content.Context;
import android.text.TextUtils;

import com.android.agnetty.core.event.ExceptionEvent;
import com.android.agnetty.core.event.MessageEvent;
import com.android.agnetty.future.upload.form.FormUploadFile;
import com.android.agnetty.utils.LogUtil;
import com.yunfa365.lawservice.app.constant.AppCst;
import com.yunfa365.lawservice.app.pojo.event.LogoutEvent;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/4/11.
 */
public class HttpFormDefaultHandler extends HttpFormHandler {

    public HttpFormDefaultHandler(Context context) {
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
        request.signWithForm();

        HttpFormFuture future = (HttpFormFuture) evt.getFuture();
        future.setUrl(AppCst.getHttpUrl() + request.getUrlPath());
        LogUtil.d("CommonPostHandler url: " + future.getUrl());

        for (String key : request.getHeader().keySet()) {
            future.setProperty(key, request.getHeader().get(key));
        }
        future.setUploadFields(request.getParam());
        FormUploadFile[] files = request.getFiles();
        if (files != null) future.setUploadFiles(files);
        return false;
    }

    @Override
    public void onHandle(MessageEvent evt) throws Exception {
        String response = new String((byte[]) evt.getData(), "utf-8");
        LogUtil.d("URL:" + ((HttpFormFuture)evt.getFuture()).getUrl());
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
