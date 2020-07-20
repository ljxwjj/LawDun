package com.yunfa365.lawservice.app.ui.activity.office;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
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
import com.yunfa365.lawservice.app.pojo.GetWebColName;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;
import com.yunfa365.lawservice.app.ui.adapter.AyAdapter;
import com.yunfa365.lawservice.app.ui.dialog.MultiSelectDialog;
import com.yunfa365.lawservice.app.ui.dialog.SpinnerDialog;
import com.yunfa365.lawservice.app.utils.AppUtil;
import com.yunfa365.lawservice.app.utils.DateUtil;
import com.yunfa365.lawservice.app.utils.FileUtil;
import com.yunfa365.lawservice.app.utils.LocationUtil;
import com.yunfa365.lawservice.app.utils.ScreenUtil;
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
 *  案件登记-刑事案件
 */
@EActivity(R.layout.activity_office41_2)
public class Office41_2Activity extends BaseUserActivity {
    private static final int WTR_REQUEST_CODE = 1;

    private static final String[] zfbzs = {"是", "否"};
    private static final String[] ajxzs = {"公诉案件", "自诉案件"};
    private static final String[] ajlys = {"委托代理", "指定代理"};
    private static final String[] lssfs = {"辩护人", "自诉人代理人", "被害人代理人", "附带民事诉讼代理人"};

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

    @NotEmpty(message = "不能为空")
    @Order(6)
    @ViewById
    EditText dsr;

    @NotEmpty(message="不能为空")
    @Order(7)
    @ViewById
    EditText dlf;

    @NotEmpty(message="收费方式不能为空")
    @Order(8)
    @ViewById
    EditText sffs;

    @Order(9)
    @ViewById
    EditText fxsfsm;

    @ViewById
    View fxsfsm_layout;

    @ViewById
    View fxsfsm_line;

    @NotEmpty(message="政府补助不能为空")
    @Order(10)
    @ViewById
    EditText zfbz;

    @Order(11)
    @ViewById
    EditText bzje;  // 补助金额

    @NotEmpty(message="诉讼阶段不能为空")
    @Order(12)
    @ViewById
    EditText ssjd;

    @NotEmpty(message="诉讼地位不能为空")
    @Order(13)
    @ViewById
    EditText ssdw;

    @NotEmpty(message="案件性质不能为空")
    @Order(14)
    @ViewById
    EditText ajxz;

    @NotEmpty(message="案件来源不能为空")
    @Order(15)
    @ViewById
    EditText ajly;

    @NotEmpty(message="律师身份不能为空")
    @Order(16)
    @ViewById
    EditText lssf;

    @ViewById
    EditText ga;

    @ViewById
    EditText jcy;

    @ViewById
    EditText fy;

    @ViewById
    EditText kss;

    @ViewById
    EditText sheng;

    @ViewById
    EditText shi;

    @ViewById
    EditText zsah;

    @NotEmpty(message = "案源人不能为空")
    @Order(17)
    @ViewById
    EditText ayr;

    @ViewById
    EditText taf;

    @ViewById
    EditText disr;

    @NotEmpty(message = "相关文件不能为空")
    @Order(18)
    @ViewById
    TextView xgwj;

    @ViewById
    EditText ajbz;

    @ViewById
    TextView moreBtn;

    @ViewById
    View moreLayout;

    @Extra
    CaseCols selectedCaseCols;

    @Extra
    Case caseItem;

