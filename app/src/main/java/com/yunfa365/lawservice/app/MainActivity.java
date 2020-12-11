package com.yunfa365.lawservice.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

import com.yunfa365.lawservice.app.pojo.AppGlobal;
import com.yunfa365.lawservice.app.ui.activity.HomeActivity_;
import com.yunfa365.lawservice.app.ui.activity.LoginActivity_;
import com.yunfa365.lawservice.app.ui.activity.WebActivity_;
import com.yunfa365.lawservice.app.utils.ScreenUtil;
import com.yunfa365.lawservice.app.utils.SpUtil;

public class MainActivity extends AppCompatActivity {

    private static final int GO_HOME_CODE = 1;
    private static final int GO_HOME_REQUEST_CODE = 2;

    private MyHandler mHandler;

    private TextView text1;
    private TextView text2;
    private int mWiteTime = 2;

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

        boolean agreementFlag = SpUtil.getUserAgreementFlag(this);
        if (agreementFlag) {
            goHome();
        } else {
            showUserAgreementDialog();
        }
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
                        HomeActivity_.intent(MainActivity.this).mDefaultTab(0).start();
                    } else {
                        LoginActivity_.intent(MainActivity.this).start();
                    }
                    finish();
                    return;
                }
            }
        }
    }

    private void showUserAgreementDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(R.layout.dialog_user_agreement).setCancelable(false)
                .setPositiveButton("我同意", (dialog12, which) -> {
                    dialog12.dismiss();
                    SpUtil.setUserAgreementFlag(MainActivity.this);
                    goHome();
                })
                .setNegativeButton("暂不使用", (dialog1, which) -> finish())
                .create();
        dialog.show();
        TextView textView = dialog.findViewById(R.id.text);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        CharSequence text = textView.getText();
        Spannable sp = (Spannable) textView.getText();
        URLSpan[] urls = sp.getSpans(0, text.length(), URLSpan.class);
        SpannableStringBuilder style = new SpannableStringBuilder(text);
        style.clearSpans();
        for (final URLSpan url : urls) {
            ClickableSpan myURLSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    WebActivity_.intent(MainActivity.this).url(url.getURL()).start();
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    if (ds != null) {
                        ds.setUnderlineText(false);
                    }
                }
            };
            style.setSpan(myURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setText(style);
    }
}