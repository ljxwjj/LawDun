package com.yunfa365.lawservice.app.ui.activity;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.agnetty.core.AgnettyException;
import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyResult;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.base.BaseActivity;
import com.yunfa365.lawservice.app.utils.AppUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EActivity(R.layout.activity_register)
class RegisterActivity extends BaseActivity {

    @ViewById(R.id.base_id_back)
    View mBackView;

    @ViewById(R.id.base_id_title)
    TextView mTitleTxt;

    @ViewById(R.id.base_right_btn)
    ImageView mRightImage;

    @ViewById(R.id.base_right_txt)
    TextView mRightTxt;

    @Order(1)
    @NotEmpty(message = "不能为空")
    @ViewById
    EditText LawCodeNums;

    @Order(2)
    @NotEmpty(message = "不能为空")
    @ViewById
    EditText Mobile;

    @Order(3)
    @NotEmpty(message = "不能为空")
    @ViewById
    EditText UPwd;

    @Order(4)
    @NotEmpty(message = "不能为空")
    @ViewById
    EditText FullName;

    @ViewById
    RadioGroup Sexs;

    @ViewById
    EditText Phone;

    @ViewById
    EditText UNums;

    @ViewById
    EditText UserCode;

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
        mTitleTxt.setText("用户注册");

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

    @Click(R.id.submitBtn)
    void submitBtnOnClick() {
        validator.validate();
    }

    private void doCommit() {
        int checkedId = Sexs.getCheckedRadioButtonId();
        RadioButton checkedButton = Sexs.findViewById(checkedId);
        AppRequest request = new AppRequest.Build("api/Users/Users_Region")
                .addParam("LawCodeNums", LawCodeNums.getText().toString())
                .addParam("Mobile", Mobile.getText().toString())
                .addParam("UPwd", UPwd.getText().toString())
                .addParam("FullName", FullName.getText().toString())
                .addParam("Sexs", checkedButton.getTag().toString())
                .addParam("Phone", Phone.getText().toString())
                .addParam("UNums", UNums.getText().toString())
                .addParam("UserCode", UserCode.getText().toString())
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
                            finish();
                            showToast("注册成功");
                        } else if ("1".equals(resp.Code)) {
                            showToast(resp.Message);
                        } else {
                            showToast("系统错误");
                        }
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        hideLoading();
                        Exception e = result.getException();
                        if (e != null && e instanceof AgnettyException) {
                            showToast("请检查网络");
                        } else {
                            showToast("未知道错误");
                        }
                    }
                })
                .execute();
    }
}
