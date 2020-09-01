package com.yunfa365.lawservice.app.application;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.text.TextUtils;

import androidx.multidex.MultiDex;

import com.android.agnetty.core.AgnettyFuture;
import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyManager;
import com.android.agnetty.core.AgnettyResult;
import com.android.agnetty.utils.LogUtil;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.mapapi.SDKInitializer;
import com.mobsandgeeks.saripaar.Validator;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.constant.AppCst;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.future.HttpJsonFuture;
import com.yunfa365.lawservice.app.pojo.AppGlobal;
import com.yunfa365.lawservice.app.pojo.CaseCols;
import com.yunfa365.lawservice.app.pojo.User;
import com.yunfa365.lawservice.app.pojo.base.BaseBean;
import com.yunfa365.lawservice.app.pojo.event.BaiDuPushBindEvent;
import com.yunfa365.lawservice.app.pojo.event.LoginEvent;
import com.yunfa365.lawservice.app.pojo.event.LogoutEvent;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.notification.PushNotificationBuilder;
import com.yunfa365.lawservice.app.ui.validation.OptionalEmail;
import com.yunfa365.lawservice.app.ui.validation.OptionalIdCard;
import com.yunfa365.lawservice.app.ui.validation.OptionalPhone;
import com.yunfa365.lawservice.app.ui.validation.Phone;
import com.yunfa365.lawservice.app.utils.AppUtil;
import com.yunfa365.lawservice.app.utils.ImageLoaders;
import com.yunfa365.lawservice.app.utils.ScreenUtil;
import com.yunfa365.lawservice.app.utils.SpUtil;
import com.yunfa365.lawservice.app.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by wangjianjie on 16/4/6.
 */

public class AppApplication extends Application {

    private static AppApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        String processName = AppUtil.getProcessName(this);
        if(processName !=null
                && processName.equals(getPackageName())) {

            SDKInitializer.initialize(this);
            EventBus.getDefault().register(this);
            ScreenUtil.GetInfo(this);
            ImageLoaders.getInstance().init(this);
            autoLogin();

            Validator.registerAnnotation(OptionalEmail.class);
            Validator.registerAnnotation(OptionalPhone.class);
            Validator.registerAnnotation(Phone.class);
            Validator.registerAnnotation(OptionalIdCard.class);

            initBaiduPushManager();
        }

