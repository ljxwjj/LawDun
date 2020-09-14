package com.yunfa365.lawservice.app.ui.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTabHost;

import com.android.agnetty.utils.LogUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.application.AppApplication;
import com.yunfa365.lawservice.app.pojo.AppGlobal;
import com.yunfa365.lawservice.app.pojo.NewVersionInfo;
import com.yunfa365.lawservice.app.pojo.event.LoginEvent;
import com.yunfa365.lawservice.app.pojo.event.LogoutEvent;
import com.yunfa365.lawservice.app.pojo.event.NewVersionEvent;
import com.yunfa365.lawservice.app.pojo.event.RequestCheckVersion;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;
import com.yunfa365.lawservice.app.ui.fragment.FragmentPage1_;
import com.yunfa365.lawservice.app.ui.fragment.FragmentPage2_;
import com.yunfa365.lawservice.app.ui.fragment.FragmentPage3_;
import com.yunfa365.lawservice.app.ui.fragment.FragmentPage4_;
import com.yunfa365.lawservice.app.ui.fragment.FragmentPage5_;
import com.yunfa365.lawservice.app.utils.LocationUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@EActivity(R.layout.activity_home)
public class HomeActivity extends BaseUserActivity {
    private boolean mExit;

    @Extra
    int mDefaultTab = -1;

    @ViewById
    View rootLayout;

    @ViewById(android.R.id.tabhost)
    FragmentTabHost fragmentTabHost;


    @StringRes(R.string.icon_fonts)
    String iconFonts;
    private String[] iconFontArray;

    private String texts[] = { "消息", "办公", "应用", "我" };
    private Class fragmentArray[];
    private TextView fontIcons[] = new TextView[5];

    private View getView(int i) {
        // 取得布局实例
        View view = View.inflate(this, R.layout.tabcontent, null);

        // 取得布局对象
        TextView imageView = (TextView) view.findViewById(R.id.image);
        TextView textView = (TextView) view.findViewById(R.id.text);
        fontIcons[i] = imageView;

        // 设置图标
        //imageView.setImageResource(imageButton[i]);
        imageView.setText(iconFontArray[i]);
        // 设置标题
        textView.setText(texts[i]);
        return view;
    }

    @AfterViews
    void init(){
        LogUtil.e("-------------------------------------------------------------");
//        LogUtil.d(getApplicationContext().getFilesDir().getAbsolutePath());             /data/data/com.lvlian.elvshi/files
//        LogUtil.d(getApplicationContext().getPackageResourcePath());                    /data/app/com.lvlian.elvshi-2/base.apk
//        LogUtil.d(getApplicationContext().getDatabasePath("db").getAbsolutePath());     /data/data/com.lvlian.elvshi/databases/db

        if (AppGlobal.mUser == null) {
            gotoLogin();
            return;
        }
        fragmentArray = new Class[]{ FragmentPage1_.class, FragmentPage3_.class,
                FragmentPage4_.class, FragmentPage5_.class };
        iconFontArray = iconFonts.split(",");
        fragmentTabHost.setup(this, getSupportFragmentManager(), R.id.maincontent);
        for (int i = 0; i < texts.length; i++) {
            TabHost.TabSpec spec = fragmentTabHost.newTabSpec(texts[i]).setIndicator(getView(i));
            fragmentTabHost.addTab(spec, fragmentArray[i], null);

            // 设置背景(必须在addTab之后，由于需要子节点（底部菜单按钮）否则会出现空指针异常)
            //fragmentTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.bt_selector);
        }
        fragmentTabHost.getTabWidget().setDividerDrawable(null);

        if (mDefaultTab >=0 || mDefaultTab <=3) {
            fragmentTabHost.setCurrentTab(mDefaultTab);
        }
        // 请求检查更新
        EventBus.getDefault().post(new RequestCheckVersion());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 使状态栏图标变黑
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onBackPressed() {
        if (mExit) {
            finish();
        } else {
            mExit = true;
            showToast(R.string.main_exit);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mExit = false;
                }
            }, 2000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppGlobal.mUser != null && AppGlobal.mUser.mRole == null) {
            EventBus.getDefault().post(new LoginEvent());
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        LogUtil.d("HomeActivity onDestroy!!!");
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1)
    public void onEvent(NewVersionEvent event) {
        if (event.status == 1) {
            showNewVersionDialog(event.versionInfo);
        }
    }

    private void showNewVersionDialog(final NewVersionInfo versionInfo) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("发现新版本" + versionInfo.Number);
        builder.setMessage(versionInfo.Mark);
        builder.setPositiveButton("下载更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadNewVersionApk(versionInfo);
            }
        });
        if (versionInfo.Levels > 0) {
            builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }
        builder.setCancelable(false);
        android.app.AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    void downloadNewVersionApk(final NewVersionInfo versionInfo) {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        ((AppApplication)getApplication()).downloadApk(HomeActivity.this, versionInfo);
                    } else {
                        showToast("获取权限失败");
                    }
                }, Throwable::printStackTrace);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LogoutEvent event) {
        showReLoginDialog();
    }

    private void gotoLogin() {
        Intent intent = new Intent(this, LoginActivity_.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void gotoConstantTab() {
        fragmentTabHost.setCurrentTab(1);
    }


}
