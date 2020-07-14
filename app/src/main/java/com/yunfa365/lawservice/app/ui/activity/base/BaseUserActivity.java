package com.yunfa365.lawservice.app.ui.activity.base;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.pojo.AppGlobal;
import com.yunfa365.lawservice.app.pojo.event.LogoutEvent;
import com.yunfa365.lawservice.app.utils.AppUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class BaseUserActivity extends BaseActivity {

    private LoginStatusObserver mObserver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mObserver = new LoginStatusObserver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppGlobal.mUser == null) {
            showReLoginDialog();
            return;
        }
        EventBus.getDefault().register(mObserver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(mObserver);
    }

    class LoginStatusObserver {
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEvent(LogoutEvent event) {
            showReLoginDialog();
        }
    }
}
