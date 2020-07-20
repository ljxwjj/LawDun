package com.yunfa365.lawservice.app.ui.activity.mycase;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyResult;
import com.google.android.material.snackbar.Snackbar;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.CusTomCols;
import com.yunfa365.lawservice.app.pojo.Custom;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.CommonLocationActivity_;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;
import com.yunfa365.lawservice.app.ui.activity.office.Office_addCustomActivity_;
import com.yunfa365.lawservice.app.utils.LocationUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;


/**
 * Created by Administrator on 2016/5/5.
 * 客户详情
 */
@EActivity(R.layout.activity_custom_info)
public class CustomInfoActivity extends BaseUserActivity {

    private static final int EDIT_REQUEST_CODE = 1;

    @ViewById
    View rootLayout;

    @ViewById(R.id.base_id_back)
    View mBackView;

    @ViewById(R.id.base_id_title)
    TextView mTitleTxt;

    @ViewById(R.id.base_right_btn)
    ImageView mRightImage;

    @ViewById(R.id.base_right_txt)
    TextView mRightTxt;

    @ViewById
    EditText wtr;

    @ViewById
    EditText ywlxr;

    @ViewById
    EditText zw;

    @ViewById
    EditText zyfzr;

    @ViewById
    EditText dqyxl;

    @ViewById
    EditText sjhm;

    @ViewById
    EditText gddh;

    @ViewById
    EditText yx;

    @ViewById
    EditText xxdz;

    @ViewById
    EditText xgzj;

    @ViewById
    EditText sfzh;

    @ViewById
    EditText khnx;

    @ViewById
    EditText khnx_1;

    @ViewById
    EditText khnx_2;

    @ViewById
    EditText khnx_3;

    @ViewById
    EditText khnx_4;

    @ViewById
    TextView khnx1label;

    @ViewById
    TextView khnx2label;

    @ViewById
    TextView khnx3label;

    @ViewById
    TextView khnx4label;

    @ViewById
    View khnx1layout;

    @ViewById
    View khnx2layout;

    @ViewById
    View khnx3layout;

    @ViewById
    View khnx4layout;

    @ViewById
    EditText sheng;

    @ViewById
    EditText shi;

    @ViewById
    EditText ajbz;

    @Extra
    Custom customItem;

    private EditText[] khnxViews;
    private TextView[] khnxLabels;
    private View[] khnxLayouts;

