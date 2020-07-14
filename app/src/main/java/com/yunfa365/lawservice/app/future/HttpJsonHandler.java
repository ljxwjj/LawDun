package com.yunfa365.lawservice.app.future;

import android.content.Context;

import com.android.agnetty.core.AgnettyException;
import com.android.agnetty.core.AgnettyHandler;
import com.android.agnetty.core.AgnettyStatus;
import com.android.agnetty.core.event.MessageEvent;
import com.android.agnetty.utils.HttpUtil;
import com.android.agnetty.utils.NetworkUtil;
import com.android.agnetty.utils.StreamUtil;

import java.io.InputStream;
import java.util.HashMap;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class HttpJsonHandler extends AgnettyHandler {

    public HttpJsonHandler(Context context) {
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

        HttpJsonFuture future = (HttpJsonFuture) evt.getFuture();

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

        OkHttpClient client;
        InputStream in = null;

        if(onStart(evt)) {
            if(!future.isScheduleFuture()) future.cancel();
            return;
        }

        int retry = future.getRetry();
        while(retry>=0) {
            try {
                Headers.Builder builder = new Headers.Builder();
                HashMap<String, String> properties = future.getProperties();
                for(String key : properties.keySet()) {
                    builder.add(key, properties.get(key));
                }
                Headers headers = builder.build();
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), (byte[])evt.getData());

                client = HttpUtil.getHttpClient(mContext, future.getConnectionTimeout(), future.getReadTimeout());
                // Create request for remote resource.
                Request request = new Request.Builder()
                        .url(future.getUrl())
                        .headers(headers)
                        .post(requestBody)
                        .build();

                Response response = client.newCall(request).execute();

                int code = response.code();
                if (response.isSuccessful()) {
                    in = response.body().byteStream();
                    byte[] result = StreamUtil.toByteArray(in);
                    evt.setData(result);
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
                }  else { //请求失败，下载失败
                    throw new Exception("HTTP RESPONSE ERROR:"+code+"!!!");
                }

                retry = -1;
            } catch (Exception ex) {
                if(retry == 0) {
                    throw new AgnettyException(ex.getMessage(), AgnettyException.NETWORK_EXCEPTION);
                } else {
                    retry--;
                }
            } finally {
                //释放资源
                try {
                    if(in != null) in.close();
                } catch (Exception ex) {
                    throw ex;
                }
            }
        }
    }

}
