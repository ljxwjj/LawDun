package com.yunfa365.lawservice.app.application;

import android.content.Context;
import android.os.Build;

import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyResult;
import com.android.agnetty.utils.LogUtil;
import com.yunfa365.lawservice.app.future.HttpJsonFuture;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.utils.AppCrashSPUtil;
import com.yunfa365.lawservice.app.utils.AppUtil;
import com.yunfa365.lawservice.app.utils.StringUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * @author : Zhenshui.Xia
 * @date   : 2015年1月7日
 * @desc   : 
 */
public class AppCrashHandler implements UncaughtExceptionHandler {
	private static AppCrashHandler INSTANCE = new AppCrashHandler();
	private Context context;
    
    private AppCrashHandler() {
    	
    }

    public static AppCrashHandler getInstance() {
        return INSTANCE;
    }

    public void init(Context context) {
    	this.context = context;
        Thread.setDefaultUncaughtExceptionHandler(this);
        sendCrashReportsToServer();
    }

    @Override
    public void uncaughtException(Thread thread, final Throwable ex) {
        new Thread() {
            @Override
            public void run() {
            	AppCrashSPUtil crash = AppCrashSPUtil.getInstance(context);
            	crash.setCrashPhoneModel(Build.MODEL);
            	crash.setCrashPhoneManufacturer(Build.MANUFACTURER);
            	StringWriter stackTrace=new StringWriter();
        		ex.printStackTrace(new PrintWriter(stackTrace));
        		String crashMsg = crash.getCrashLog() + stackTrace.toString();
        		crashMsg = crashMsg.length()>2000 ? crashMsg.substring(0, 1999) : crashMsg;
        		crash.setCrashLog(crashMsg);
        		LogUtil.e(stackTrace.toString());
            	System.exit(0);
            }
        }.start();
    }

    private void sendCrashReportsToServer() {
        AppCrashSPUtil crash = AppCrashSPUtil.getInstance(context);
        String crashMsg = crash.getCrashLog();
        if (StringUtil.isEmpty(crashMsg)) {
            return;
        }
        String processName = AppUtil.getProcessName(context);
        if(processName !=null
                && processName.equals(context.getPackageName())) {
            AppRequest request = new AppRequest.Build("System/AddAppLog")
                    .addParam("app_type", "1")
                    .addParam("model", crash.getCrashPhoneModel())
                    .addParam("manufacturer", crash.getCrashPhoneManufacturer())
                    .addParam("crash_log", crashMsg)
                    .addParam("android_version", android.os.Build.VERSION.RELEASE + " - " + AppUtil.getVersionName(context))
                    .create();
            new HttpJsonFuture.Builder(context)
                    .setData(request)
                    .setListener(new AgnettyFutureListener() {
                        @Override
                        public void onComplete(AgnettyResult result) {
                            AppCrashSPUtil crash = AppCrashSPUtil.getInstance(context);
                            crash.setCrashLog("");
                        }

                        @Override
                        public void onException(AgnettyResult result) {
                            AppCrashSPUtil crash = AppCrashSPUtil.getInstance(context);
                            crash.setCrashLog("");
                        }
                    })
                    .execute();
        }
    }
}
