package com.yunfa365.lawservice.app.ui.activity.common;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.agnetty.constant.CharsetCst;
import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyResult;
import com.android.agnetty.future.local.LocalFuture;
import com.android.agnetty.utils.LogUtil;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.handler.CopyAssetsHandler;
import com.yunfa365.lawservice.app.pojo.GridItem;
import com.yunfa365.lawservice.app.ui.activity.base.BaseWebViewActivity;
import com.yunfa365.lawservice.app.utils.AppUtil;
import com.yunfa365.lawservice.app.utils.FileUtil;
import com.yunfa365.lawservice.app.utils.StringUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.FileNotFoundException;

/**
 * Created by Administrator on 2016/5/13.
 */
@EActivity(R.layout.activity_tool_webview)
public class ToolWebViewActivity extends BaseWebViewActivity {
    private ProgressDialog mProgressDialog;

    @ViewById(R.id.base_id_back)
    View mBackView;

    @ViewById(R.id.base_id_title)
    TextView mTitleTxt;

    @ViewById(R.id.base_right_btn)
    ImageView mRightImage;

    @ViewById(R.id.base_right_txt)
    TextView mRightTxt;

    @ViewById
    WebView webview;

    @Extra
    GridItem toolItem;

    private String widgetPath;

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
            public void onClick(View v) {
                finish();
            }
        });
        mTitleTxt.setText(toolItem.name);

        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(false);
        webSettings.setAppCacheEnabled(true);

        webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogUtil.d("url    :" + url);
                if (url.startsWith("elvshitools://")) {
                    GridItem item = new GridItem();
                    item.name = "";
                    item.url = url.replace("elvshitools://", "");
                    Intent intent = new Intent(ToolWebViewActivity.this, ToolWebViewActivity_.class);
                    intent.putExtra("toolItem", item);
                    startActivity(intent);
                }
                return true;
            }
        });
        webview.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ToolWebViewActivity.this);
                builder.setTitle("提示消息")
                        .setMessage(message)
                        .setPositiveButton(R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
                result.confirm();
                return true;
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                mTitleTxt.setText(title);
            }
        });
        webview.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        widgetPath = getApplicationContext().getFilesDir().getAbsolutePath() + "/widget";
        if(!FileUtil.isFileExist(widgetPath + "/version.json")) {
            copyAssetsFile();
        } else {
            int result = -1;
            try {
                String versionJson1 = FileUtil.getAssetsFileContent(getResources(), "widget/version.json").trim();
                StringBuilder versionJson2 = FileUtil.readFile(widgetPath + "/version.json", CharsetCst.UTF_8);
                String version = StringUtil.getJsonField(versionJson1, "Version");
                String currentVersion = StringUtil.getJsonField(versionJson2.toString(), "Version");
                result = StringUtil.compareVersion(currentVersion, version);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (result >= 0) {
                loadWebView();
            } else {
                FileUtil.deleteFile(widgetPath);
                copyAssetsFile();
            }
        }
    }

    private void copyAssetsFile() {
        new LocalFuture.Builder(this)
                .setHandler(CopyAssetsHandler.class)
                .setListener(new AgnettyFutureListener(){
                    @Override
                    public void onStart(AgnettyResult result) {
                        mProgressDialog = AppUtil.showProgressDialog(ToolWebViewActivity.this, "数据初始化中，请稍等...");
                    }

                    @Override
                    public void onComplete(AgnettyResult result) {
                        mProgressDialog.cancel();
                        loadWebView();
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        mProgressDialog.cancel();
                    }
                })
                .execute();
    }

    private void loadWebView() {
        String filePath = widgetPath + "/" + toolItem.url;
        LogUtil.d("load file :" + filePath);
        try {
            String html = FileUtil.readFile(filePath, CharsetCst.UTF_8).toString();
            String baseUrl = filePath.substring(0, filePath.lastIndexOf("/") + 1);
            webview.loadDataWithBaseURL("file://" + baseUrl, html, "text/html", CharsetCst.UTF_8, null);
        } catch (FileNotFoundException e) {
            AppUtil.showToast(this, "找不到文件" + toolItem.url);
        }
    }

}
