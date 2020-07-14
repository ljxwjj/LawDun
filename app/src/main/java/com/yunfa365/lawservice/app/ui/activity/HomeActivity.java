package com.yunfa365.lawservice.app.ui.activity;

import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

@EActivity(R.layout.activity_home)
class HomeActivity extends BaseUserActivity {

    @Extra
    int mDefaultTab;
}
