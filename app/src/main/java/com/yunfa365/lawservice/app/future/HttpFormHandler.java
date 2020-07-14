package com.yunfa365.lawservice.app.future;

import android.content.Context;

import com.android.agnetty.core.AgnettyException;
import com.android.agnetty.core.AgnettyHandler;
import com.android.agnetty.core.AgnettyStatus;
import com.android.agnetty.core.event.MessageEvent;
import com.android.agnetty.future.upload.form.FormUploadFile;
import com.android.agnetty.utils.FileUtil;
import com.android.agnetty.utils.HttpUtil;
import com.android.agnetty.utils.NetworkUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/4/11.
 */
public abstract class HttpFormHandler extends AgnettyHandler {

    public HttpFormHandler(Context context) {
        super(context);
    }

    /**
     * 编码
     * @param evt
     * @return
     */
    public abstract boolean onEncode(MessageEvent evt) throws Exception;

    /**
     * 开始（上传前）
     * @param evt
     * @return
     */
    public abstract boolean onStart(MessageEvent evt) throws Exception;

    /**
     * 解压缩
     * @param evt
     * @return
     * @throws Exception
     */
    public abstract boolean onDecompress(MessageEvent evt) throws Exception;

    /**
     * 解码
     * @param evt
     * @return
     */
    public abstract boolean onDecode(MessageEvent evt) throws Exception;

    /**
     * 业务处理
     * @param evt
     * @return
     */
    public abstract void onHandle(MessageEvent evt) throws Exception;

    @Override
    public void onExecute(MessageEvent evt) throws Exception {

        HttpFormFuture future = (HttpFormFuture) evt.getFuture();

        if(onEncode(evt)) {
            if(!future.isScheduleFuture()) future.cancel();
            return;
        }
        evt.setStatus(AgnettyStatus.START);
        future.commitStart(evt.getData());

        //网络没连上
        if(!NetworkUtil.isNetAvailable(mContext)) {
            AgnettyException ex = new AgnettyException("Network isn't avaiable", AgnettyException.NETWORK_UNAVAILABLE);
            throw ex;
        }
        if(onStart(evt)) {
            if(!future.isScheduleFuture()) future.cancel();
            return;
        }

        OkHttpClient client = HttpUtil.getHttpClient(mContext, future.getConnectionTimeout(), future.getReadTimeout());

        Headers.Builder builder = new Headers.Builder();
        HashMap<String, String> properties = future.getProperties();
        for(String key : properties.keySet()) {
            builder.add(key, properties.get(key));
        }
        Headers headers = builder.build();

        RequestBody requestBody = null;
        if (future.getUploadFiles() == null || future.getUploadFiles().length == 0) {
            FormBody.Builder formBuilder = new FormBody.Builder();
            // 上传的表单参数部分，格式请参考文章
            for (Map.Entry<String, String> entry : future.getUploadFields().entrySet()) {// 构建表单字段内容
                formBuilder.add(entry.getKey(), entry.getValue());
            }
            requestBody = formBuilder.build();
        } else {
            MultipartBody.Builder multipartRequestBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            for (Map.Entry<String, String> entry : future.getUploadFields().entrySet()) {// 构建表单字段内容
                multipartRequestBuilder.addFormDataPart(entry.getKey(), entry.getValue());
            }
            // 上传的文件部分，格式请参考文章
            for (FormUploadFile formFile : future.getUploadFiles()) {
                MediaType mediaType = MediaType.parse(formFile.getContentType());
                RequestBody body = null;
                if (formFile.getData() != null) {
                    body = RequestBody.create(mediaType, formFile.getData());
                } else if (FileUtil.isFileExist(formFile.getPath())) {
                    body = RequestBody.create(mediaType, new File(formFile.getPath()));
                }
                multipartRequestBuilder.addFormDataPart(formFile.getField(), formFile.getName(), body);
            }
            requestBody = multipartRequestBuilder.build();
        }

        Request.Builder requestBuilder = new Request.Builder()
                .url(future.getUrl())
                .headers(headers)
                .post(requestBody);
        Request request = requestBuilder.build();

        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new Exception("HTTP RESPONSE ERROR:"+response.code()+"!!!");

            evt.setData(response.body().bytes());
            if(onDecompress(evt)) {
                if(!future.isScheduleFuture()) future.cancel();
                return;
            }
            if(onDecode(evt)) {
                if(!future.isScheduleFuture()) future.cancel();
                return;
            }
            evt.setStatus(AgnettyStatus.COMPLETED);
            onHandle(evt);
        } catch (Exception ex) {
            throw new AgnettyException(ex.getMessage(), AgnettyException.NETWORK_EXCEPTION);
        }
    }
}
