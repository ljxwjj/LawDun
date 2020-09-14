package com.yunfa365.lawservice.app.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.agnetty.utils.LogUtil;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.ui.activity.base.BaseActivity;
import com.yunfa365.lawservice.app.ui.activity.base.BaseWebViewActivity;
import com.yunfa365.lawservice.app.utils.ScreenUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_web)
class WebActivity extends BaseWebViewActivity {

    @ViewById(R.id.base_id_back)
    View mBackView;

    @ViewById(R.id.base_id_title)
    TextView mTitleTxt;

    @ViewById
    View progressView;

    @ViewById
    WebView webView;

    @Extra
    String url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    @AfterViews
    void init() {
        mBackView.setVisibility(View.VISIBLE);
        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });
        progressView.setVisibility(View.GONE);
        initWebView();
        webView.loadUrl(url);
    }

    private void initWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);// 设置允许访问文件数据
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webView.setWebViewClient(new CommonWebViewClient());
        webView.setWebChromeClient(new CommonWebChromeClient());
        webView.setDownloadListener(new CommonDownloadListener());
        webView.addJavascriptInterface(new ObjectJS(), "app");
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    private void goBack() {
        if (webView.canGoBack()) {
            webView.goBack();
            String title = webView.copyBackForwardList().getCurrentItem().getTitle();
            mTitleTxt.setText(title);
        } else {
            finish();
        }
    }

    public class ObjectJS {
        @JavascriptInterface
        public void close() {
            finish();
        }
    }

    /**
     *
     * @author Zhenshui.xia
     *
     */
    public	class CommonWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            progressView.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams params = progressView.getLayoutParams();
            params.width = ScreenUtil.screenWidth * newProgress/100;
            progressView.setLayoutParams(params);

            if (newProgress == 100) {
                progressView.animate().alpha(0).setDuration(400)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animator) {
                                progressView.setAlpha(1);
                                progressView.setVisibility(View.GONE);
                            }
                        });
            }
        }

        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            mTitleTxt.setText(title);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            result.confirm();
            return true;
        }
    }

    class CommonWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url.startsWith("elvshi:")){
                try{
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = Uri.parse(url);
                    intent.setData(uri);
                    startActivity(intent);
                }catch(Exception e){
                    e.printStackTrace();
                }
                return true;
            }
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            LogUtil.e("WebActivity errorCode: " + errorCode + "");
        }
    }

    class CommonDownloadListener implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent,
                                    String contentDisposition, String mimetype, long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

    }
}
