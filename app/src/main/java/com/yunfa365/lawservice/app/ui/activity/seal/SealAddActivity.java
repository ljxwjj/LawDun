package com.yunfa365.lawservice.app.ui.activity.seal;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyResult;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.BhSeal;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;
import com.yunfa365.lawservice.app.ui.view.form.SingleInputField;
import com.yunfa365.lawservice.app.ui.view.form.TextViewField;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_seal_add)
public class SealAddActivity extends BaseUserActivity {

    @ViewById(R.id.base_id_back)
    View mBackView;

    @ViewById(R.id.base_id_title)
    TextView mTitleTxt;

    @ViewById(R.id.base_right_btn)
    ImageView mRightImage;

    @ViewById(R.id.base_right_txt)
    TextView mRightTxt;

    @ViewById
    SingleInputField name;

    @ViewById
    TextViewField macAddress;

    @Extra
    String mac;

    @AfterViews
    void init() {
        macAddress.setValueStr(mac);
    }

    @Click(R.id.submitBtn)
    public void submitBtnOnClick() {
        String title = name.getValue().toString();
        AppRequest request = new AppRequest.Build("api/WebSet/Zhang_Add")
                .addParam("ZTitle", title)
                .addParam("ZMac", mac)
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
                            showToast(resp.Message);
                            BhSeal item = resp.getFirstObject(BhSeal.class);

                            Intent data = new Intent();
                            data.putExtra("item", item);
                            setResult(RESULT_OK, data);
                            finish();
                        } else {
                            showToast(resp.Message);
                        }
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        hideLoading();
                        result.getException().printStackTrace();
                    }
                })
                .execute();
    }

}
