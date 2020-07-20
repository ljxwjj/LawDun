package com.yunfa365.lawservice.app.ui.activity.office;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyResult;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.CaseCols;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;
import com.yunfa365.lawservice.app.ui.dialog.SpinnerDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Administrator on 2016/4/20.
 * 新增案件
 */
@EActivity(R.layout.activity_office41)
public class Office41Activity extends BaseUserActivity {
    private static final int REQUEST_CODE = 1;

    private CaseCols[] types;
    private CaseCols[] categorys;
    private CaseCols selectedCaseCols;

    @ViewById(R.id.base_id_back)
    View mBackView;

    @ViewById(R.id.base_id_title)
    TextView mTitleTxt;

    @ViewById(R.id.base_right_btn)
    ImageView mRightImage;

    @ViewById(R.id.base_right_txt)
    TextView mRightTxt;

    @ViewById(android.R.id.text1)
    EditText text1;
    @ViewById(android.R.id.text2)
    EditText text2;

    @AfterViews
    void init(){
        mBackView.setVisibility(View.VISIBLE);
        mBackView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleTxt.setText("新增案件");
        loadData();
        //String casecolsStr = FileUtil.getRawFileContent(getResources(), R.raw.case_cols);

    }

    private void loadData() {
        // 获取案件类型
        AppRequest request = new AppRequest.Build("Case/GetCaseColsList")
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
                            types = resp.resultsToArray(CaseCols.class);
                            for (CaseCols cc : types) {
                                for (CaseCols child : cc.Children) {
                                    child.Fid = cc.ID;
                                    //child.TempID = cc.TempID;
                                    child.FTitle = cc.Title;
                                }
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

    @Click(android.R.id.text1)
    void text1OnClick(View view) {
        if (types == null)
            return;
        new SpinnerDialog(this, "请选择案件类型", types, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CaseCols cc = types[which];
                text1.setText(cc.toString());
                categorys = cc.Children;

                selectedCaseCols = categorys[0];
                text2.setText(selectedCaseCols.toString());
            }
        }).show();
    }

    @Click(android.R.id.text2)
    void text2OnClick(View view) {
        if (categorys == null)
            return;
        new SpinnerDialog(this, "选择案件分类", categorys, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CaseCols cc = categorys[which];
                text2.setText(cc.toString());
                selectedCaseCols = cc;
            }
        }).show();
    }

    @Click(android.R.id.button1)
    void button1OnClick(View view) {
        if (selectedCaseCols == null) {
            showToast("请选择案件类型");
            return;
        }
        if ("1".equals(selectedCaseCols.TempID)) {
            Intent intent = new Intent(this, Office61Activity_.class);
            intent.putExtra("selectedCaseCols", selectedCaseCols);
            startActivityForResult(intent, REQUEST_CODE);
        } else if ("2".equals(selectedCaseCols.TempID)) {
            Intent intent = new Intent(this, Office41_2Activity_.class);
            intent.putExtra("selectedCaseCols", selectedCaseCols);
            startActivityForResult(intent, REQUEST_CODE);
        } else if ("3".equals(selectedCaseCols.TempID)) {
            Intent intent = new Intent(this, Office41_3Activity_.class);
            intent.putExtra("selectedCaseCols", selectedCaseCols);
            startActivityForResult(intent, REQUEST_CODE);
        } else if ("4".equals(selectedCaseCols.TempID)) {
            Intent intent = new Intent(this, Office41_4Activity_.class);
            intent.putExtra("selectedCaseCols", selectedCaseCols);
            startActivityForResult(intent, REQUEST_CODE);
        } else if ("5".equals(selectedCaseCols.TempID)) {
            Intent intent = new Intent(this, Office41_5Activity_.class);
            intent.putExtra("selectedCaseCols", selectedCaseCols);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @OnActivityResult(REQUEST_CODE)
    void onResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            finish();
        }
    }
}
