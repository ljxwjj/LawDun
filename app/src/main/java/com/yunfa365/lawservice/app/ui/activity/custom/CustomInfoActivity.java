package com.yunfa365.lawservice.app.ui.activity.custom;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyResult;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.Custom;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;
import com.yunfa365.lawservice.app.utils.FileUtil;
import com.yunfa365.lawservice.app.utils.FormFactory;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


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
    LinearLayout containerLayout;

    @Extra
    int ID;

    private Custom customItem;


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

        loadCustomDetail();
    }

    private void initView() {
        String customInfoPageFormat = FileUtil.getRawFileContent(getResources(), R.raw.custominfo_page_format);
        try {
            JSONArray jsonArray = new JSONArray(customInfoPageFormat);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                containerLayout.addView(FormFactory.createFieldByJson(this, jsonObject, customItem));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private void loadCustomDetail() {
        // 客户详情
        AppRequest request = new AppRequest.Build("api/Custom/Des_Get")
                .addParam("Cid", ID + "")
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
                            customItem = resp.getFirstObject(Custom.class);

                            initView();
                        }
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        hideLoading();
                    }
                })
                .execute();
    }


}
