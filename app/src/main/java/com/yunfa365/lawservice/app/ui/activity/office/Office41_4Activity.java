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
import android.widget.AutoCompleteTextView;
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
import com.mobsandgeeks.saripaar.annotation.Digits;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Optional;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.constant.AppCst;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.Case;
import com.yunfa365.lawservice.app.pojo.CaseCols;
import com.yunfa365.lawservice.app.pojo.Custom;
import com.yunfa365.lawservice.app.pojo.base.BaseBean;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;
import com.yunfa365.lawservice.app.ui.activity.custom.Office_searchCustomActivity_;
import com.yunfa365.lawservice.app.ui.adapter.AyAdapter;
import com.yunfa365.lawservice.app.ui.dialog.SpinnerDialog;
import com.yunfa365.lawservice.app.utils.AppUtil;
import com.yunfa365.lawservice.app.utils.DateUtil;
import com.yunfa365.lawservice.app.utils.FileUtil;
import com.yunfa365.lawservice.app.utils.ScreenUtil;
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
 * 案件登记-非诉案件
 */
@EActivity(R.layout.activity_office41_4)
public class Office41_4Activity extends BaseUserActivity {

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

    @NotEmpty(message="不能为空")
    @Order(4)
    @ViewById
    AutoCompleteTextView ay;

    @NotEmpty(message="委托人不能为空")
    @Order(5)
    @ViewById
    EditText wtr;

    @NotEmpty(message="不能为空")
    @Order(7)
    @ViewById
    EditText dfdsr;

    @Optional
    @Digits(integer = 10, message="诉讼标的必须为数字")
    @Order(8)
    @ViewById
    EditText ssbd;

    @NotEmpty(message="收费方式不能为空")
    @Order(10)
    @ViewById
    EditText sffs;

    @NotEmpty(message="不能为空")
    @Order(9)
    @ViewById
    EditText dlf;

    @ViewById
    EditText slbm;

    @ViewById
    EditText ajbz;



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

        initRadStarLabel(rootLayout);
        initAyAutoComplete();
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

    private void initAyAutoComplete() {
        String[] autoString = FileUtil.getAssetsFileContents(getResources(), "ay.txt");
        ay.setAdapter(AyAdapter.createAyAdapter(this, R.layout.ay_dropdown_item_1line, autoString));
        ay.setDropDownVerticalOffset(ScreenUtil.dip2px(4));
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
            anh.setText(caseItem.CaseID);

            sarq.setText(caseItem.Begtime);
            ay.setText(caseItem.AyMake);
            wtr.setText(caseItem.TWtr);
            Custom custom = new Custom();
            custom.ID = caseItem.CustId;
            wtr.setTag(custom);

            dfdsr.setText(caseItem.TDfdsr);
            ssbd.setText(caseItem.Ssbd);
            dlf.setText(caseItem.Price + "");
            sffs.setText(caseItem.PayCols);
            fxsfsm.setText(caseItem.FengXianMake);
            zfbz.setText(caseItem.IsBuTie);
            bzje.setText(caseItem.BuTiePrice + "");
            ajbz.setText(caseItem.Des);
        }
    }

    private void initValidate() {
        validator = new Validator(this);
        validator.put(bzje, new QuickRule<EditText>(){
            private String message;
            @Override
            public boolean isValid(EditText view) {
                String hasBz = zfbz.getText().toString();
                if (zfbzs[1].equals(hasBz)) {
                    return true;
                }
                String je = view.getText().toString();
                if (TextUtils.isEmpty(je)) {
                    message = "不能为空";
                    return false;
                } else {
                    try {
                        double i = Double.valueOf(je);
                    } catch (Exception e) {
                        message = "必须为数字";
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

    @Click(android.R.id.button1)
    void button1OnClick(View view) {
        validator.validate();
    }

    private void doCommit() {
        AppRequest.Build build = new AppRequest.Build("Case/UpdateCaseFSS")
                .addParam("CaseId", caseItem == null?"0":caseItem.ID + "")
                .addParam("ColsV1", selectedCaseCols.Fid + "")
                .addParam("ColsV2", selectedCaseCols.ID + "")
                .addParam("BegTime", sarq.getText().toString())         // 收案日期
                .addParam("AyMake", ay.getText().toString())            // 案由
                .addParam("Wtr", ((Custom)wtr.getTag()).ID + "")        // 委托人
                .addParam("TDfdsr", dfdsr.getText().toString())         // 对方当事人
                .addParam("Ssbd", ssbd.getText().toString())            // 标的
                .addParam("PayCols", sffs.getText().toString())         // 收费方式
                .addParam("Price", dlf.getText().toString())            // 代理费
                .addParam("Slfy", slbm.getText().toString())
                .addParam("Des", ajbz.getText().toString())             // 备注说明
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
                            AppUtil.showToast(Office41_4Activity.this, resp.Message);
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
            wtr.setTag(custom);
            wtr.setText(custom.Title);
        }
    }

}
