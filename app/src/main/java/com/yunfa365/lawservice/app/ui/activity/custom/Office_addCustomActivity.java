package com.yunfa365.lawservice.app.ui.activity.custom;

import android.Manifest;
import android.content.Context;
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
import com.yunfa365.lawservice.app.pojo.YesNo;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;
import com.yunfa365.lawservice.app.ui.dialog.AddressPickerDialog;
import com.yunfa365.lawservice.app.ui.validation.OptionalEmail;
import com.yunfa365.lawservice.app.ui.validation.OptionalPhone;
import com.yunfa365.lawservice.app.ui.dialog.SpinnerDialog;
import com.yunfa365.lawservice.app.ui.view.addresspickerlib.YwpAddressBean;
import com.yunfa365.lawservice.app.utils.AppUtil;
import com.yunfa365.lawservice.app.utils.FileUtil;
import com.yunfa365.lawservice.app.utils.LocationUtil;
import com.yunfa365.lawservice.app.utils.StringUtil;
import com.yunfa365.lawservice.app.utils.UriUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import java.util.List;


/**
 * Created by Administrator on 2016/4/21.
 * 添加客户
 */
@EActivity(R.layout.activity_office_add_custom)
public class Office_addCustomActivity extends BaseUserActivity {
    private static final int SELECT_FILE_REQUEST_CODE = 1;

    private YesNo[] khdjs = {new YesNo(0, "选择客户等级"),
            new YesNo(1, "新建客户"),
            new YesNo(2, "初步意向客户"),
            new YesNo(3, "高意向客户"),
            new YesNo(4, "正式客户"),
            new YesNo(5, "无意向客户"),
            new YesNo(6, "无效客户")};

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

    @ViewById
    EditText dsr;

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
    EditText khdj;

    @ViewById
    EditText ajbz;

    // ------------------更多--------------------
    @ViewById
    EditText ywlxr;

    @ViewById
    EditText zw;

    @ViewById
    EditText zyfzr;

    @ViewById
    EditText dqyxl;

    @OptionalPhone(message = "固定电话格式不正确")
    @Order(5)
    @ViewById
    EditText gddh;

    @OptionalEmail(message = "邮箱格式不正确")
    @Order(6)
    @ViewById
    EditText yx;

    @ViewById
    EditText wxhm;

    @ViewById
    EditText qqhm;

    @ViewById
    TextView xgwj;

    @ViewById
    TextView ssqy;

    @Order(8)
    @ViewById
    EditText xxdz;

    @ViewById
    TextView moreBtn;

    @ViewById
    View moreLayout;

    @Extra
    Custom customItem;

    @Extra
    String customName;

    private CusTomCols[] khnxs;

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

