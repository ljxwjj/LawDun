package com.yunfa365.lawservice.app.ui.activity.joblog;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyResult;
import com.android.agnetty.future.upload.form.FormUploadFile;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.Case;
import com.yunfa365.lawservice.app.pojo.Custom;
import com.yunfa365.lawservice.app.pojo.JobLog;
import com.yunfa365.lawservice.app.pojo.JobLogCols;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;
import com.yunfa365.lawservice.app.ui.activity.custom.Office_searchCustomActivity_;
import com.yunfa365.lawservice.app.ui.activity.law_case.SelectCaseActivity_;
import com.yunfa365.lawservice.app.ui.dialog.DateTimePickDialogUtil;
import com.yunfa365.lawservice.app.ui.dialog.SpinnerDialog;
import com.yunfa365.lawservice.app.ui.validation.NotEmptyQuickRule;
import com.yunfa365.lawservice.app.utils.DateUtil;
import com.yunfa365.lawservice.app.utils.FileUtil;
import com.yunfa365.lawservice.app.utils.UriUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@EActivity(R.layout.activity_joblog_add)
public class JoblogAddActivity extends BaseUserActivity {
    private static final int XGAJ_REQUEST_CODE = 1;
    private static final int XGFJ_REQUEST_CODE = 2;
    private static final int WTR_REQUEST_CODE = 3;
    private JobLogCols[] allCols;
    private JobLogCols[] rznxs;
    private JobLogCols[] rznbs;

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

    @NotEmpty(message = "日志类型不能为空")
    @Order(1)
    @ViewById
    EditText rznx;

    @NotEmpty(message = "日志类别不能为空")
    @Order(2)
    @ViewById
    EditText rznb;

    @Order(3)
    @ViewById
    EditText xgaj;

    @Order(4)
    @ViewById
    EditText khmc;

    @NotEmpty(message = "开始时间不能为空")
    @Order(5)
    @ViewById
    EditText kssj;

    @NotEmpty(message = "结束时间不能为空")
    @Order(6)
    @ViewById
    EditText jssj;

    @NotEmpty(message = "工作描述不能为空")
    @Order(9)
    @ViewById
    EditText gzms;

    @ViewById
    EditText xgfj;

    @Extra
    JobLog joblogItem;

    @Extra
    String CaseId;

    private Validator validator;
    @AfterViews
    void init() {
        mBackView.setVisibility(View.VISIBLE);
        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleTxt.setText("创建工作日志");

        initDefaultValue();

        loadColsList();
    }

