package com.yunfa365.lawservice.app.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyResult;
import com.android.agnetty.utils.LogUtil;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Password;
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
import com.yunfa365.lawservice.app.utils.SpUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;

import java.util.List;

@EActivity(R.layout.activity_login)
class LoginActivity extends BaseActivity {

    @Phone(message = "手机号格式错误")
    @Order(1)
    @ViewById
    EditText inputPhone;

    @Password(message = "密码格式错误")
    @Order(2)
    @ViewById
    EditText inputPassword;

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

    private void doCommit() {
        String phone = inputPhone.getText().toString();
        String password = inputPassword.getText().toString();
        SpUtil.setUsername(LoginActivity.this, phone);

        AppRequest request = new AppRequest.Build("api/Users/Users_Login")
                .addParam("Mobile", phone)
                .addParam("UsersPwd", password)
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
