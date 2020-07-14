package com.yunfa365.lawservice.app.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyResult;
import com.android.agnetty.future.upload.form.FormUploadFile;
import com.android.agnetty.utils.LogUtil;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.constant.AppCst;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.future.HttpJsonFuture;
import com.yunfa365.lawservice.app.pojo.AppGlobal;
import com.yunfa365.lawservice.app.pojo.User;
import com.yunfa365.lawservice.app.pojo.event.LoginEvent;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.base.BaseActivity;
import com.yunfa365.lawservice.app.ui.validation.Phone;
import com.yunfa365.lawservice.app.utils.AppUtil;
import com.yunfa365.lawservice.app.utils.FileUtil;
import com.yunfa365.lawservice.app.utils.SpUtil;
import com.yunfa365.lawservice.app.utils.UriUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;

import java.util.List;

@EActivity(R.layout.activity_login)
class LoginActivity extends BaseActivity {
    private final int REQUEST_CODE_CHOOSE = 2;

    @Phone(message = "手机号格式错误")
    @Order(1)
    @ViewById
    EditText inputPhone;

    @Password(message = "密码格式错误")
    @Order(2)
    @ViewById
    EditText inputPassword;

    @ViewById
    TextView inputFile;

    private Validator validator;

    @AfterViews
    void init(){
        initValidate();
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

                    if (view instanceof  EditText && view.isFocusable()) {
                        view.requestFocus();
                        ((EditText) view).setError(message);
                    } else {
                        AppUtil.showToast(getBaseContext(), message);
                    }
                }
            }
        });
    }

    @Click(R.id.submitBtn)
    void submitBtnOnClick() {
        validator.validate();
    }

    @Click(R.id.registerBtn)
    void registerBtnOnClick() {
        WebActivity_.intent(this).start();
    }

    @Click(R.id.inputFile)
    void inputFileOnClick() {
        requestSelectFile();
    }

    private void requestSelectFile() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        startGetContent();
                    } else {
                        AppUtil.showToast(LoginActivity.this, "获取权限失败");
                    }
                }, Throwable::printStackTrace);
    }

    private void startGetContent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult( Intent.createChooser(intent, "选择上传文件"), REQUEST_CODE_CHOOSE);
        } catch (android.content.ActivityNotFoundException ex) {
            AppUtil.showToast(this, "Please install a File Manager.");
        }
    }

    @OnActivityResult(REQUEST_CODE_CHOOSE)
    void photoPickerOnResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String path = UriUtil.getPath(this, uri);
            if (TextUtils.isEmpty(path)) {
                String fileName = UriUtil.getFileName(this, uri);
                byte[] content = UriUtil.getContent(this, uri);
                if (fileName != null && content != null) {
                    onFileSelected(fileName, content);
                } else {
                    LogUtil.e("文件读取失败");
                }
            } else {
                onFileSelected(path);
            }
        }
    }

    private void onFileSelected(String path) {
        FormUploadFile file = new FormUploadFile("file", FileUtil.getFileName(path), path);
        inputFile.setText(path);
        inputFile.setTag(file);
    }

    private void onFileSelected(String fileName, byte[] content) {
        FormUploadFile file = new FormUploadFile("file", fileName, content);
        inputFile.setText(fileName);
        inputFile.setTag(file);
    }

    private void doCommit() {
        String phone = inputPhone.getText().toString();
        String password = inputPassword.getText().toString();
        SpUtil.setUsername(LoginActivity.this, phone);
        FormUploadFile file = (FormUploadFile) inputFile.getTag();

        AppRequest.Build build = new AppRequest.Build("api/Users/Users_Login")
                .addParam("Mobile", phone)
                .addParam("UPwd", password);
        if (file != null) {
            build.addFile(file);
        }
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
                        if (resp.Status == AppCst.HTTP_CODE_SUCCESS) {
                            AppGlobal.mUser = resp.resultsToObject(User.class);
                            SpUtil.setCurrentUser(LoginActivity.this, resp.Results);

                            EventBus.getDefault().post(new LoginEvent());
                            LogUtil.d("login success----------");


                            HomeActivity_.intent(LoginActivity.this)
                                    .mDefaultTab(2)
                                    .start();

                            finish();
                        } else if (!TextUtils.isEmpty(resp.Message)) {
                            AppUtil.showToast(LoginActivity.this, resp.Message);
                        }
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        hideLoading();
                        showToast("网络异常");
                    }
                })
                .execute();
    }
}
