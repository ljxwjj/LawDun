package com.yunfa365.lawservice.app.ui.activity.office;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyResult;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.CaseCols;
import com.yunfa365.lawservice.app.pojo.ShenCha;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;
import com.yunfa365.lawservice.app.ui.adapter.CommonListAdapter;
import com.yunfa365.lawservice.app.ui.view.holder.CommonFooterViewHolder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.util.List;


/**
 * Created by Administrator on 2016/4/19.
 * 新增案件-利益冲突预测
 */
@EActivity(R.layout.activity_office61)
public class Office61Activity extends BaseUserActivity {
    private static final int REQUEST_CODE = 1;

    @Extra
    CaseCols selectedCaseCols;

    @ViewById(R.id.base_id_back)
    View mBackView;

    @ViewById(R.id.base_id_title)
    TextView mTitleTxt;

    @ViewById(R.id.base_right_btn)
    ImageView mRightImage;

    @ViewById(R.id.base_right_txt)
    TextView mRightTxt;

    @ViewById(R.id.search_layout)
    View searchLayout;

    @ViewById(R.id.search_result_layout)
    View resultLayout;

    @ViewById(android.R.id.text1)
    EditText mInput1;

    @ViewById(android.R.id.text2)
    EditText mInput2;

    @ViewById(android.R.id.button1)
    TextView mButton1;

    @ViewById(android.R.id.button2)
    TextView mButton2;

    @ViewById(android.R.id.button3)
    TextView mButton3;

    @ViewById
    RecyclerView listView;
    MyAdapter mAdapter;

    private String keyword1, keyword2;

    @AfterViews
    void init(){
        mBackView.setVisibility(View.VISIBLE);
        mBackView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleTxt.setText("利益冲突预测");

        mInput2.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    onClickOk();
                }
                return true;
            }
        });
        mButton1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onClickOk();
            }
        });
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLayout.setVisibility(View.VISIBLE);
                resultLayout.setVisibility(View.GONE);
            }
        });
        mButton3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                gotoOffice41();
            }
        });
        if (selectedCaseCols == null) {
            mButton2.setVisibility(View.GONE);
        }

        mAdapter = new MyAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(layoutManager);
        listView.setAdapter(mAdapter);
    }

    private void gotoOffice41() {
        Intent intent = new Intent(this, Office41_1Activity_.class);
        intent.putExtra("selectedCaseCols", selectedCaseCols);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @OnActivityResult(REQUEST_CODE)
    void onResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private void hideKeyBord(View v) {
        InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void onClickOk() {
        keyword1 = mInput1.getText().toString();
        keyword2 = mInput2.getText().toString();
        if (TextUtils.isEmpty(keyword1) || TextUtils.isEmpty(keyword2)) {
            showToast("请输入委托人/当事人和对方当事人");
            return;
        }
        mAdapter.mData.clear();
        mAdapter.notifyDataSetChanged();

        hideKeyBord(mInput1);
        doSearch();
    }

    private void doSearch() {
        AppRequest request = new AppRequest.Build("api/Case/Case_ChongTu")
                .addParam("Custom", keyword1)
                .addParam("DCustom", keyword2)
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
                            searchLayout.setVisibility(View.GONE);
                            resultLayout.setVisibility(View.VISIBLE);

                            List<ShenCha> shenChas = resp.resultsToList(ShenCha.class);
                            mAdapter.mData.addAll(shenChas);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            showAddDialog();
                        }
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        hideLoading();
                    }
                })
                .execute();
    }

    private void showAddDialog() {
        new AlertDialog.Builder(this).setTitle(R.string.notice)
                .setMessage("未检测到相关案件，去立案？")
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("去立案", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gotoOffice41();
                    }
                }).show();
    }

    class MyAdapter extends CommonListAdapter {

        public MyAdapter(Context context) {
            super(context);

            mHeaderCount = 0;
            mFooterCount = 0;
            hasDetail = false;
        }

        @Override
        protected CommonFooterViewHolder getFooterViewHolder() {
            return null;
        }

    }

}
