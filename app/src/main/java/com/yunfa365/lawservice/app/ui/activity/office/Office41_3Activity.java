package com.yunfa365.lawservice.app.ui.activity.office;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.mobsandgeeks.saripaar.QuickRule;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.constant.AppCst;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.Case;
import com.yunfa365.lawservice.app.pojo.CaseCols;
import com.yunfa365.lawservice.app.pojo.Custom;
import com.yunfa365.lawservice.app.pojo.DiQu;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;
import com.yunfa365.lawservice.app.ui.dialog.SpinnerDialog;
import com.yunfa365.lawservice.app.utils.AppUtil;
import com.yunfa365.lawservice.app.utils.DateUtil;
import com.yunfa365.lawservice.app.utils.FileUtil;
import com.yunfa365.lawservice.app.utils.LocationUtil;
import com.yunfa365.lawservice.app.utils.StringUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by Administrator on 2016/4/20.
 * 案件登记-法律顾问
 */
@EActivity(R.layout.activity_office41_3)
public class Office41_3Activity extends BaseUserActivity {
    private static final int WTR_REQUEST_CODE = 1;
    private static final String[] zfbzs = {"是", "否"};

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
    TextView anh;

    // sequence 字段内规则执行顺序
    @NotEmpty(message="收案日期不能为空", sequence = 1)
    @Order(3)
    @ViewById
    EditText sarq;

    @NotEmpty(message="委托人不能为空")
    @Order(4)
    @ViewById
    EditText wtr;

    @NotEmpty(message = "当事人不能为空")
    @Order(5)
    @ViewById
    EditText dsr;

    @NotEmpty(message="不能为空")
    @Order(6)
    @ViewById
    EditText gwf;

    @NotEmpty(message="收费方式不能为空")
    @Order(7)
    @ViewById
    EditText sffs;

    @Order(8)
    @ViewById
    EditText fxsfsm;

    @ViewById
    View fxsfsm_layout;

    @ViewById
    View fxsfsm_line;

    @ViewById
    EditText sheng;

    @ViewById
    EditText shi;

    @NotEmpty(message = "开始日期不能为空")
    @Order(9)
    @ViewById
    EditText qsrq;

    @NotEmpty(message = "截止日期不能为空")
    @Order(10)
    @ViewById
    EditText jzrq;

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

    private DiQu[] shengs;
    private DiQu[] shis;
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

