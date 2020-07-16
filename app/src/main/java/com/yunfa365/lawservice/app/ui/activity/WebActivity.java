package com.yunfa365.lawservice.app.ui.activity;

import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.ui.activity.base.BaseActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_web)
class WebActivity extends BaseActivity {
    @ViewById
    WebView webview;

    @Extra
    String url;

    @AfterViews
    void init() {
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });
        webview.loadUrl("https://www.baidu.com");
    }
}
