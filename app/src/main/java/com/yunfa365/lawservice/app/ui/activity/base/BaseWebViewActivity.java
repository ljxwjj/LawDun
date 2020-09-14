package com.yunfa365.lawservice.app.ui.activity.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;


import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.pojo.AppGlobal;
import com.yunfa365.lawservice.app.pojo.event.LogoutEvent;
import com.yunfa365.lawservice.app.ui.activity.LoginActivity_;
import com.yunfa365.lawservice.app.utils.AppUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * 为什么要添加此基类
 * 因为在vivo手机上，继承自AppCompatActivity的activity中inflating webview会崩溃
 * Created by Administrator on 2016/4/25.
 */
public class BaseWebViewActivity extends Activity {
    private ProgressDialog mProgressDialog;
    private AlertDialog mReLoginDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public boolean isLoading() {
        return mProgressDialog != null && mProgressDialog.isShowing();
    }

    public void showLoading() {
        if (mProgressDialog == null || !mProgressDialog.isShowing()) {
            mProgressDialog = AppUtil.showProgressDialog(this, R.string.wait_loading);
        }
    }

    public  void hideLoading() {
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
            mProgressDialog = null;
        }
    }

    public void showReLoginDialog() {
        if (mReLoginDialog != null) return;
        mReLoginDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.notice)
                .setMessage(R.string.relogin_message)
                .setCancelable(false)
                .setPositiveButton(R.string.relogin, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gotoLogin();
                    }
                })
                .show();
    }

    private void gotoLogin() {
        Intent intent = new Intent(this, LoginActivity_.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void showToast(String message) {
        AppUtil.showToast(this, message);
    }

    public void showToast(int message) {
        AppUtil.showToast(this, message);
    }
}
