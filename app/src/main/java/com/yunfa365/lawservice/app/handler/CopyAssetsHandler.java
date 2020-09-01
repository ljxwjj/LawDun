package com.yunfa365.lawservice.app.handler;

import android.content.Context;

import com.android.agnetty.core.event.ExceptionEvent;
import com.android.agnetty.core.event.MessageEvent;
import com.android.agnetty.future.local.LocalHandler;
import com.android.agnetty.utils.LogUtil;
import com.yunfa365.lawservice.app.utils.FileUtil;

/**
 * Created by Administrator on 2016/6/3.
 */
public class CopyAssetsHandler extends LocalHandler {
    Context mContext;
    public CopyAssetsHandler(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void onHandle(MessageEvent evt) throws Exception {
        LogUtil.d("开始拷贝 widget 目录");
        boolean copyResult = FileUtil.copyAssetsDir(mContext, "widget", mContext.getFilesDir().getAbsolutePath());
        LogUtil.d("拷贝结果：" + copyResult);
    }

    @Override
    public void onException(ExceptionEvent evt) {

    }

    @Override
    public void onDispose() {

    }
}
