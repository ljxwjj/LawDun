package com.yunfa365.lawservice.app.ui.activity.finance;

import android.app.DatePickerDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyResult;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.Bill;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;
import com.yunfa365.lawservice.app.utils.DateUtil;
import com.yunfa365.lawservice.app.utils.FileUtil;
import com.yunfa365.lawservice.app.utils.FormFactory;
import com.yunfa365.lawservice.app.utils.StringUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

@EActivity(R.layout.activity_bill_detail)
public class BillDetailActivity extends BaseUserActivity {

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

    @ViewById
    LinearLayout auditForm;

    @ViewById
    EditText fph;

    @ViewById
    TextView kprq;

    @ViewById
    EditText make;

    @Extra
    int ID;

    Bill item;

    @AfterViews
    void init() {

        mBackView.setVisibility(View.VISIBLE);
        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleTxt.setText("审批详情");

        loadData();
    }

    @Click(R.id.kprq)
    void kprqOnClick(TextView view) {
        String rq = view.getText().toString();
        final Calendar calendar = Calendar.getInstance();
        if (TextUtils.isEmpty(rq)) {
            calendar.setTime(new Date());
        } else {
            calendar.setTime(StringUtil.formatDate(rq, "yyyy-MM-dd"));
        }
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String rq = DateUtil.formatDate(calendar, "yyyy-MM-dd");
                view.setText(rq);
            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Click(R.id.submitBtn)
    void submitBtnOnClick() {
        doSubmit();
    }

    private void initView() {
        containerLayout.removeView(auditForm);

        containerLayout.addView(FormFactory.createGroupTitle(this, "发票申请信息"));
        String customInfoPageFormat = FileUtil.getRawFileContent(getResources(), R.raw.bill_detail_page_format);
        try {
            JSONArray jsonArray = new JSONArray(customInfoPageFormat);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                containerLayout.addView(FormFactory.createFieldByJson(this, jsonObject, item));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        if (item.Stat == 0) {
            containerLayout.addView(FormFactory.createGroupTitle(this, "开具发票"));
            containerLayout.addView(auditForm);
        } else {
            containerLayout.addView(FormFactory.createGroupTitle(this, "开票信息"));

            containerLayout.addView(FormFactory.createTextField(this, "开票人：", item.SUidTxt));
            containerLayout.addView(FormFactory.createTextField(this, "开票时间：", item.BillTime));
            containerLayout.addView(FormFactory.createTextField(this, "发票号：", item.BillNums));
            containerLayout.addView(FormFactory.createTextField(this, "开票说明：", item.BillMake));
        }
    }

    private void loadData() {
        showLoading();
        AppRequest request = new AppRequest.Build("api/Finance/Bill_Des_Get")
                .addParam("Bid", ID + "")
                .create();
        new HttpFormFuture.Builder(this)
                .setData(request)
                .setListener(new AgnettyFutureListener(){

                    @Override
                    public void onComplete(AgnettyResult result) {
                        hideLoading();
                        AppResponse resp = (AppResponse)result.getAttach();
                        if (resp.flag) {
                            item = resp.getFirstObject(Bill.class);
                            initView();
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

    private void doSubmit() {
        showLoading();
        AppRequest request = new AppRequest.Build("api/Finance/Bill_Open")
                .addParam("Bid", ID + "")
                .addParam("BillNums", fph.getText().toString())
                .addParam("BillTime", kprq.getText().toString())
                .addParam("BillMake", make.getText().toString())
                .create();
        new HttpFormFuture.Builder(this)
                .setData(request)
                .setListener(new AgnettyFutureListener(){

                    @Override
                    public void onComplete(AgnettyResult result) {
                        hideLoading();
                        AppResponse resp = (AppResponse)result.getAttach();
                        if (resp.flag) {
                            setResult(RESULT_OK);
                            finish();
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
