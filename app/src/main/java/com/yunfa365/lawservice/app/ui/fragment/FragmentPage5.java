package com.yunfa365.lawservice.app.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.android.pushservice.PushManager;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.pojo.AppGlobal;
import com.yunfa365.lawservice.app.ui.activity.LoginActivity_;
import com.yunfa365.lawservice.app.ui.fragment.base.BaseFragment;
import com.yunfa365.lawservice.app.utils.SpUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
@EFragment(R.layout.fragment_page5)
class FragmentPage5 extends BaseFragment {

    @ViewById(R.id.base_id_back)
    View mBackView;

    @ViewById(R.id.base_id_title)
    TextView mTitleTxt;

    @ViewById(R.id.base_right_btn)
    ImageView mRightImage;

    @ViewById(R.id.base_right_txt)
    TextView mRightTxt;

    private Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        mActivity = null;
        _contentView_ = null;
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (_contentView_ == null) {
            _contentView_ = inflater.inflate(R.layout.fragment_page5, container, false);
        }
        ViewGroup parent = (ViewGroup) _contentView_.getParent();
        if (parent != null) {
            parent.removeView(_contentView_);
        }

        return _contentView_;
    }

    @AfterViews
    public void init() {
        if (_contentView_.getTag() != null) {
            return;
        }
        _contentView_.setTag(new Object());
        mActivity = getActivity();
        mTitleTxt.setText("个人中心");


        mRightTxt.setVisibility(View.VISIBLE);
        mRightTxt.setText("退出");
        mRightTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    public void logout() {
        AppGlobal.mUser = null;
        SpUtil.setCurrentUser(mActivity, "");
        PushManager.stopWork(mActivity); // 关闭推送
        Intent intent = new Intent(mActivity, LoginActivity_.class);
        startActivity(intent);
        mActivity.finish();
    }
}
