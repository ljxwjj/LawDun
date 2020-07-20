package com.yunfa365.lawservice.app.ui.activity.office;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyResult;
import com.android.agnetty.utils.LogUtil;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.DecimalMin;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.Case;
import com.yunfa365.lawservice.app.pojo.CaseCols;
import com.yunfa365.lawservice.app.pojo.CaseFile;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;
import com.yunfa365.lawservice.app.utils.AppUtil;
import com.yunfa365.lawservice.app.utils.DateUtil;
import com.yunfa365.lawservice.app.utils.StringUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/4/20.
 */
@EActivity(R.layout.activity_office41_5)
public class Office41_5Activity extends BaseUserActivity {

    @ViewById(R.id.base_id_back)
    View mBackView;

    @ViewById(R.id.base_id_title)
    TextView mTitleTxt;

    @ViewById(R.id.base_right_btn)
    ImageView mRightImage;

    @ViewById(R.id.base_right_txt)
    TextView mRightTxt;

    @ViewById
    EditText fwlx;

    @NotEmpty(message = "服务时间不能为空")
    @Order(1)
    @ViewById
    EditText fwsj;

    @NotEmpty(message = "服务人次不能为空")
    @Order(2)
    @ViewById
    EditText fwrc;

    @NotEmpty(message = "代书事项不能为空")
    @Order(3)
    @ViewById
    EditText dssx;

    @NotEmpty(message = "服务费用不能为空")
    @Order(4)
    @DecimalMin(value = 0)
    @ViewById
    EditText fwfy;

    @ViewById
    EditText wtr;

    @ViewById
    EditText zsah;

    @NotEmpty(message = "案源人不能为空")
    @Order(17)
    @ViewById
    EditText ayr;

    @ViewById
    EditText disr;

    @NotEmpty(message = "相关文件不能为空")
    @Order(18)
    @ViewById
    TextView xgwj;

    @ViewById
    EditText bzsm;

    @ViewById
    TextView moreBtn;

    @ViewById
    View moreLayout;

    @Extra
    CaseCols selectedCaseCols;

    @Extra
    Case caseItem;

    private String Mid;
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
        mTitleTxt.setText("案件登记");
        //loadDataSsjd();
        initValidate();
        loadRequiredFields();
        initDefaultValue();
    }

    private void initDefaultValue() {
        if (caseItem == null) {
            Mid = AppUtil.generateMid();
            fwlx.setText(selectedCaseCols.Title);
        } else {
            Mid = caseItem.ID;
            loadCaseFiles();
            fwsj.setText(caseItem.Begtime);
            fwrc.setText(caseItem.Ssbd);
            dssx.setText(caseItem.AyMake);
            fwfy.setText(caseItem.Price + "");
            wtr.setText(caseItem.TWtr);
            bzsm.setText(caseItem.Des);
            fwlx.setText(caseItem.AyTxt);
            zsah.setText(caseItem.UserDefId);
            ayr.setText(caseItem.AnYuanRen);
            disr.setText(caseItem.DiSanRen);
        }
    }

    private void loadCaseFiles() {
        AppRequest request = new AppRequest.Build("Case/FileStoreList")
                .addParam("Mid", Mid)
                .addParam("Cols", "5")
                .create();
        new HttpFormFuture.Builder(this)
                .setData(request)
                .setListener(new AgnettyFutureListener(){

                    @Override
                    public void onComplete(AgnettyResult result) {
                        hideLoading();
                        AppResponse resp = (AppResponse)result.getAttach();
                        if (resp.flag) {
                            List<CaseFile> files = resp.resultsToList(CaseFile.class);
                            xgwj.setTag(files);
                            displayXgwj(files);
                        }
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        hideLoading();
                    }
                })
                .execute();
    }

    private void displayXgwj(List<CaseFile> files) {
        String[] names = new String[files.size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = files.get(i).FileName;
        }
        xgwj.setText(StringUtil.implode(names, ","));
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

    @Click(R.id.fwsj)
    void sarqOnClick(View view) {
        String rq = fwsj.getText().toString();
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
                fwsj.setText(rq);
            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Click(android.R.id.button1)
    void button1OnClick(View view) {
        validator.validate();
    }

    private void showMoreLayout() {
        moreLayout.setVisibility(View.VISIBLE);
        moreBtn.setText("收起");
    }

    private void doCommit() {
        AppRequest.Build build = new AppRequest.Build("Case/UpdateCaseZXWS")
                .addParam("Cols", selectedCaseCols.Fid)
                .addParam("Ay", selectedCaseCols.ID)
                .addParam("BegTime", fwsj.getText().toString())     //服务时间
                .addParam("Ssbd", fwrc.getText().toString())        //服务人次
                .addParam("AyMake", dssx.getText().toString())      //咨询/代书事项
                .addParam("Price", fwfy.getText().toString())       //服务费用
                .addParam("TWtr", wtr.getText().toString())        //委托人
                .addParam("DiSanRen", disr.getText().toString())    //第三人
                .addParam("UserDefId", zsah.getText().toString())   //专属案号
                .addParam("AnYuanRen", ayr.getText().toString())    // 案源人
                .addParam("Des", bzsm.getText().toString())         //备注说明
                .addParam("Mid", Mid);
        if (caseItem != null) build.addParam("CaseId", caseItem.ID);
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

    private void loadRequiredFields() {
        AppRequest request = new AppRequest.Build("Case/FieldIsRequired")
                .create();
        new HttpFormFuture.Builder(this)
                .setData(request)
                .setListener(new AgnettyFutureListener(){

                    @Override
                    public void onComplete(AgnettyResult result) {
                        hideLoading();
                        AppResponse resp = (AppResponse)result.getAttach();
                        if (resp.flag) {
                            List<String> requiredFields = resp.resultsToList(String.class);
                            if (!requiredFields.contains("AnYuanRen")) {
                                validator.removeRules(ayr);
                                ayr.setHint("请填写案源人(选填)");
                            } else {
                                showMoreLayout();
                            }
                            if (!requiredFields.contains("CaseFileUp")) {
                                validator.removeRules(xgwj);
                                xgwj.setHint("请选择相关文件(选填)");
                            } else {
                                showMoreLayout();
                            }
                        }
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        hideLoading();
                    }
                })
                .execute();
    }

    @Click(R.id.moreBtn)
    void moreBtnOnClick(TextView view) {
        if (moreLayout.getVisibility() == View.GONE) {
            moreLayout.setVisibility(View.VISIBLE);
            view.setText("收起");
        } else {
            moreLayout.setVisibility(View.GONE);
            view.setText("展开");
        }
    }

}
