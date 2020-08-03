package com.yunfa365.lawservice.app.ui.activity.office;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
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
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.CaseFile;
import com.yunfa365.lawservice.app.pojo.CusTomCols;
import com.yunfa365.lawservice.app.pojo.Custom;
import com.yunfa365.lawservice.app.pojo.DiQu;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;
import com.yunfa365.lawservice.app.ui.validation.NotEmptyQuickRule;
import com.yunfa365.lawservice.app.ui.validation.OptionalEmail;
import com.yunfa365.lawservice.app.ui.validation.OptionalIdCard;
import com.yunfa365.lawservice.app.ui.validation.OptionalPhone;
import com.yunfa365.lawservice.app.ui.dialog.SpinnerDialog;
import com.yunfa365.lawservice.app.utils.AppUtil;
import com.yunfa365.lawservice.app.utils.FileUtil;
import com.yunfa365.lawservice.app.utils.LocationUtil;
import com.yunfa365.lawservice.app.utils.StringUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/4/21.
 * 添加客户
 */
@EActivity(R.layout.activity_office_add_custom)
public class Office_addCustomActivity extends BaseUserActivity {

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

    @NotEmpty(message = "委托人不能为空")
    @Order(1)
    @ViewById
    EditText wtr;

    @OptionalPhone(message = "手机号码格式不正确")
    @Order(2)
    @ViewById
    EditText sjhm;

    @NotEmpty(message = "客户类型不能为空")
    @Order(3)
    @ViewById
    EditText khnx;

    @Order(4)
    @ViewById
    EditText xgzj;

    @ViewById
    View xgzj_layout;

    @ViewById
    View xgzj_line;

    @ViewById
    EditText zyfzr;

    @ViewById
    EditText ywlxr;

    @OptionalEmail(message = "邮箱格式不正确")
    @Order(5)
    @ViewById
    EditText yx;

    @ViewById
    EditText zw;

    @ViewById
    EditText dqyxl;

    @OptionalPhone(message = "固定电话格式不正确")
    @Order(6)
    @ViewById
    EditText gddh;

    @ViewById
    EditText sheng;

    @ViewById
    EditText shi;

    @OptionalIdCard(message = "身份证号格式不正确")
    @Order(7)
    @ViewById
    EditText sfzh;

    @Order(8)
    @ViewById
    EditText xxdz;

    @ViewById
    TextView xgwj;

    @ViewById
    EditText ajbz;

    @ViewById
    EditText khnx_1;

    @ViewById
    EditText khnx_2;

    @ViewById
    EditText khnx_3;

    @ViewById
    EditText khnx_4;

    @ViewById
    TextView khnx1label;

    @ViewById
    TextView khnx2label;

    @ViewById
    TextView khnx3label;

    @ViewById
    TextView khnx4label;

    @ViewById
    View khnx1layout;

    @ViewById
    View khnx2layout;

    @ViewById
    View khnx3layout;

    @ViewById
    View khnx4layout;

    @ViewById
    TextView moreBtn;

    @ViewById
    View moreLayout;

    @Extra
    Custom customItem;

    @Extra
    String customName;

    private CusTomCols[] khnxs;
    private CusTomCols[][] khnxMap;
    private int maxLevel;

    private DiQu[] shengs;
    private DiQu[] shis;
    private String Mid;

    private EditText[] khnxViews;
    private TextView[] khnxLabels;
    private View[] khnxLayouts;

    private Validator validator;
    private QuickRule requiredXgzjRule;

    private int dialogTimes = 0;


    @AfterViews
    void init() {
        mBackView.setVisibility(View.VISIBLE);
        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleTxt.setText("添加委托人");

        String diquStr = FileUtil.getRawFileContent(getResources(), R.raw.diqu);
        shengs = StringUtil.toObjectArray(diquStr, DiQu.class);
        loadCustomType();

        khnxViews = new EditText[]{khnx_1, khnx_2, khnx_3, khnx_4};
        khnxLabels = new TextView[]{khnx1label, khnx2label, khnx3label, khnx4label};
        khnxLayouts = new View[]{khnx1layout, khnx2layout, khnx3layout, khnx4layout};
        khnxMap = new CusTomCols[5][];

        khnx1layout.setVisibility(View.GONE);
        khnx2layout.setVisibility(View.GONE);
        khnx3layout.setVisibility(View.GONE);
        khnx4layout.setVisibility(View.GONE);

        initValidate();
        loadRequiredFields();
        initDefaultValue();
    }