    @AfterViews
    void init() {
        mBackView.setVisibility(View.VISIBLE);
        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleTxt.setText("客户详情");

        mRightImage.setVisibility(View.VISIBLE);
        mRightImage.setImageResource(R.mipmap.edit_normal);
        mRightImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomInfoActivity.this, Office_addCustomActivity_.class);
                intent.putExtra("customItem", customItem);
                startActivityForResult(intent, EDIT_REQUEST_CODE);
            }
        });

        khnxViews = new EditText[]{khnx_1, khnx_2, khnx_3, khnx_4};
        khnxLabels = new TextView[]{khnx1label, khnx2label, khnx3label, khnx4label};
        khnxLayouts = new View[]{khnx1layout, khnx2layout, khnx3layout, khnx4layout};

        khnx1layout.setVisibility(View.GONE);
        khnx2layout.setVisibility(View.GONE);
        khnx3layout.setVisibility(View.GONE);
        khnx4layout.setVisibility(View.GONE);

        if (customItem == null){
            showToast("案件不存在");
            finish();
            return;
        }
        loadCustomDetail();
    }

    private void loadCustomDetail() {
        // 客户详情
        AppRequest request = new AppRequest.Build("Customer/GetCustomerDetail")
                .addParam("ObjID", customItem.ID + "")
                .create();
        new HttpFormFuture.Builder(this)
                .setData(request)
                .setListener(new AgnettyFutureListener(){
                    @Override
                    public void onStart(AgnettyResult result) {
                        showLoading();
                    }

                    @Override
                    public void onComplete(AgnettyResult result) {
                        hideLoading();
                        AppResponse resp = (AppResponse)result.getAttach();
                        if (resp.flag) {
                            customItem = resp.resultsToObject(Custom.class);

                            initDefaultValue();
                            initDefaultKhnx();
                        }
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        hideLoading();
                    }
                })
                .execute();
    }

    private void initDefaultValue() {
        khnx.setText(customItem.CustColsName);
        wtr.setText(customItem.Title);
        sjhm.setText(customItem.Phone);
        sheng.setText(customItem.Province);
        shi.setText(customItem.City);
        ajbz.setText(customItem.Make);
        xgzj.setText(customItem.UNums);
        sfzh.setText(customItem.IdCard);

        ywlxr.setText(customItem.YwRen);
        zw.setText(customItem.YwRenZhiWu);
        zyfzr.setText(customItem.FzRen);
        dqyxl.setText(customItem.YingXiangLi);
        gddh.setText(customItem.Phone2);
        yx.setText(customItem.Email);
        xxdz.setText(customItem.Address);
    }


    private void initDefaultKhnx() {
        String khsxStr = customItem.Model;
        String sxs[] = khsxStr.split("♀");
        for (int i =0; i < sxs.length; i++) {
            String sx = sxs[i];
            if (TextUtils.isEmpty(sx)) {
                continue;
            }
            String kvs[] = sx.split("\\|");

            khnxLayouts[i].setVisibility(View.VISIBLE);
            CusTomCols labelTag = new CusTomCols();
            CusTomCols viewTag = new CusTomCols();

            labelTag.Title = kvs[0];
            viewTag.Title = kvs[1];
            khnxLabels[i].setText(kvs[0]);
            khnxViews[i].setText(kvs[1]);
        }
    }

    @Click(R.id.button1)
    void button1OnClick(View view) {
//        Intent intent = new Intent(this, CaseJobLogActivity_.class);
//        intent.putExtra("CustomId", customItem.ID + "");
//        startActivity(intent);
    }

    @Click(R.id.button2)
    void button2OnClick(View view) {
//        Intent intent = new Intent(this, CustomCaseActivity_.class);
//        intent.putExtra("CustomId", customItem.ID + "");
//        startActivity(intent);
    }

    @Click(R.id.button3)
    void button3OnClick(View view) {
//        Intent intent = new Intent(this, CaseFileDetailActivity_.class);
//        intent.putExtra("Mid", customItem.ID + "");
//        intent.putExtra("Cols", "1");
//        startActivity(intent);
    }

    @OnActivityResult(EDIT_REQUEST_CODE)
    void addOnResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            setResult(RESULT_OK);
            customItem = (Custom) data.getSerializableExtra("customItem");
            initDefaultValue();

            khnx1layout.setVisibility(View.GONE);
            khnx2layout.setVisibility(View.GONE);
            khnx3layout.setVisibility(View.GONE);
            khnx4layout.setVisibility(View.GONE);
            initDefaultKhnx();
        }
    }

    @Click(R.id.sjhm)
    void sjhmOnClick(View view) {
        final String phone = ((EditText)view).getText().toString();
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.CALL_PHONE)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        cellPhone(phone);
                    } else {
                        showToast("获取权限失败");
                    }
                }, Throwable::printStackTrace);
    }

    @Click(R.id.gddh)
    void gddhOnClick(View view) {
        final String phone = ((EditText)view).getText().toString();
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.CALL_PHONE)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        cellPhone(phone);
                    } else {
                        showToast("获取权限失败");
                    }
                }, Throwable::printStackTrace);
    }

    private void cellPhone(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return;
        }
        try {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
            startActivity(intent);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Click(R.id.xxdz)
    void xxdzOnClick(View view) {
        String address = ((EditText)view).getText().toString();
        if (TextUtils.isEmpty(address)) {
            showToast("无效地址");
            return;
        }
        Intent intent = new Intent(this, CommonLocationActivity_.class);
        intent.putExtra("targetCity", customItem.City);
        intent.putExtra("targetAddress", address);
        startActivity(intent);
    }

    @Click(R.id.yx)
    void yxOnClick(View view) {
        if (TextUtils.isEmpty(customItem.Email)) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + customItem.Email));
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        intent.putExtra(Intent.EXTRA_TEXT, "");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            showToast("手机上未找到邮件程序");
        }
    }

}
