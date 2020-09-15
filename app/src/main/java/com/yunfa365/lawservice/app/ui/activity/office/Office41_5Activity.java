package com.yunfa365.lawservice.app.ui.activity.office;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyResult;
import com.android.agnetty.utils.LogUtil;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.constant.AppCst;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.Case;
import com.yunfa365.lawservice.app.pojo.CaseCols;
import com.yunfa365.lawservice.app.pojo.Custom;
import com.yunfa365.lawservice.app.pojo.User;
import com.yunfa365.lawservice.app.pojo.base.BaseBean;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;
import com.yunfa365.lawservice.app.ui.activity.custom.Office_searchCustomActivity_;
import com.yunfa365.lawservice.app.ui.dialog.SpinnerDialog;
import com.yunfa365.lawservice.app.utils.AppUtil;
import com.yunfa365.lawservice.app.utils.DateUtil;
import com.yunfa365.lawservice.app.utils.StringUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/4/20.
 *
 * 咨询代写文书
 */
@EActivity(R.layout.activity_office41_5)
public class Office41_5Activity extends BaseUserActivity {

    private static final int WTR_REQUEST_CODE = 1;
    private static final int ZYRY_REQUEST_CODE = 2;

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
    TextView ajlx;

    @ViewById
    TextView anh;

    @NotEmpty(message = "执业人员不能为空")
    @Order(1)
    @ViewById
    EditText zyry;

    // sequence 字段内规则执行顺序
    @NotEmpty(message="收案日期不能为空", sequence = 1)
    @Order(2)
    @ViewById
    EditText sarq;

    @NotEmpty(message = "代书事项不能为空")
    @Order(3)
    @ViewById
    EditText dssx;

    @NotEmpty(message = "服务人次不能为空")
    @Order(4)
    @ViewById
    EditText fwrc;

    @NotEmpty(message="委托人不能为空")
    @Order(5)
    @ViewById
    EditText wtr;

    @ViewById
    EditText sffs;

    @NotEmpty(message="不能为空")
    @Order(7)
    @ViewById
    EditText dlf;

    @ViewById
    EditText bzsm;

    @Extra
    CaseCols selectedCaseCols;

    @Extra
    Case caseItem;

    private Validator validator;

    @AfterViews
    void init(){
        mBackView.setVisibility(View.VISIBLE);
        mBackView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (caseItem != null) {
            selectedCaseCols = new CaseCols();
            selectedCaseCols.Fid = caseItem.ColsV1;
            selectedCaseCols.ID = caseItem.ColsV2;
        }
        mTitleTxt.setText("案件登记");
        initRadStarLabel(rootLayout);
        initValidate();
        initDefaultValue();
    }

