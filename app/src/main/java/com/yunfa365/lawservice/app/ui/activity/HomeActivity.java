package com.yunfa365.lawservice.app.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_home)
class HomeActivity extends BaseUserActivity {

    @ViewById(R.id.base_id_back)
    View mBackView;

    @ViewById(R.id.base_id_title)
    TextView mTitleTxt;

    @ViewById(R.id.base_right_btn)
    ImageView mRightImage;

    @ViewById(R.id.base_right_txt)
    TextView mRightTxt;

    @Extra
    int mDefaultTab;

    @AfterViews
    void init() {
        mTitleTxt.setText("首页");
        mRightTxt.setVisibility(View.VISIBLE);
        mRightTxt.setText("退出");
        mRightTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }
}