        if (!AppCst.DEBUG) {
            AppCrashHandler.getInstance().init(getApplicationContext());
        }
    }

    private void initBaiduPushManager() {
//        PushSettings.enableDebugMode(getApplicationContext(), true);
        PushManager.startWork(getApplicationContext(),
                PushConstants.LOGIN_TYPE_API_KEY,
                AppUtil.getMetaValue(this, "api_key"));

        PushNotificationBuilder builder = new PushNotificationBuilder();
        builder.setStatusbarIcon(R.mipmap.notification_icon);
        builder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
        builder.setNotificationDefaults(Notification.DEFAULT_ALL);
        PushManager.setDefaultNotificationBuilder(getApplicationContext(), builder);
    }

    private void autoLogin() {
//        if (testLogin()) return;
        String userInfo = SpUtil.getCurrentUser(this);
        if (TextUtils.isEmpty(userInfo)) {
            return;
        }
        LogUtil.d(userInfo);
        try {
            User[] users = StringUtil.toObjectArray(userInfo, User.class);
            AppGlobal.mUser = users[0];
            EventBus.getDefault().post(new LoginEvent());
        } catch (Exception e) {
            SpUtil.setCurrentUser(this, "");
        }
    }

    /**
     * 模拟用户登录
     * {    "Wid":1005,
     *      "LawyerName":"陈丽珍律师事务所",
     *      "WUid":5990,"ID":1,
     *      "Mobile":"18888888888",
     *      "FullName":"超级管理员",
     *      "Attestation":0,
     *      "SignKey":"0A19E5E97DEB0A17B0C97BFA95B3C49E"
     * }
     * @return login success
     */
    private boolean testLogin() {
        User user = new User();
        user.ID = 0;
        user.LawId = 0;  //对应数据库的id字段
        user.Mobile = "";
        AppGlobal.mUser = user;
        AppUtil.showToast(this, "测试模拟登录用户");
        EventBus.getDefault().post(new LoginEvent());
        return true;
    }

    @Subscribe
    public void onEvent(LoginEvent event) {
        if (AppGlobal.mUser == null) {
            return;
        }
        if (!PushManager.isPushEnabled(getApplicationContext())) {
            initBaiduPushManager();
        }
        if (AppGlobal.mUser.mRole == null) {
            AppGlobal.mUser.loadUserRole(this);
        }
        loadSffsData();

        if (AppGlobal.mBaiDuChannelId != null) {
            PushManager.listTags(getApplicationContext());
            LogUtil.d("listTags onEvent loginin");
            sendBaiduBindInfo();
        }
    }

    @Subscribe
    public void onEvent(LogoutEvent event) {
        if (AppGlobal.mUser != null) {
            AppGlobal.mUser = null;
            SpUtil.setCurrentUser(this, "");
            PushManager.stopWork(this); // 关闭推送
        }
    }

    @Subscribe
    public void onEvent(BaiDuPushBindEvent event) {
        if (AppGlobal.mUser == null) {
            return;
        }
        if (AppGlobal.mBaiDuChannelId != null) {
            PushManager.listTags(getApplicationContext());
            LogUtil.d("listTags onEvent BaiDuPushBindEvent");
            sendBaiduBindInfo();
        }
    }

    private void sendBaiduBindInfo() {
        AppRequest request = new AppRequest.Build("Home/GetPushInfo")
                .addParam("ChannelID", AppGlobal.mBaiDuChannelId)
                .create();

        new HttpJsonFuture.Builder(this)
                .setData(request)
                .execute();
    }

    private void loadSffsData() {
        String TAG = "GetSFFFList";
        AgnettyManager manager = AgnettyManager.getInstance(this);
        AgnettyFuture future = manager.getFutureByTag(TAG);
        if (future != null) return;

        AppRequest request = new AppRequest.Build("api/Case/PayCols_Get")
                .create();
        new HttpFormFuture.Builder(this)
                .setTag(TAG)
                .setData(request)
                .setListener(new AgnettyFutureListener(){
                    @Override
                    public void onComplete(AgnettyResult result) {
                        AppResponse resp = (AppResponse) result.getAttach();
                        if (resp.flag) {
                            BaseBean[] sffs = resp.resultsToArray(BaseBean.class);
                            AppCst.sffss = sffs;
                        }
                    }
                })
                .execute();
    }


    /*@Subscribe
    public void onEvent(RequestCheckVersion event) {
        String version = AppUtil.getVersionName(this);
        AppRequest request = new AppRequest.Build("Home/CheckLastestAppVersion")
                .addParam("Number", version)  // 版本号
                .addParam("Type", "1")        // 1:律所版  2：用户版
                .addParam("AppCode", "1")     //1： 安卓  2： IOS
                .create();
        new HttpJsonFuture.Builder(this)
                .setData(request)
                .setListener(new AgnettyFutureListener(){
                    @Override
                    public void onStart(AgnettyResult result) {

                    }

                    @Override
                    public void onComplete(AgnettyResult result) {
                        AppResponse resp = (AppResponse)result.getAttach();
                        // 状态  0：没有更新   1：普通更新  2：微小更新  3：强制更新
                        if (resp.Status == AppCst.HTTP_CODE_SUCCESS) {
                            EventBus.getDefault().post(new NewVersionEvent(0, resp.Message));
                        } else if (resp.Status == 3) {
                            final NewVersionInfo versionInfo = resp.resultsToObject(NewVersionInfo.class);
                            NewVersionEvent event = new NewVersionEvent(1, resp.Message);
                            event.versionInfo = versionInfo;
                            EventBus.getDefault().post(event);
                        }
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        NewVersionEvent event = new NewVersionEvent(0, R.string.network_exception_message);
                        EventBus.getDefault().post(event);
                    }
                })
                .execute();
    }

    public void downloadApk(final Context context, NewVersionInfo versionInfo) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("软件版本更新");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        String fileName = AppUtil.urlToName(versionInfo.Url);
        final String filePath = StorageUtil.getFilePath(context, fileName);
        final DownloadFuture future = new DownloadFuture.Builder(context)
                .setUrl(versionInfo.Url)
                .setDownloadMode(DownloadFuture.DIRECT_MODE)
                .setPath(filePath)
                .setListener(new AgnettyFutureListener(){
                    @Override
                    public void onProgress(AgnettyResult result) {
                        int progress = result.getProgress();
                        progressDialog.setProgress(progress);
                    }

                    @Override
                    public void onComplete(AgnettyResult result) {
                        super.onComplete(result);
                        AppUtil.installApk(context, filePath);
                        progressDialog.cancel();
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        super.onException(result);
                        progressDialog.cancel();
                    }
                }).create();
        future.execute();

        if (versionInfo.Levels > 0) {
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getText(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    future.cancel();
                    dialog.cancel();
                }
            });
        }
        progressDialog.show();
    }*/

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static AppApplication getInstance() {
        return instance;
    }
}
