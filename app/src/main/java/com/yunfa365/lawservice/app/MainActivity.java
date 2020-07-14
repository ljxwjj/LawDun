package com.yunfa365.lawservice.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.yunfa365.lawservice.app.pojo.AppGlobal;
import com.yunfa365.lawservice.app.ui.activity.HomeActivity_;
import com.yunfa365.lawservice.app.ui.activity.LoginActivity_;
import com.yunfa365.lawservice.app.utils.ScreenUtil;

public class MainActivity extends AppCompatActivity {

    private static final int GO_HOME_CODE = 1;
    private static final int GO_HOME_REQUEST_CODE = 2;

    private MyHandler mHandler;

    private TextView text1;
    private TextView text2;
    private int mWiteTime = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenUtil.setNavigationBar(this, View.GONE);

        if (!isTaskRoot()) {
            finish();
            return;
        }
        mHandler = new MyHandler();

        setContentView(R.layout.activity_main);
        goHome();
    }

    private void goHome() {
        if (text1 != null) {
            text1.setText(String.format("%dS", mWiteTime));
        }
        mHandler.sendEmptyMessageDelayed(GO_HOME_CODE, 1000);
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == GO_HOME_CODE && !isFinishing()) {
                mWiteTime--;
                if (text1 != null && mWiteTime >= 0) {
                    text1.setText(String.format("%dS", mWiteTime));
                }
                if (mWiteTime > 0) {
                    this.sendEmptyMessageDelayed(GO_HOME_CODE, 1000);
                } else {
                    if (AppGlobal.mUser != null) {
                        HomeActivity_.intent(MainActivity.this).mDefaultTab(2).start();
                    } else {
                        LoginActivity_.intent(MainActivity.this).start();
                    }
                    finish();
                    return;
                }
            }
        }
    }
}