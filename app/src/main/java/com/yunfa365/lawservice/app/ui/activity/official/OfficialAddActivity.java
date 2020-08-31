package com.yunfa365.lawservice.app.ui.activity.official;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyResult;
import com.android.agnetty.future.upload.form.FormUploadFile;
import com.android.agnetty.utils.LogUtil;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.Case;
import com.yunfa365.lawservice.app.pojo.CusTomCols;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;
import com.yunfa365.lawservice.app.ui.activity.law_case.SelectCaseActivity_;
import com.yunfa365.lawservice.app.ui.dialog.SpinnerDialog;
import com.yunfa365.lawservice.app.utils.AppUtil;
import com.yunfa365.lawservice.app.utils.FileUtil;
import com.yunfa365.lawservice.app.utils.UriUtil;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EActivity(R.layout.activity_official_add)
public class OfficialAddActivity extends BaseUserActivity {
    private static final int SELECT_FILE_REQUEST_CODE = 1;
    private static final int SELECT_CASE_REQUEST_CODE = 2;

    @ViewById(R.id.base_id_back)
    View mBackView;

    @ViewById(R.id.base_id_title)
    TextView mTitleTxt;

    @ViewById(R.id.base_right_btn)
    ImageView mRightImage;

    @ViewById(R.id.base_right_txt)
    TextView mRightTxt;

    @NotEmpty(message = "用印类型不能为空")
    @Order(1)
    @ViewById
    EditText yylx;

    @NotEmpty(message = "不能为空")
    @Order(2)
    @ViewById
    EditText yymc;

    @NotEmpty(message = "案件不能为空")
    @Order(3)
    @ViewById
    EditText xgaj;

    @NotEmpty(message = "盖章文件不能为空")
    @Order(4)
    @ViewById
    EditText gzwj;

    @NotEmpty(message = "盖章次数不能为空")
    @Order(5)
    @ViewById
    EditText gzcs;

    @ViewById
    EditText sqsy;

    private CusTomCols[] yylxs;
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
        mTitleTxt.setText("用印申请");
        loadCustomType();
        initValidate();
    }

    private void loadCustomType() {
        // 获取诉讼阶段 诉讼地位
        AppRequest request = new AppRequest.Build("api/official/Cols_Get")
                .create();
        new HttpFormFuture.Builder(this)
                .setData(request)
                .setListener(new AgnettyFutureListener() {
                    @Override
                    public void onStart(AgnettyResult result) {
                        showLoading();
                    }

                    @Override
                    public void onComplete(AgnettyResult result) {
                        hideLoading();
                        AppResponse resp = (AppResponse) result.getAttach();
                        if (resp.flag) {
                            yylxs = resp.resultsToArray(CusTomCols.class);
                        }
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        hideLoading();
                    }
                })
                .execute();
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
                        showToast(message);
                    }
                }
            }
        });
    }

    @Click(R.id.yylx)
    void yylxOnClick(View view) {
        new SpinnerDialog(this, "请选择", yylxs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                yylx.setTag(yylxs[which]);
                yylx.setText(yylxs[which].toString());
            }
        }).show();
    }

    @Click(R.id.xgaj)
    void xgajOnClick() {
        SelectCaseActivity_.intent(this).startForResult(SELECT_CASE_REQUEST_CODE);
    }

    @Click(R.id.gzwj)
    void gzwjOnClick() {
        Matisse.from(this)
                .choose(MimeType.ofAll())
                .countable(true)
                .maxSelectable(1)
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .showPreview(false) // Default is `true`
                .forResult(SELECT_FILE_REQUEST_CODE);
    }

    @Click(R.id.submitBtn)
    void submitBtnOnClick(View view) {
        validator.validate();
    }

    private void doCommit() {
        LogUtil.d("+++++++++++doCommit+++");

        FormUploadFile file = (FormUploadFile) gzwj.getTag();
        AppRequest.Build build = new AppRequest.Build("api/official/Add")
                .addParam("CaseId", ((Case)xgaj.getTag()).ID + "")
                .addParam("SCols", ((CusTomCols)yylx.getTag()).ID + "")
                .addParam("Title", yymc.getText().toString())
                .addParam("Make", sqsy.getText().toString())
                .addParam("ZNums", gzcs.getText().toString());
        if (file != null) build.addFile(file);

        AppRequest request = build.create();
        new HttpFormFuture.Builder(this)
                .setData(request)
                .setListener(new AgnettyFutureListener() {
                    @Override
                    public void onStart(AgnettyResult result) {
                        showLoading();
                    }

                    @Override
                    public void onComplete(AgnettyResult result) {
                        hideLoading();
                        AppResponse resp = (AppResponse) result.getAttach();
                        if (resp.flag) {
                            showToast(resp.Message);
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

    @OnActivityResult(SELECT_FILE_REQUEST_CODE)
    void xgfjOnResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            List<Uri> selected = Matisse.obtainResult(data);
            Uri uri = selected.get(0);
            String path = UriUtil.getPath(this, uri);
            String fileName = "";
            FormUploadFile file = null;
            if (TextUtils.isEmpty(path)) {
                fileName = UriUtil.getFileName(this, uri);
                byte[] content = UriUtil.getContent(this, uri);
                if (fileName != null && content != null) {
                    file = new FormUploadFile("file", fileName, content);
                } else {
                    LogUtil.e("文件读取失败");
                }
            } else {
                fileName = FileUtil.getFileName(path);
                file = new FormUploadFile("file", FileUtil.getFileName(path), path);
            }
            gzwj.setText(fileName);
            gzwj.setTag(file);
        }
    }

    @OnActivityResult(SELECT_CASE_REQUEST_CODE)
    void xgajOnResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Case caseItem = (Case) data.getSerializableExtra("caseItem");
            xgaj.setText(caseItem.CaseIdTxt);
            xgaj.setTag(caseItem);
        }
    }

}