    private void loadCustomType() {
        // 获取诉讼阶段 诉讼地位
        AppRequest request = new AppRequest.Build("Customer/GetCustomerCol")
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
                            khnxs = resp.resultsToArray(CusTomCols.class);
                            khnxMap[0] = khnxs;
                            if (customItem != null) {
                                initDefaultKhnx();
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

    private void loadCaseFiles() {
        AppRequest request = new AppRequest.Build("Case/FileStoreList")
                .addParam("Mid", Mid)
                .addParam("Cols", "1")
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
                        showToast(message);
                    }
                }
            }
        });

        requiredXgzjRule = new QuickRule<EditText>(){
            private String message;
            @Override
            public boolean isValid(EditText view) {
                if (khnxs == null || khnxs.length < 1) {
                    return true;
                }
                Object tag = khnx.getTag();
                if (khnxs[0] != tag) {
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
        };
    }

    private void initDefaultValue() {
        if (customItem == null) {
            Mid = AppUtil.generateMid();
            if (!TextUtils.isEmpty(customName)) {
                wtr.setText(customName);
            }
            initLocation();
        } else {
            // khnx.setText(customItem.CustColsName);  不能在此设置客户类型，会导致证件号码字段不联动，因为此处setText不触发onTextChange，原因是khnxs为null

            Mid = customItem.ID + "";
            loadCaseFiles();

            wtr.setText(customItem.Title);
            // Model to see : initDefaultKhnx();
            sjhm.setText(customItem.Phone);
            sheng.setText(customItem.ProvinceIdTxt);
            shi.setText(customItem.CityIdTxt);
            ajbz.setText(customItem.Make);
            xgzj.setText(customItem.UNums);
            sfzh.setText(customItem.UNums);

            ywlxr.setText(customItem.YwRen);
            zw.setText(customItem.YwRenZhiWu);
            zyfzr.setText(customItem.FzRen);
            dqyxl.setText(customItem.YingXiangLi);
            gddh.setText(customItem.Phone);
            yx.setText(customItem.Email);
            xxdz.setText(customItem.Address);

            if (!TextUtils.isEmpty(customItem.ProvinceIdTxt)) {
                for (DiQu sheng : shengs) {
                    if (sheng.text.equals(customItem.ProvinceIdTxt)) {
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

    private void initDefaultKhnx() {
        String khsxStr = customItem.Model;
        String sxs[] = khsxStr.split("♀");
        for (int i = 0; i < sxs.length; i++) {
            String sx = sxs[i];
            if (TextUtils.isEmpty(sx)) {
                continue;
            }
            String kvs[] = sx.split("\\|");

            khnxLayouts[i].setVisibility(View.VISIBLE);
            CusTomCols labelTag = new CusTomCols();
            CusTomCols viewTag = new CusTomCols();

            labelTag.Title = kvs[0];
            viewTag.Title = kvs[1];
            khnxLabels[i].setText(kvs[0]);
            khnxLabels[i].setTag(labelTag);
            khnxViews[i].setText(kvs[1]);
            khnxViews[i].setTag(viewTag);
        }

        LogUtil.d("---------------------");
        for (CusTomCols ctc : khnxMap[0]) {
            if (ctc.ID == customItem.CustCols) {
                if (ctc.Children != null) {
                    maxLevel = ctc.Children.length;
                } else {
                    maxLevel = 1;
                }

                khnx.setTag(ctc);
                khnx.setText(ctc.Title);
                initKunx(ctc, 0);
                break;
            }
        }
        LogUtil.d("--------************-------------");
    }

    private void initKunx(CusTomCols ctc, int level) {
        if (ctc.Children != null && ctc.Children.length > 0) {
            for (int i = 0; i < ctc.Children.length; i++) {
                CusTomCols child = ctc.Children[i];
                khnxMap[i + 1] = child.Item;

                CusTomCols viewTag = (CusTomCols) khnxViews[i].getTag();
                if (viewTag == null) {
                    continue;
                }
                LogUtil.d("has Children i:" + i + "  " + viewTag.Title);
                for (int j = 0; j < child.Item.length; j++) {
                    if (child.Item[j].Title.equals(viewTag.Title)) {
                        initKunx(khnxMap[i + 1][j], i + 1);
                        break;
                    }
                }
            }

        } else if (ctc.Item != null && ctc.Item.length > 0) {
            LogUtil.d("has Item: level" + level);
            khnxMap[level + 1] = ctc.Item;
        }
    }


    private void setShis(DiQu[] diqus) {
        shis = diqus;
        shi.setText(shis[0].toString());
        shi.setTag(shis[0]);
    }

    private void setKunx(CusTomCols ctc, int level) {

        if (ctc.Children != null && ctc.Children.length > 0) {
            for (int i = 0; i < ctc.Children.length; i++) {
                CusTomCols child = ctc.Children[i];

                khnxLayouts[i].setVisibility(View.VISIBLE);
                khnxLabels[i].setText(child.toString() + "：");
                khnxLabels[i].setTag(child);
                khnxMap[i + 1] = child.Item;
                khnxViews[i].setText(khnxMap[i + 1][0].toString());
                khnxViews[i].setTag(khnxMap[i + 1][0]);
                setKunx(khnxMap[i + 1][0], i + 1);
            }

        } else if (ctc.Item != null && ctc.Item.length > 0) {
            khnxLayouts[level].setVisibility(View.VISIBLE);
            khnxLabels[level].setText("");
            khnxLabels[level].setTag(null);
            khnxMap[level + 1] = ctc.Item;
            khnxViews[level].setText(khnxMap[level + 1][0].toString());
            khnxViews[level].setTag(khnxMap[level + 1][0]);
            setKunx(khnxMap[level + 1][0], level + 1);
        }
    }

//    private void setKhnxNote(CusTomCols ctc) {
//        CusTomCols[] nexts = getNextLevel(ctc);
//        if (nexts.length == 0) {
//            if (khnxMap[2] == null) {
//                khnxLayouts[1].setVisibility(View.GONE);
//            }
//            return;
//        }
//
//        khnxLayouts[1].setVisibility(View.VISIBLE);
//        khnxLabels[1].setText("");
//        khnxMap[2] = nexts;
//
//        khnxViews[1].setText(khnxMap[2][0].toString());
//        khnxViews[1].setTag(khnxMap[2][0]);
//    }

    private CusTomCols[] getNextLevel(CusTomCols ctc) {
        List<CusTomCols> ctcList = new ArrayList<CusTomCols>();
        for (CusTomCols c : khnxs) {
            if (ctc.ID == c.Fid)
                ctcList.add(c);
        }
        return ctcList.toArray(new CusTomCols[ctcList.size()]);
    }

    @Click(R.id.khnx)
    void khnxOnClick(View view) {
        new SpinnerDialog(this, "请选择", khnxMap[0], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                khnx.setTag(khnxMap[0][which]);
                khnx.setText(khnxMap[0][which].toString());

                for (int i = 0; i < 4; i++) {
                    khnxLayouts[i].setVisibility(View.GONE);
                    khnxMap[i + 1] = null;
                }
                if (khnxMap[0][which].Children != null) {
                    maxLevel = khnxMap[0][which].Children.length;
                } else {
                    maxLevel = 1;
                }

                setKunx(khnxMap[0][which], 0);
            }
        }).show();
    }

    @TextChange(R.id.khnx)
    void khnxTextChange(TextView tv, CharSequence text) {
        if (khnxs == null || khnxs.length < 1) {
            return;
        }
        if (text.toString().equals(khnxs[0].Title)) {
            xgzj_layout.setVisibility(View.GONE);
            xgzj_line.setVisibility(View.GONE);
        } else {
            xgzj_layout.setVisibility(View.VISIBLE);
            xgzj_line.setVisibility(View.VISIBLE);
        }
    }

    @Click(R.id.khnx_1)
    void khnx1OnClick(View view) {
        if (khnxMap[1] == null) {
            return;
        }
        new SpinnerDialog(this, "请选择", khnxMap[1], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                khnx_1.setTag(khnxMap[1][which]);
                khnx_1.setText(khnxMap[1][which].toString());

                if (maxLevel == 1 && khnxMap[1][which].Item == null) {
                    for (int i = 1; i < 4; i++) {
                        khnxLayouts[i].setVisibility(View.GONE);
                    }
                }
                setKunx(khnxMap[1][which], 1);
            }
        }).show();
    }

    @Click(R.id.khnx_2)
    void khnx2OnClick(View view) {
        if (khnxMap[2] == null) {
            return;
        }
        new SpinnerDialog(this, "请选择", khnxMap[2], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                khnx_2.setTag(khnxMap[2][which]);
                khnx_2.setText(khnxMap[2][which].toString());

                if (maxLevel == 1 && khnxMap[2][which].Item == null) {
                    for (int i = 2; i < 4; i++) {
                        khnxLayouts[i].setVisibility(View.GONE);
                    }
                }
                setKunx(khnxMap[2][which], 2);
            }
        }).show();
    }

    @Click(R.id.khnx_3)
    void khnx3OnClick(View view) {
        if (khnxMap[3] == null) {
            return;
        }
        new SpinnerDialog(this, "请选择", khnxMap[3], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                khnx_3.setTag(khnxMap[3][which]);
                khnx_3.setText(khnxMap[3][which].toString());

                if (khnxMap[3][which].Item == null) {
                    for (int i = 3; i < 4; i++) {
                        khnxLayouts[i].setVisibility(View.GONE);
                    }
                }
                setKunx(khnxMap[3][which], 3);
            }
        }).show();
    }

    @Click(R.id.khnx_4)
    void khnx4OnClick(View view) {
        if (khnxMap[4] == null) {
            return;
        }
        new SpinnerDialog(this, "请选择", khnxMap[4], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                khnx_4.setTag(khnxMap[4][which]);
                khnx_4.setText(khnxMap[4][which].toString());
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


    @Click(android.R.id.button1)
    void button1OnClick(View view) {
        validator.validate();
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

    private void doCommit() {
        LogUtil.d("+++++++++++doCommit+++");
        String khsx = "";
        for (int i = 0; i < khnxLayouts.length; i++) {
            if (khnxLayouts[i].getVisibility() == View.VISIBLE) {
                Object tag = khnxLabels[i].getTag();
                String label = tag == null ? "" : tag.toString();
                String value = ((CusTomCols) khnxViews[i].getTag()).Title;
                khsx += label + "|" + value + "♀";
            } else {
                break;
            }
        }
        final Custom custom = new Custom();
        custom.CustCols = ((CusTomCols) khnx.getTag()).ID;
//        custom.CustColsName = ((CusTomCols) khnx.getTag()).toString();
        custom.Title = wtr.getText().toString();
        custom.Model = khsx;
        custom.Phone = sjhm.getText().toString();
//        custom.Province = sheng.getText().toString();
//        custom.City = shi.getText().toString();
        custom.Make = ajbz.getText().toString();
        custom.UNums = xgzj.getText().toString();
//        custom.IdCard = sfzh.getText().toString();

        custom.YwRen = ywlxr.getText().toString();
        custom.YwRenZhiWu = zw.getText().toString();
        custom.FzRen = zyfzr.getText().toString();
        custom.YingXiangLi = dqyxl.getText().toString();
//        custom.Phone2 = gddh.getText().toString();
        custom.Email = yx.getText().toString();
        custom.Address = xxdz.getText().toString();

        AppRequest.Build build = new AppRequest.Build("Customer/UpdateCustomer")
                .addParam("TCustCols", custom.CustCols + "")          // 客户类型
                .addParam("Title", custom.Title)              // 客户名称
                .addParam("Model", custom.Model)       // 客户属性
                .addParam("Phone", custom.Phone)       // 手机号码
                .addParam("TProvince", custom.ProvinceIdTxt)   // 省
                .addParam("TCapital", custom.CityIdTxt)    // 市
                .addParam("Make", custom.Make)        // 备注
                .addParam("UNums", custom.UNums)       // 身份证号
                .addParam("IdCard", custom.UNums)
                .addParam("YwRen", custom.YwRen)
                .addParam("YwRenZhiWu", custom.YwRenZhiWu)
                .addParam("FzRen", custom.FzRen)
                .addParam("YingXiangLi", custom.YingXiangLi)
                .addParam("Phone2", custom.Phone)
                .addParam("Email", custom.Email)
                .addParam("Adress", custom.Address)
                .addParam("Mid", Mid);
        if (customItem != null) build.addParam("ObjID", customItem.ID + "");
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
                            String resultId = resp.RData;
                            custom.ID = Integer.parseInt(resultId);
                            Intent data = new Intent();
                            data.putExtra("customItem", custom);
                            setResult(RESULT_OK, data);
                            finish();
                        } else {
                            AppUtil.showToast(Office_addCustomActivity.this, resp.Message);
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
                    public void onStart(AgnettyResult result) {
                        showLoading();
                    }

                    @Override
                    public void onComplete(AgnettyResult result) {
                        hideLoading();
                        AppResponse resp = (AppResponse)result.getAttach();
                        if (resp.flag) {
                            List<String> requiredFields = resp.resultsToList(String.class);
                            if (requiredFields.contains("Phone")) {
                                validator.put(sjhm, new NotEmptyQuickRule("不能为空"));
                                sjhm.setHint("请填写手机号");
                            }
                            if (requiredFields.contains("CustCardNums")) {
                                validator.put(xgzj, requiredXgzjRule);
                                xgzj.setHint("请填写证件号码");
                                showMoreLayout();
                            }
                            if (requiredFields.contains("IdCard")) {
                                validator.put(sfzh, new NotEmptyQuickRule("不能为空"));
                                sfzh.setHint("请填写身份证号");
                                showMoreLayout();
                            }
                            if (requiredFields.contains("Address")) {
                                validator.put(xxdz, new NotEmptyQuickRule("不能为空"));
                                xxdz.setHint("请填写详细地址");
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

    @Override
    public void showLoading() {
        if (this.dialogTimes == 0) {
            super.showLoading();
        }
        this.dialogTimes++;
    }

    @Override
    public void hideLoading() {
        this.dialogTimes--;
        if (this.dialogTimes == 0) {
            super.hideLoading();
        }
    }

}