    private void initValidate(JobLogCols logCols) {
        validator = new Validator(this);
        if ("1".equals(logCols.SelectCase)) {
            validator.put(xgaj, new NotEmptyQuickRule("相关案件不能为空"));
        }
        if ("1".equals(logCols.SelectCust)) {
            validator.put(khmc, new NotEmptyQuickRule("客户名称不能为空"));
        }
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

                    if (view instanceof  EditText && view.isFocusable()) {
                        view.requestFocus();
                        ((EditText) view).setError(message);
                    } else {
                        showToast(message);
                    }
                }
            }
        });
    }
    private void initDefaultValue() {
        if (joblogItem == null) {
            Date now = new Date();
            kssj.setText(DateUtil.formatDate(now, "yyyy-MM-dd HH:mm"));
            jssj.setText(DateUtil.formatDate(now, "yyyy-MM-dd HH:mm"));

        } else {
            rznx.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            rznb.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

            if (joblogItem.CustId > 0) {
                Custom custom = new Custom();
                custom.ID = joblogItem.CustId;
                khmc.setText(joblogItem.CustName);
                khmc.setTag(custom);
            }
            kssj.setText(joblogItem.BegTime);
            jssj.setText(joblogItem.EndTime);
            gzms.setText(joblogItem.Des);
            xgfj.setText(FileUtil.getFileName(joblogItem.FilePath));

            if (joblogItem.CaseId > 0) {
                Case caseDetail = new Case();
                caseDetail.ID = joblogItem.CaseId;
                xgaj.setText(joblogItem.CaseIdTxt);
                xgaj.setTag(caseDetail);
            }
        }
    }

    private void setRznx(JobLogCols logCols) {
        rznx.setText(logCols.toString());
        rznx.setTag(logCols);
        rznbs = getJobLogCols(logCols.ID);

        rznb.setText(rznbs[0].toString());
        rznb.setTag(rznbs[0]);

        View xgajLayout = (View) xgaj.getParent().getParent();
        View khLayout = (View) khmc.getParent().getParent();
        if (logCols.SelectCase == 0) {
            xgajLayout.setVisibility(View.GONE);
        } else {
            xgajLayout.setVisibility(View.VISIBLE);
        }
        if (logCols.SelectCust == 0) {
            khLayout.setVisibility(View.GONE);
        } else {
            khLayout.setVisibility(View.VISIBLE);
        }
        initValidate(logCols);
    }

    @Click(R.id.rznx)
    void rznxOnClick(View view) {
        if (rznxs == null || joblogItem != null) {
            return;
        }
        new SpinnerDialog(this, "选择日志类型", rznxs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setRznx(rznxs[which]);
            }
        }).show();
    }

    @Click(R.id.rznb)
    void rznbOnClick(View view) {
        if (rznbs == null || joblogItem != null) {
            return;
        }
        new SpinnerDialog(this, "选择日志类别", rznbs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                rznb.setText(rznbs[which].toString());
                rznb.setTag(rznbs[which]);
            }
        }).show();
    }

    @Click(R.id.khmc)
    void khmcOnClick(View view) {
        Intent intent = new Intent(this, Office_searchCustomActivity_.class);
        startActivityForResult(intent, WTR_REQUEST_CODE);
    }

    @Click(R.id.kssj)
    void kssjOnClick(View view) {
        String rq = kssj.getText().toString();
        DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(this, rq);
        dateTimePicKDialog.dateTimePicKDialog(kssj);
    }

    @Click(R.id.jssj)
    void jssjOnClick(View view) {
        String rq = jssj.getText().toString();
        DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(this, rq);
        dateTimePicKDialog.dateTimePicKDialog(jssj);
    }


    @Click(R.id.xgaj)
    void xgajOnClick(View view) {
        Intent intent = new Intent(this, SelectCaseActivity_.class);
        startActivityForResult(intent, XGAJ_REQUEST_CODE);
    }

    @OnActivityResult(XGAJ_REQUEST_CODE)
    void xgajOnResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Case caseItem = (Case) data.getSerializableExtra("caseItem");
            xgaj.setText(caseItem.CaseIdTxt);
            xgaj.setTag(caseItem);
        }
    }

    @Click(R.id.xgfj)
    void xgfjOnClick(View view) {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        startGetContent();
                    } else {
                        showToast("获取权限失败");
                    }
                }, Throwable::printStackTrace);
    }

    void startGetContent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult( Intent.createChooser(intent, "选择上传文件"), XGFJ_REQUEST_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            showToast("Please install a File Manager.");
        }
    }

    @OnActivityResult(XGFJ_REQUEST_CODE)
    void xgfjOnResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String path = UriUtil.getPath(this, uri);
            if (TextUtils.isEmpty(path)) return;
            xgfj.setText(FileUtil.getFileName(path));
            xgfj.setTag(path);
        }
    }

    @OnActivityResult(WTR_REQUEST_CODE)
    void selectWtrResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Custom custom = (Custom) data.getSerializableExtra("customItem");
            khmc.setText(custom.Title);
            khmc.setTag(custom);
        }
    }

    @Click(android.R.id.button1)
    void button1OnClick(View view) {
        validator.validate();
    }

    private void doCommit() {
        String xgfjPath = (String) xgfj.getTag();
        AppRequest.Build build = new AppRequest.Build("api/JobLog/Add")
                .addParam("Jid", joblogItem == null?"0":joblogItem.ID + "")
                .addParam("V1", ((JobLogCols)rznx.getTag()).ID + "")    // 一级类别
                .addParam("V2", ((JobLogCols)rznb.getTag()).ID + "")    // 二级类别
                .addParam("CaseId", xgaj.getTag() == null?"":((Case)xgaj.getTag()).ID + "")          // 案件ID
                .addParam("CustId", khmc.getTag() == null?"":((Custom)khmc.getTag()).ID + "")      // 客户名称
                .addParam("BegTime", kssj.getText().toString())        // 开始时间
                .addParam("EndTime", jssj.getText().toString())        // 结束时间
                .addParam("Des", gzms.getText().toString());           // 工作描述
        if (joblogItem != null) build.addParam("ID", joblogItem.ID + "");
        if (!TextUtils.isEmpty(xgfjPath)) build.addFile(new FormUploadFile("file", FileUtil.getFileName(xgfjPath), xgfjPath));
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
                            JobLog tempObj = resp.getFirstObject(JobLog.class);

                            Intent data = new Intent();
                            data.putExtra("joblogItem", tempObj);
                            setResult(RESULT_OK, data);
                            finish();
                        }
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        hideLoading();
                    }
                })
                .execute();
    }

    private void loadColsList() {
        // 获取案件类型
        AppRequest request = new AppRequest.Build("api/JobLog/Cols_Get")
                .addParam("Fid", "-1")
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
                            allCols = resp.resultsToArray(JobLogCols.class);
                            rznxs = getJobLogCols(0);
                            if (joblogItem == null) {
                                setRznx(rznxs[0]);
                            } else {
                                for (JobLogCols nx : rznxs) {
                                    if (nx.ID == joblogItem.V1) {
                                        setRznx(nx);
                                        break;
                                    }
                                }
                                for (JobLogCols nb : rznbs) {
                                    if (nb.ID == joblogItem.V2) {
                                        rznb.setText(nb.toString());
                                        rznb.setTag(nb);
                                        break;
                                    }
                                }
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

    private JobLogCols[] getJobLogCols(int fid) {
        List<JobLogCols> list = new ArrayList<>();
        for (JobLogCols cols : allCols) {
            if (cols.Fid == fid) {
                list.add(cols);
            }
        }
        Collections.sort(list, (o1, o2) -> o1.Sort - o2.Sort);
        return list.toArray(new JobLogCols[0]);
    }


}
