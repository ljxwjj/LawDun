package com.yunfa365.lawservice.app.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyResult;
import com.baidu.android.pushservice.PushManager;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.constant.AppCst;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.AppGlobal;
import com.yunfa365.lawservice.app.pojo.User;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.LoginActivity_;
import com.yunfa365.lawservice.app.ui.activity.WebActivity_;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;
import com.yunfa365.lawservice.app.ui.activity.personal.ModifyPasswordActivity_;
import com.yunfa365.lawservice.app.ui.activity.personal.ProfileActivity_;
import com.yunfa365.lawservice.app.ui.fragment.base.BaseFragment;
import com.yunfa365.lawservice.app.utils.AppUtil;
import com.yunfa365.lawservice.app.utils.SpUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.Date;

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

    @ViewById
    TextView userName;

    @ViewById
    TextView caseValue;

    @ViewById
    TextView audintValue;

    @ViewById
    TextView billValue;

    @ViewById
    TextView user_agreement;

    @ViewById
    TextView copyright;

    private BaseUserActivity mActivity;

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
        mActivity = (BaseUserActivity) getActivity();
        mTitleTxt.setText("个人中心");


        mRightTxt.setVisibility(View.VISIBLE);
        mRightTxt.setText("退出");
        mRightTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int year = calendar.get(Calendar.YEAR);

        copyright.setText(getString(R.string.app_copyright, year));

        user_agreement.setMovementMethod(LinkMovementMethod.getInstance());
        CharSequence text = user_agreement.getText();
        Spannable sp = (Spannable) user_agreement.getText();
        URLSpan[] urls = sp.getSpans(0, text.length(), URLSpan.class);
        SpannableStringBuilder style = new SpannableStringBuilder(text);
        style.clearSpans();
        for (final URLSpan url : urls) {
            ClickableSpan myURLSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    WebActivity_.intent(FragmentPage5.this).url(url.getURL()).start();
                }
            };
            style.setSpan(myURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        user_agreement.setText(style);

        loadData();
    }

    @Click(R.id.jbzl)
    void jbzlOnClick() {
        ProfileActivity_.intent(this).start();
    }

    @Click(R.id.xgmm)
    void xgmmOnClick() {
        ModifyPasswordActivity_.intent(this).start();
    }

    @Click(R.id.lxwm)
    void lxwmOnClick() {
        WebActivity_.intent(this)
                .url(AppCst.getHttpUrl() + "Web/about/contact.html")
                .start();
    }

    @Click(R.id.gywm)
    void gywmOnClick() {
        WebActivity_.intent(this)
                .url(AppCst.getHttpUrl() + "Web/about/about.html")
                .start();
    }

    public void logout() {
        AppGlobal.mUser = null;
        SpUtil.setCurrentUser(mActivity, "");
        PushManager.stopWork(mActivity); // 关闭推送
        Intent intent = new Intent(mActivity, LoginActivity_.class);
        startActivity(intent);
        mActivity.finish();
    }

    private void loadData() {
        AppRequest.Build build = new AppRequest.Build("api/Users/Users_MyInfo_Get")
                .addParam("GetCols", "1");
        AppRequest request = build.create();
        new HttpFormFuture.Builder(mActivity)
                .setData(request)
                .setListener(new AgnettyFutureListener(){
                    @Override
                    public void onStart(AgnettyResult result) {
                        mActivity.showLoading();
                    }

                    @Override
                    public void onComplete(AgnettyResult result) {
                        mActivity.hideLoading();
                        AppResponse resp = (AppResponse)result.getAttach();
                        if (resp.flag) {
                            User myInfo = resp.getFirstObject(User.class);
                            if (myInfo.FullName.length() > 0) {
                                userName.setText(myInfo.FullName.substring(0, 1));
                            }
                            caseValue.setText(myInfo.TongJi_Case);
                            audintValue.setText(myInfo.TongJi_Off);
                            billValue.setText(myInfo.TongJi_Bill);
                        } else if (!TextUtils.isEmpty(resp.Message)) {
                            AppUtil.showToast(mActivity, resp.Message);
                        }
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        mActivity.hideLoading();
                        mActivity.showToast("网络异常");
                    }
                })
                .execute();
    }
}
