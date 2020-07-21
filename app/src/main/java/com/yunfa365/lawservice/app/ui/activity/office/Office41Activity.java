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
import com.yunfa365.lawservice.app.utils.ListUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2016/4/20.
 * 新增案件
 */
@EActivity(R.layout.activity_office41)
public class Office41Activity extends BaseUserActivity {
    private static final int REQUEST_CODE = 1;

    private List<CaseCols> allCaseCols;
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
    }

    private void loadData() {
        // 获取案件类型
        AppRequest request = new AppRequest.Build("api/Case/Cols_Get")
                .addParam("Fid", "-1")
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
                            allCaseCols = resp.resultsToList(CaseCols.class);
                            initCaseCols_types();
                        }
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        hideLoading();
                    }
                })
                .execute();
    }

    private Comparator<CaseCols> caseColsComparator = (o1, o2) -> o1.Sort - o2.Sort;

    private void initCaseCols_types() {
        types = ListUtils.filter(allCaseCols, caseCols -> caseCols.Fid == 0)
                .toArray(new CaseCols[0]);
        Arrays.sort(types, caseColsComparator);
    }

    private void initCaseCols_categorys(CaseCols fCaseCols) {
        categorys = ListUtils.filter(allCaseCols, caseCols -> {
            boolean result = caseCols.Fid == fCaseCols.ID;
            if (result) {
                caseCols.TempCols = fCaseCols.TempCols;
                caseCols.IsChongTu = fCaseCols.IsChongTu;
            }
            return result;
        })
                .toArray(new CaseCols[0]);
        Arrays.sort(categorys, caseColsComparator);
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
                initCaseCols_categorys(cc);

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
        if (selectedCaseCols.IsChongTu == 1) {
            Intent intent = new Intent(this, Office61Activity_.class);
            intent.putExtra("selectedCaseCols", selectedCaseCols);
            startActivityForResult(intent, REQUEST_CODE);
        } else if ("ms".equals(selectedCaseCols.TempCols)) {
            Intent intent = new Intent(this, Office41_1Activity_.class);
            intent.putExtra("selectedCaseCols", selectedCaseCols);
            startActivityForResult(intent, REQUEST_CODE);
        } else if ("fs".equals(selectedCaseCols.TempCols)) {
            Intent intent = new Intent(this, Office41_3Activity_.class);
            intent.putExtra("selectedCaseCols", selectedCaseCols);
            startActivityForResult(intent, REQUEST_CODE);
        } else if ("gw".equals(selectedCaseCols.TempCols)) {
            Intent intent = new Intent(this, Office41_4Activity_.class);
            intent.putExtra("selectedCaseCols", selectedCaseCols);
            startActivityForResult(intent, REQUEST_CODE);
        } else if ("ds".equals(selectedCaseCols.TempCols)) {
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