    private GetWebColName[] ssjds;
    private GetWebColName[] ssdws;

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
        initAyAutoComplete();
        loadDataSsjd();
        initValidate();
        loadRequiredFields();
        initDefaultValue();
    }

    private void initAyAutoComplete() {
        String[] autoString = FileUtil.getAssetsFileContents(getResources(), "ay.txt");
        ay.setAdapter(AyAdapter.createAyAdapter(this, R.layout.ay_dropdown_item_1line, autoString));
        ay.setDropDownVerticalOffset(ScreenUtil.dip2px(4));
    }

    private void initDefaultValue() {
        if (caseItem == null) {
            Mid = AppUtil.generateMid();
            sffs.setText(AppCst.sffss[1]);
            zfbz.setText(zfbzs[1]);

            ajxz.setText(ajxzs[0]);
            ajly.setText(ajlys[0]);
            lssf.setText(lssfs[0]);

            initLocation();
        } else {
            Mid = caseItem.ID;
            anh.setText(caseItem.CaseID);

            ay.setText(caseItem.AyMake);
            sarq.setText(caseItem.Begtime);
            wtr.setText(caseItem.TWtr);
            dsr.setText(caseItem.LxRen);
            Custom custom = new Custom();
            custom.ID = caseItem.Wtr;
            wtr.setTag(custom);

            dlf.setText(StringUtil.formatDouble(caseItem.Price));
            sffs.setText(caseItem.PayCols);
            fxsfsm.setText(caseItem.FengXianMake);
            zfbz.setText(caseItem.IsBuTie);
            bzje.setText(StringUtil.formatDouble(caseItem.BuTiePrice));

            ssjd.setText(caseItem.TSscx);
            String ssjdStr = caseItem.Sscx;
            String ssjdStrs[] = ssjdStr.split(",");
            GetWebColName[] ssjdObjs = new GetWebColName[ssjdStrs.length];
            try {
                for (int i = 0; i < ssjdStrs.length; i++) {
                    GetWebColName colObj = new GetWebColName();
                    int id = Integer.parseInt(ssjdStrs[i]);
                    colObj.ID = id;
                    ssjdObjs[i] = colObj;
                }
            } catch(Exception e) {
                e.printStackTrace();
                AppUtil.showToast(this, "数据解析错误");
            }
            ssjd.setTag(ssjdObjs);

            ssdw.setText(caseItem.TSsdw);
            GetWebColName colName = new GetWebColName();
            colName.ID = caseItem.Ssdw;
            ssdw.setTag(colName);

            ajxz.setText(caseItem.AjXz);
            ajly.setText(caseItem.AjLy);
            lssf.setText(caseItem.DLShenFen);
            ga.setText(caseItem.Slfy1);
            jcy.setText(caseItem.Slfy2);
            fy.setText(caseItem.Slfy3);
            kss.setText(caseItem.Slfy4);

            sheng.setText(caseItem.Province);
            shi.setText(caseItem.City);
            zsah.setText(caseItem.UserDefId);
            ayr.setText(caseItem.AnYuanRen);
            disr.setText(caseItem.DiSanRen);
            taf.setText(caseItem.TongAnFan);
            ajbz.setText(caseItem.Des);

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

    private void loadDataSsjd() {
        // 获取诉讼阶段 诉讼地位
        AppRequest request = new AppRequest.Build("Case/GetLawyerWebColList")
                .addParam("Fid", "2")
                .addParam("CaseCols", selectedCaseCols.Fid) // 案件类型
                .addParam("Ay", selectedCaseCols.ID)
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
                        AppResponse resp = (AppResponse)result.getAttach();
                        if (resp.flag) {
                            List<GetWebColName> ssjdList = resp.resultsToList(GetWebColName.class);
                            ssjds = ssjdList.toArray(new GetWebColName[ssjdList.size()]);
                            loadDataSsdw();
                        }
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        hideLoading();
                    }
                })
                .execute();
    }

    private void loadDataSsdw() {
        // 诉讼地位
        AppRequest request = new AppRequest.Build("Case/GetLawyerWebColList")
                .addParam("Fid", "3")
                .addParam("CaseCols", selectedCaseCols.Fid) // 案件类型
                .create();
        new HttpFormFuture.Builder(this)
                .setData(request)
                .setListener(new AgnettyFutureListener(){

                    @Override
                    public void onComplete(AgnettyResult result) {
                        hideLoading();
                        AppResponse resp = (AppResponse)result.getAttach();
                        if (resp.flag) {
                            List<GetWebColName> ssdwList = resp.resultsToList(GetWebColName.class);
                            ssdws = ssdwList.toArray(new GetWebColName[ssdwList.size()]);
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

    @Click(R.id.wtr)
    void wtrOnClick(View view) {
        Intent intent = new Intent(this, Office_searchCustomActivity_.class);
        startActivityForResult(intent, WTR_REQUEST_CODE);
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

    @Click(R.id.zfbz)
    void zfbzOnClick(View view) {
        new SpinnerDialog(this, "请选择", zfbzs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                zfbz.setText(zfbzs[which]);
            }
        }).show();
    }

    @Click(R.id.ssjd)
    void ssjdOnClick(View view) {
        if (ssjds == null)
            return;

        boolean[] checkItems = new boolean[ssjds.length];
        GetWebColName[] selected = (GetWebColName[]) ssjd.getTag();

        if (selected != null) {
            for (int i = 0; i < ssjds.length; i++) {
                for (int j = 0; j < selected.length; j++) {
                    if (selected[j].ID == ssjds[i].ID) {
                        checkItems[i] = true;
                        break;
                    }
                }
            }
        }

        MultiSelectDialog<GetWebColName> msDialog = new MultiSelectDialog(this, "请选择诉讼阶段", ssjds, checkItems, new MultiSelectDialog.MultiSelectListener() {
            @Override
            public void onItemClick(MultiSelectDialog dialog, int position, boolean checked) {

            }

            @Override
            public void onOkClick(MultiSelectDialog dialog) {
                GetWebColName[] selected = (GetWebColName[]) dialog.getSelectedItem();
                ssjd.setTag(selected);
                String[] strs = new String[selected.length];
                for (int i = 0; i < selected.length; i++)
                    strs[i] = selected[i].toString();
                ssjd.setText(StringUtil.implode(strs, " "));
            }

            @Override
            public void onCancelClick(MultiSelectDialog dialog) {

            }
        });
        msDialog.show();
    }

    @Click(R.id.ssdw)
    void ssdwOnClick(View view) {
        if (ssdws == null)
            return;
        new SpinnerDialog(this, "选择诉讼地位", ssdws, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GetWebColName gwcn = ssdws[which];
                ssdw.setText(gwcn.Title);
                ssdw.setTag(gwcn);
            }
        }).show();
    }

    @Click(R.id.ajxz)
    void ajxzOnClick(View view) {
        new SpinnerDialog(this, "选择案件性质", ajxzs, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ajxz.setText(ajxzs[which]);
            }
        }).show();
    }

    @Click(R.id.ajly)
    void ajlyOnClick(View view) {
        new SpinnerDialog(this, "选择案件来源", ajlys, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ajly.setText(ajlys[which]);
            }
        }).show();
    }

    @Click(R.id.lssf)
    void lssfOnClick(View view) {
        new SpinnerDialog(this, "选择律师身份", lssfs, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                lssf.setText(lssfs[which]);
            }
        }).show();
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

    @Click(android.R.id.button1)
    void button1OnClick(View view) {
        validator.validate();
    }

    private void showMoreLayout() {
        moreLayout.setVisibility(View.VISIBLE);
        moreBtn.setText("收起");
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

    private void doCommit() {
        GetWebColName[] selected = (GetWebColName[]) ssjd.getTag();
        String ids[] = new String[selected.length];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = selected[i].ID + "";
        }
        String ssjdStr = StringUtil.implode(ids, ",");

        AppRequest.Build build = new AppRequest.Build("Case/UpdateCaseXS")
                .addParam("Cols", selectedCaseCols.Fid)
                .addParam("Ay", selectedCaseCols.ID)
                .addParam("AyMake", ay.getText().toString())        //案由
                .addParam("BegTime", sarq.getText().toString())     // 收案日期
                .addParam("Wtr", ((Custom)wtr.getTag()).ID + "")    //委托人
                .addParam("LxRen", dsr.getText().toString())        //嫌疑人
                .addParam("Price", dlf.getText().toString())        //代理费
                .addParam("PayCols", sffs.getText().toString())     //收费方式
                .addParam("FengXianMake", fxsfsm.getText().toString())
                .addParam("IsBuTie", zfbz.getText().toString())     //政府补贴
                .addParam("BuTiePrice", bzje.getText().toString())  //补助金额
                .addParam("Sscx", ssjdStr)                          //诉讼阶段
                .addParam("Ssdw", ((GetWebColName)ssdw.getTag()).ID + "")       //诉讼地位
                .addParam("CaseType", ajxz.getText().toString())                //案件性质
                .addParam("CaseFrom", ajly.getText().toString())    //案件来源
                .addParam("LsCols", lssf.getText().toString())      //代理律师身份
                .addParam("Slfy1", ga.getText().toString())        //公安
                .addParam("Slfy2", jcy.getText().toString())       //检察院
                .addParam("Slfy3", fy.getText().toString())        //法院
                .addParam("Slfy4", kss.getText().toString())       //看守所
                .addParam("Province", sheng.getText().toString())   //省
                .addParam("City", shi.getText().toString())         //市
                .addParam("DiSanRen", disr.getText().toString())    //第三人
                .addParam("UserDefId", zsah.getText().toString())   //专属案号
                .addParam("AnYuanRen", ayr.getText().toString())    // 案源人
                .addParam("TongAnFan", taf.getText().toString())    //同案犯
                .addParam("Des", ajbz.getText().toString())         //备注说明
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
                            AppUtil.showToast(Office41_2Activity.this, resp.Message);
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
                                dsr.setHint("请填写嫌疑人(选填)");
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
