package com.yunfa365.lawservice.app.ui.activity.office;

import android.app.DatePickerDialog;
import android.content.Context;
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
import com.mobsandgeeks.saripaar.QuickRule;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Optional;
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
 * 案件登记-法律顾问
 */
@EActivity(R.layout.activity_office41_3)
public class Office41_3Activity extends BaseUserActivity {

    private static final int WTR_REQUEST_CODE = 1;
    private static final int ZYRY_REQUEST_CODE = 2;
    private static final BaseBean[] zfbzs = {new BaseBean(0, "不享受"), new BaseBean(1, "享受")};
    private static final BaseBean[] sfzps = {new BaseBean(0, "否"), new BaseBean(1, "是")};
    private static final BaseBean[] kpzts = {new BaseBean(1, "开票"), new BaseBean(0, "预收")};

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

    @NotEmpty(message = "执业人员不能为空")
    @Order(1)
    @ViewById
    EditText zyry;

    // sequence 字段内规则执行顺序
    @NotEmpty(message="收案日期不能为空", sequence = 1)
    @Order(3)
    @ViewById
    EditText sarq;

    @NotEmpty(message="委托人不能为空")
    @Order(4)
    @ViewById
    EditText wtr;

    @NotEmpty(message="收费方式不能为空")
    @Order(7)
    @ViewById
    EditText sffs;

    @NotEmpty(message="不能为空")
    @Order(6)
    @ViewById
    EditText gwf;

    @NotEmpty(message = "开始日期不能为空")
    @Order(9)
    @ViewById
    EditText qsrq;

    @NotEmpty(message = "截止日期不能为空")
    @Order(10)
    @ViewById
    EditText jzrq;

    @ViewById
    EditText bzsm;



    @ViewById
    TextView kpzt;   // 开票状态

    @ViewById
    TextView sfzp;   // 是否指派

    @ViewById
    EditText jzf;    // 交杂费

    @Order(12)
    @ViewById
    EditText fxsfsm;

    @ViewById
    EditText zfbz;

    @Optional
    //@Min(value = 0, message = "必须为数字")
    @Order(14)
    @ViewById
    EditText bzje;  // 补助金额


    @ViewById
    TextView moreBtn;

    @ViewById
    View moreLayout;

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
        mTitleTxt.setText("案件登记");

        if (caseItem != null) {
            selectedCaseCols = new CaseCols();
            selectedCaseCols.Fid = caseItem.ColsV1;
            selectedCaseCols.ID = caseItem.ColsV2;
        }
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
            sffs.setText(AppCst.sffss[1].toString());
            sffs.setTag(AppCst.sffss[1]);
            kpzt.setText(kpzts[0].toString());
            kpzt.setTag(kpzts[0]);
            zfbz.setText(zfbzs[0].toString());
            zfbz.setTag(zfbzs[0]);
            sfzp.setText(sfzps[0].toString());
            sfzp.setTag(sfzps[0]);
        } else {
            anh.setText(caseItem.CaseIdTxt);
            zyry.setText(caseItem.UsersListTxt);
            sarq.setText(caseItem.BegTime);
            wtr.setText(caseItem.CustIdTxt);
            Custom custom = new Custom();
            custom.ID = caseItem.CustId;
            wtr.setTag(custom);
            sffs.setText(caseItem.PayColsTxt);
            sffs.setTag(new BaseBean(caseItem.PayCols, null));
            gwf.setText(caseItem.CasePrice + "");
            qsrq.setText(caseItem.CaseTime1);
            jzrq.setText(caseItem.CaseTime2);
            bzsm.setText(caseItem.Des);

            for (BaseBean item : kpzts) {
                if (item.ID == caseItem.BillStat) {
                    kpzt.setText(item.toString());
                    kpzt.setTag(item);
                }
            }
            for (BaseBean item : sfzps) {
                if (item.ID == caseItem.IsFrom) {
                    sfzp.setText(item.toString());
                    sfzp.setTag(item);
                }
            }
            jzf.setText(caseItem.ZPrice + "");
            fxsfsm.setText(caseItem.FengXianMake);
            for (BaseBean item : zfbzs) {
                if (item.ID == caseItem.IsBuTie) {
                    zfbz.setText(item.toString());
                    zfbz.setTag(item);
                }
            }
            bzje.setText(caseItem.BuTiePrice + "");
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

    @Click(R.id.kpzt)
    void kpztOnClick() {
        new SpinnerDialog(this, "请选择", kpzts, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BaseBean selected = kpzts[which];
                kpzt.setText(selected.Title);
                kpzt.setTag(selected);
            }
        }).show();
    }

    @Click(R.id.sfzp)
    void sfzpOnClick(View view) {
        if (sfzps == null) return;
        new SpinnerDialog(this, "请选择", sfzps, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BaseBean selected = sfzps[which];
                sfzp.setText(selected.Title);
                sfzp.setTag(selected);
            }
        }).show();
    }

    @Click(R.id.zfbz)
    void zfbzOnClick(View view) {
        new SpinnerDialog(this, "请选择", zfbzs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                zfbz.setText(zfbzs[which].toString());
                zfbz.setTag(zfbzs[which]);
            }
        }).show();
    }

    @Click(R.id.wtr)
    void wtrOnClick(View view) {
        Intent intent = new Intent(this, Office_searchCustomActivity_.class);
        startActivityForResult(intent, WTR_REQUEST_CODE);
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
        AppRequest.Build build = new AppRequest.Build("api/Case/Add_GW")
                .addParam("CaseId", caseItem == null?"0":caseItem.ID + "")
                .addParam("ColsV1", selectedCaseCols.Fid + "")
                .addParam("ColsV2", selectedCaseCols.ID + "")
                .addParam("UsersList", zyry.getText().toString())
                .addParam("BegTime", sarq.getText().toString())    // 收案日期
                .addParam("CustName", wtr.getText().toString())   // 委托人
                .addParam("CaseTime1", qsrq.getText().toString())  // 起始日期
                .addParam("CaseTime2", jzrq.getText().toString())  // 截止日期
                .addParam("PayCols", ((BaseBean)sffs.getTag()).ID + "")    // 收费方式
                .addParam("FengXianMake", fxsfsm.getText().toString())
                .addParam("CasePrice", gwf.getText().toString())       // 顾问费
                .addParam("Des", bzsm.getText().toString())         // 备注说明
                .addParam("IsFrom", ((BaseBean)sfzp.getTag()).ID + "")
                .addParam("ZPrice", jzf.getText().toString())
                .addParam("IsBuTie", ((BaseBean)zfbz.getTag()).ID + "")
                .addParam("BuTiePrice", bzje.getText().toString())
                .addParam("BillStat", ((BaseBean)kpzt.getTag()).ID + "")
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