        loadCustomType();
        initValidate();
        initDefaultValue();
    }

    private void loadCustomType() {
        // 获取诉讼阶段 诉讼地位
        AppRequest request = new AppRequest.Build("api/Custom/Cols_Get")
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
            if (!TextUtils.isEmpty(customName)) {
                wtr.setText(customName);
            }
            khdj.setText(khdjs[0].toString());
            khdj.setTag(khdjs[0]);
            initLocation();
        } else {
            loadCaseFiles();

            wtr.setText(customItem.Title);
            dsr.setText(customItem.CaseUName);
            sjhm.setText(customItem.Mobile);
            khnx.setText(customItem.CustColsTxt);
            khnx.setTag(new CusTomCols(customItem.CustCols, ""));
            // Model to see : initDefaultKhnx();
            ajbz.setText(customItem.Make);
            xgzj.setText(customItem.UNums);
            khdj.setText(customItem.CustRanksTxt);
            khdj.setTag(new YesNo(customItem.CustRanks, ""));

            DiQu[] addressTag = {
                    new DiQu(),
                    new DiQu(),
                    new DiQu(),
            };
            addressTag[0].id = customItem.ProvinceId;
            addressTag[1].id = customItem.CityId;
            addressTag[2].id = customItem.AreaId;
            YwpAddressBean.getInstance(this).initAddress(addressTag);
            ssqy.setTag(addressTag);
            ssqy.setText(String.format("%s-%s-%s", addressTag[0].text, addressTag[1].text, addressTag[2].text));

            ywlxr.setText(customItem.YwRen);
            zw.setText(customItem.YwRenZhiWu);
            zyfzr.setText(customItem.FzRen);
            dqyxl.setText(customItem.YingXiangLi);
            gddh.setText(customItem.Phone);
            yx.setText(customItem.Email);
            xxdz.setText(customItem.Address);
            wxhm.setText(customItem.WeiXinNums);
            qqhm.setText(customItem.QQNums);

        }
    }

    private LocationUtil.OnGetLocationListener locationListener = new LocationUtil.OnGetLocationListener() {
        @Override
        public void onGetLocation(String province, String city, String district) {
            if (isFinishing()) return;

            if (ssqy.getTag() == null) {
                DiQu[] addressTag = YwpAddressBean.getInstance(Office_addCustomActivity.this)
                        .initAddress(province, city, district);
                ssqy.setTag(addressTag);
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
    }


    @Click(R.id.khnx)
    void khnxOnClick(View view) {
        new SpinnerDialog(this, "请选择", khnxs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                khnx.setTag(khnxs[which]);
                khnx.setText(khnxs[which].toString());
            }
        }).show();
    }

    @Click(R.id.ssqy)
    void ssqyOnClick() {
        DiQu[] addressTag = (DiQu[]) ssqy.getTag();
        new AddressPickerDialog(this, addressTag, true)
                .setAddressPickerListener(new AddressPickerDialog.AddressPickerListener() {
                    @Override
                    public void onSure(DiQu[] addresss) {
                        ssqy.setText(String.format("%s-%s-%s", addresss[0].text, addresss[1].text, addresss[2].text));
                        ssqy.setTag(addresss);
                    }

                    @Override
                    public void onClear() {
                        ssqy.setText("");
                        ssqy.setTag(null);
                    }
                })
                .show();
    }

    @Click(R.id.khdj)
    void khdjOnClick() {
        new SpinnerDialog(this, "请选择客户等级", khdjs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                YesNo selected = khdjs[which];
                khdj.setText(selected.toString());
                khdj.setTag(selected);
            }
        }).show();
    }

    @Click(R.id.xgwj)
    void xgwjOnClick(View view) {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        startGetContent();
                    } else {
                        showToast("获取权限失败");
                    }
                }, Throwable::printStackTrace);
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

    private void doCommit() {
        LogUtil.d("+++++++++++doCommit+++");

        int sheng = 0, shi = 0, qu = 0;
        DiQu[] dqs = (DiQu[]) ssqy.getTag();
        if (dqs != null) {
            sheng = dqs[0].id;
            shi = dqs[1].id;
            qu = dqs[2].id;
        }
        FormUploadFile file = (FormUploadFile) xgwj.getTag();

        AppRequest.Build build = new AppRequest.Build("api/Custom/Add")
                .addParam("Cid", customItem == null?"0":customItem.ID + "")
                .addParam("Title", wtr.getText().toString())              // 客户名称
                .addParam("CaseUName", dsr.getText().toString())       // 当事人名称
                .addParam("Mobile", sjhm.getText().toString())
                .addParam("CustCols", ((CusTomCols) khnx.getTag()).ID + "")          // 客户类型
                .addParam("CustRanks", ((YesNo)khdj.getTag()).id + "")
                .addParam("UNums", xgzj.getText().toString())       // 身份证号
                .addParam("Make", ajbz.getText().toString())        // 备注
                .addParam("YwRen", ywlxr.getText().toString())
                .addParam("YwRenZhiWu", zw.getText().toString())
                .addParam("FzRen", zyfzr.getText().toString())
                .addParam("YingXiangLi", dqyxl.getText().toString())
                .addParam("WeiXinNums", wxhm.getText().toString())
                .addParam("QQNums", qqhm.getText().toString())
                .addParam("Phone", gddh.getText().toString())       // 固定电话
                .addParam("Email", yx.getText().toString())
                .addParam("ProvinceId", sheng + "")   // 省
                .addParam("CityId", shi + "")    // 市
                .addParam("AreaId", qu + "")
                .addParam("Address", xxdz.getText().toString());
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
                            Custom custom = resp.getFirstObject(Custom.class);
                            Intent data = new Intent();
                            data.putExtra("customItem", custom);
                            setResult(RESULT_OK, data);
                            finish();
                            CustomInfoActivity_.intent(Office_addCustomActivity.this)
                                    .ID(custom.ID)
                                    .start();
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

    void startGetContent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult( Intent.createChooser(intent, "选择上传文件"), SELECT_FILE_REQUEST_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            AppUtil.showToast(this, "Please install a File Manager.");
        }
    }

    @OnActivityResult(SELECT_FILE_REQUEST_CODE)
    void xgfjOnResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
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
            xgwj.setText(fileName);
            xgwj.setTag(file);
        }
    }

}