        String diquStr = FileUtil.getRawFileContent(getResources(), R.raw.diqu);
        shengs = StringUtil.toObjectArray(diquStr, DiQu.class);
        //loadDataSsjd();
        initValidate();
        loadRequiredFields();
        initDefaultValue();
    }

    private void initDefaultValue() {
        if (caseItem == null) {
            Mid = AppUtil.generateMid();
            sffs.setText(AppCst.sffss[1]);

            initLocation();
        } else {
            Mid = caseItem.ID;
            anh.setText(caseItem.CaseID);

            sarq.setText(caseItem.Begtime);
            wtr.setText(caseItem.TWtr);
            dsr.setText(caseItem.LxRen);
            Custom custom = new Custom();
            custom.ID = caseItem.CustId;
            wtr.setTag(custom);

            gwf.setText(caseItem.Price + "");
            sffs.setText(caseItem.PayCols);
            fxsfsm.setText(caseItem.FengXianMake);

            sheng.setText(caseItem.Province);
            shi.setText(caseItem.City);
            qsrq.setText(caseItem.CaseTime1);
            jzrq.setText(caseItem.CaseTime2);
            zsah.setText(caseItem.UserDefId);
            ayr.setText(caseItem.AnYuanRen);
            disr.setText(caseItem.DiSanRen);
            bzsm.setText(caseItem.Des);

            if (!TextUtils.isEmpty(caseItem.Province)) {
                for (DiQu sheng : shengs) {
                    if (sheng.text.equals(caseItem.Province)) {
                        shis = sheng.children;
                        break;
                    }
                }
            }
        }
    }

    private LocationUtil.OnGetLocationListener locationListener = new LocationUtil.OnGetLocationListener() {
        @Override
        public void onGetLocation(String province, String city, String district) {
            if (isFinishing()) return;

            if (TextUtils.isEmpty(sheng.getText())) {
                for (DiQu s : shengs) {
                    if (s.text.equals(province)) {
                        sheng.setText(s.text);
                        sheng.setTag(s);
                        setShis(s.children);
                        break;
                    }
                }
                if (shis == null) return;
                for (DiQu s : shis) {
                    if (s.text.equals(city)) {
                        shi.setText(s.text);
                        shi.setTag(s);
                        break;
                    }
                }
            }
        }
    };

    private void initLocation() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        LocationUtil.getInstance(this).startLocation(locationListener);
                    } else {
                        showToast("获取权限失败");
                    }
                }, Throwable::printStackTrace);
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
        validator.put(fxsfsm, new QuickRule<EditText>(){
            private String message;
            @Override
            public boolean isValid(EditText view) {
                String sf = sffs.getText().toString();
                if (AppCst.sffss[4].equals(sf)) {
                    String sm = view.getText().toString();
                    if (TextUtils.isEmpty(sm)) {
                        message = "不能为空";
                        return false;
                    }
                }
                return true;
            }

            @Override
            public String getMessage(Context context) {
                return message;
            }
        });
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
                sffs.setText(AppCst.sffss[which]);
            }
        }).show();
    }

    @TextChange(R.id.sffs)
    void sffsTextChange(TextView tv, CharSequence text) {
        if (AppCst.sffss[4].equals(text.toString())) {
            fxsfsm_layout.setVisibility(View.VISIBLE);
            fxsfsm_line.setVisibility(View.VISIBLE);
        } else {
            fxsfsm_layout.setVisibility(View.GONE);
            fxsfsm_line.setVisibility(View.GONE);

        }
    }

    @Click(R.id.wtr)
    void wtrOnClick(View view) {
        Intent intent = new Intent(this, Office_searchCustomActivity_.class);
        startActivityForResult(intent, WTR_REQUEST_CODE);
    }

    @Click(R.id.sheng)
    void shengOnClick(View view) {
        if (shengs == null)
            return;
        new SpinnerDialog(this, "请选择省/直辖市", shengs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sheng.setTag(shengs[which]);
                sheng.setText(shengs[which].toString());
                setShis(shengs[which].children);
            }
        }).show();
    }

    @Click(R.id.shi)
    void shiOnClick(View view) {
        if (shis == null)
            return;
        new SpinnerDialog(this, "请选择地级市", shis, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                shi.setTag(shis[which]);
                shi.setText(shis[which].toString());
            }
        }).show();
    }

    private void setShis(DiQu[] diqus) {
        shis = diqus;
        shi.setText(shis[0].toString());
        shi.setTag(shis[0]);
    }

    @Click(R.id.qsrq)
    void qsrqOnClick(View view) {
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
                qsrq.setText(rq);
            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Click(R.id.jzrq)
    void jzrqOnClick(View view) {
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
                jzrq.setText(rq);
            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Click(android.R.id.button1)
    void button1OnClick(View view) {
        validator.validate();
    }

    private void doCommit() {
        AppRequest.Build build = new AppRequest.Build("Case/UpdateCaseFLGW")
                .addParam("Cols", selectedCaseCols.Fid + "")
                .addParam("Ay", selectedCaseCols.ID + "")
                .addParam("BegTime", sarq.getText().toString())    // 收案日期
                .addParam("Wtr", ((Custom)wtr.getTag()).ID + "")   // 委托人
                .addParam("LxRen", dsr.getText().toString())        //当事人
                .addParam("Price", gwf.getText().toString())       // 顾问费
                .addParam("PayCols", sffs.getText().toString())    // 收费方式
                .addParam("FengXianMake", fxsfsm.getText().toString())
                .addParam("Province", sheng.getText().toString())  // 省
                .addParam("City", shi.getText().toString())        // 市
                .addParam("CaseTime1", qsrq.getText().toString())  // 起始日期
                .addParam("CaseTime2", jzrq.getText().toString())  // 截止日期
                .addParam("DiSanRen", disr.getText().toString())    //第三人
                .addParam("UserDefId", zsah.getText().toString())   //专属案号
                .addParam("AnYuanRen", ayr.getText().toString())    // 案源人
                .addParam("Des", bzsm.getText().toString())         // 备注说明
                .addParam("Mid", Mid);

        if (caseItem != null) {
            build.addParam("CaseId", caseItem.ID);
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
                        if (resp.flag) {
                            String ajID = resp.RData;
                            LogUtil.d("案件添加成功，ID：" + ajID);
                            setResult(RESULT_OK);
                            finish();
                            LogUtil.d("案件修改保存成功");

                        } else {
                            AppUtil.showToast(Office41_3Activity.this, resp.Message);
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

    private void showMoreLayout() {
        moreLayout.setVisibility(View.VISIBLE);
        moreBtn.setText("收起");
    }

    @OnActivityResult(WTR_REQUEST_CODE)
    void selectWtrResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Custom custom = (Custom) data.getSerializableExtra("customItem");
            wtr.setTag(custom);
            wtr.setText(custom.Title);
        }
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
                            if (!requiredFields.contains("LxRen")) {
                                validator.removeRules(dsr);
                                dsr.setHint("请填写当事人(选填)");
                            }
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

}
