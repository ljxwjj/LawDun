package com.yunfa365.lawservice.app.ui.activity.base;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.ui.activity.LoginActivity_;
import com.yunfa365.lawservice.app.utils.AppUtil;

public class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private AlertDialog mReLoginDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
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
