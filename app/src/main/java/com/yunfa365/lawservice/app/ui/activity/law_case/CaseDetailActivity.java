package com.yunfa365.lawservice.app.ui.activity.law_case;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyResult;
import com.google.gson.JsonObject;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.Audit;
import com.yunfa365.lawservice.app.pojo.Case;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;
import com.yunfa365.lawservice.app.utils.ArrayUtil;
import com.yunfa365.lawservice.app.utils.FormFactory;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EActivity(R.layout.activity_case_detail)
public class CaseDetailActivity extends BaseUserActivity {

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

    private Case item;
    private List<String> invisibleField = Arrays.asList("CaseId", "CustId");
    private Map<String, String> invisibleFieldValue = new HashMap<>();
    private Map<String, Object> linkField;

    {
        linkField = new HashMap<>();
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
        mTitleTxt.setText("案件详情");

        loadData();
    }

    private void initView(String RData) {
        containerLayout.addView(FormFactory.createGroupTitle(this, "案件详情"));
        try {
            JSONArray jsonArray = new JSONArray(RData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String labelStr = jsonObject.getString("DKey");
                String valueStr = jsonObject.getString("DValue");
                if (invisibleField.contains(labelStr)) {
                    invisibleFieldValue.put(labelStr, valueStr);
                    continue;
                }
                View fieldView = FormFactory.createTextField(this, labelStr + "：", valueStr);
                containerLayout.addView(fieldView);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void loadData() {
        showLoading();
        AppRequest request = new AppRequest.Build("api/Case/Des_Get")
                .addParam("CaseId", ID + "")
                .addParam("GetType", "1") //获取类型，1：获取拼接好的键值对，2：获取数据库源数据；不传值默认为1
                .create();
        new HttpFormFuture.Builder(this)
                .setData(request)
                .setListener(new AgnettyFutureListener(){

                    @Override
                    public void onComplete(AgnettyResult result) {
                        hideLoading();
                        AppResponse resp = (AppResponse)result.getAttach();
                        if (resp.flag) {
                            initView(resp.RData);
                        } else {
                            showToast(resp.Message);
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