    private void initRadStarLabel(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                view = group.getChildAt(i);
                initRadStarLabel(view);
            }
        } else if (view instanceof TextView) {
            TextView textView = (TextView) view;
            String text = textView.getText().toString();
            if (text.startsWith("*")) {
                Object radSpan = new ForegroundColorSpan(Color.RED);
                SpannableStringBuilder style = new SpannableStringBuilder(text);
                style.setSpan(radSpan, 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                textView.setText(style);
            }
        }
    }

    private void initDefaultValue() {
        if (caseItem == null) {
            ajlx.setText(selectedCaseCols.Title);

            sffs.setText(AppCst.sffss[1].toString());
            sffs.setTag(AppCst.sffss[1]);
        } else {
            anh.setText(caseItem.CaseIdTxt);
            ajlx.setText(caseItem.ColsV2Txt);
            zyry.setText(caseItem.UsersListTxt);
            sarq.setText(caseItem.BegTime);
            dssx.setText(caseItem.AyMake);
            fwrc.setText(caseItem.Ssbd);
            wtr.setText(caseItem.CustIdTxt);
            sffs.setText(caseItem.PayColsTxt);
            sffs.setTag(new BaseBean(caseItem.PayCols, null));
            dlf.setText(caseItem.CasePrice + "");
            bzsm.setText(caseItem.Des);
        }
    }

    private void initValidate() {
        validator = new Validator(this);
        validator.setValidationMode(Validator.Mode.IMMEDIATE);
        validator.setValidationListener(new Validator.ValidationListener() {
            @Override
            public void onValidationSucceeded() {
                doCommit();
            }

            @Override
            public void onValidationFailed(List<ValidationError> errors) {
                for (ValidationError error : errors) {
                    View view = error.getView();
                    String message = error.getCollatedErrorMessage(getBaseContext());

                    if (view instanceof EditText && view.isFocusable()) {
                        view.requestFocus();
                        ((EditText) view).setError(message);
                    } else {
                        AppUtil.showToast(getBaseContext(), message);
                    }
                }
            }
        });
    }

    @Click(R.id.zyry)
    void zyryOnClick() {
        Intent intent = new Intent(this, Office_searchLawyerActivity_.class);
        startActivityForResult(intent, ZYRY_REQUEST_CODE);
    }

    @Click(R.id.sarq)
    void sarqOnClick(View view) {
        String rq = sarq.getText().toString();
        final Calendar calendar = Calendar.getInstance();
        if (TextUtils.isEmpty(rq)) {
            calendar.setTime(new Date());
        } else {
            calendar.setTime(StringUtil.formatDate(rq, "yyyy-MM-dd"));
        }
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String rq = DateUtil.formatDate(calendar, "yyyy-MM-dd");
                sarq.setText(rq);
            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Click(R.id.sffs)
    void sffsOnClick(View view) {
        new SpinnerDialog(this, "请选择收费方式", AppCst.sffss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sffs.setText(AppCst.sffss[which].toString());
                sffs.setTag(AppCst.sffss[which]);
            }
        }).show();
    }

    @Click(R.id.wtr)
    void wtrOnClick(View view) {
        Intent intent = new Intent(this, Office_searchCustomActivity_.class);
        startActivityForResult(intent, WTR_REQUEST_CODE);
    }

    @Click(android.R.id.button1)
    void button1OnClick(View view) {
        validator.validate();
    }

    private void doCommit() {
        AppRequest.Build build = new AppRequest.Build("api/Case/Add_DS")
                .addParam("CaseId", caseItem == null?"0":caseItem.ID + "")
                .addParam("ColsV1", selectedCaseCols.Fid + "")
                .addParam("ColsV2", selectedCaseCols.ID + "")
                .addParam("UsersList", zyry.getText().toString())
                .addParam("BegTime", sarq.getText().toString())         // 收案日期
                .addParam("AyMake", dssx.getText().toString())      //咨询/代书事项
                .addParam("Ssbd", fwrc.getText().toString())        //服务人次
                .addParam("CustName", wtr.getText().toString())         // 委托人
                .addParam("PayCols", ((BaseBean)sffs.getTag()).ID + "") // 收费方式
                .addParam("CasePrice", dlf.getText().toString())        // 代理费
                .addParam("Des", bzsm.getText().toString())             // 备注说明
                ;

        AppRequest request = build.create();
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
                            String ajID = resp.RData;
                            LogUtil.d("案件添加成功，ID：" + ajID);
                            setResult(RESULT_OK);
                            finish();
                            LogUtil.d("案件修改保存成功");
                        }
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        hideLoading();
                    }
                })
                .execute();
    }

    @OnActivityResult(WTR_REQUEST_CODE)
    void selectWtrResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Custom custom = (Custom) data.getSerializableExtra("customItem");
            wtr.setText(custom.Title);
        }
    }

    @OnActivityResult(ZYRY_REQUEST_CODE)
    void selectLawyerResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            List<User> lawyers = (List<User>) data.getSerializableExtra("lawyers");
            String[] lawNames = new String[lawyers.size()];
            for (int i = 0; i < lawNames.length; i++) {
                lawNames[i] = lawyers.get(i).FullName;
            }
            zyry.setText(StringUtil.implode(lawNames, ","));
        }
    }

}
