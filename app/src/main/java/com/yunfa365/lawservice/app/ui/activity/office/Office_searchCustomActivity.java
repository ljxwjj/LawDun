package com.yunfa365.lawservice.app.ui.activity.office;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyResult;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.Custom;
import com.yunfa365.lawservice.app.pojo.YesNo;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;
import com.yunfa365.lawservice.app.ui.dialog.SpinnerDialog;
import com.yunfa365.lawservice.app.ui.view.pulltorefresh.XListView;
import com.yunfa365.lawservice.app.utils.AppUtil;
import com.yunfa365.lawservice.app.utils.ViewHolder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/21.
 * 选择委托人
 */
@EActivity(R.layout.activity_office_search_custom)
public class Office_searchCustomActivity extends BaseUserActivity {
    private static final int ADD_WTR_REQUEST_CODE = 1;
    private static final YesNo[] jstjs = {new YesNo(1, "根据委托人"), new YesNo(4, "根据电话")};

    private int pageSize = 10;

    @ViewById(R.id.base_id_back)
    View mBackView;

    @ViewById(R.id.base_id_title)
    TextView mTitleTxt;

    @ViewById(R.id.base_right_btn)
    ImageView mRightImage;

    @ViewById(R.id.base_right_txt)
    TextView mRightTxt;

    @ViewById(android.R.id.list)
    XListView mListView;
    MyAdapter mAdapter;
    List<Custom> mData;

    private EditText mInput1, mInput2;
    private View mButton1, mButton2;
    private String keyword1;
    private int keyword2 = 1;
    private int currentPage;

    @AfterViews
    void init(){
        mBackView.setVisibility(View.VISIBLE);
        mBackView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleTxt.setText("选择委托人");

        View headerView = LinearLayout.inflate(this, R.layout.list_header_office_wtr, null);
        mInput1 = (EditText) headerView.findViewById(android.R.id.text1);
        mInput2 = (EditText) headerView.findViewById(android.R.id.text2);
        mButton1 = headerView.findViewById(android.R.id.button1);
        mButton2 = headerView.findViewById(android.R.id.button2);
        mListView.addHeaderView(headerView);
        mInput1.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    keyword1 = mInput1.getText().toString();
                    if (TextUtils.isEmpty(keyword1)) {
                        AppUtil.showToast(Office_searchCustomActivity.this, "请输入检索条件");
                        return false;
                    }
                    currentPage = 1;
                    mData.clear();
                    mAdapter.notifyDataSetChanged();

                    hideKeyBord(v);
                    doSearch();
                }
                return true;
            }
        });
        mInput2.setText(jstjs[1].toString());
        mInput2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SpinnerDialog(Office_searchCustomActivity.this, "请选择", jstjs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        keyword2 = jstjs[which].id;
                        mInput2.setText(jstjs[which].toString());
                    }
                }).show();
            }
        });

        mButton1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                keyword1 = mInput1.getText().toString();
                if (TextUtils.isEmpty(keyword1)) {
                    AppUtil.showToast(Office_searchCustomActivity.this, "请输入检索条件");
                    return;
                }
                currentPage = 1;
                mData.clear();
                mAdapter.notifyDataSetChanged();

                hideKeyBord(v);
                doSearch();
            }
        });
        mButton2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String customName = mInput1.getText().toString();
                Intent intent = new Intent(Office_searchCustomActivity.this, Office_addCustomActivity_.class);
                intent.putExtra("customName", customName);
                startActivityForResult(intent, ADD_WTR_REQUEST_CODE);
            }
        });
        mListView.setPullRefreshEnable(false);
        mListView.setPullLoadEnable(false);
        mListView.setAutoLoadEnable(true);
        mData = new ArrayList<Custom>();
        mAdapter = new MyAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setXListViewListener(new XListView.IXListViewListener(){

            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                currentPage++;
                doSearch();
            }
        });
        currentPage = 1;
        doSearch();
    }

    @OnActivityResult(ADD_WTR_REQUEST_CODE)
    void addWtrOnResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Custom custom = (Custom) data.getSerializableExtra("customItem");
            setResult(RESULT_OK, data);
            finish();
        }
    }

    private void hideKeyBord(View v) {
        InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void doSearch() {// 检索所有客户
        String searchType = TextUtils.isEmpty(keyword1)?"1":(keyword2 + "");
        AppRequest request = new AppRequest.Build("Customer/GetCustomerAllList")
                .addParam("Title", keyword1)
                .addParam("SearchType", searchType)
                .addParam("Pages", currentPage+"")
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
                            List<Custom> shenChas = resp.resultsToList(Custom.class);
                            mData.addAll(shenChas);
                            mAdapter.notifyDataSetChanged();
                            if (shenChas.size() >= pageSize) {
                                mListView.setPullLoadEnable(true);
                            } else {
                                mListView.setPullLoadEnable(false);
                            }
                        } else {
                            mListView.setPullLoadEnable(false);
                            AppUtil.showToast(Office_searchCustomActivity.this, resp.Message);
                        }
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        hideLoading();
                    }
                })
                .execute();
    }

    class MyAdapter extends BaseAdapter implements View.OnClickListener {

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Custom item = (Custom) getItem(position);
            if (convertView == null) {
                convertView = LinearLayout.inflate(Office_searchCustomActivity.this, R.layout.item_office_wtr, null);
            }
            TextView text1 = ViewHolder.get(convertView, R.id.text1);
            TextView text3 = ViewHolder.get(convertView, R.id.text3);
            TextView text4 = ViewHolder.get(convertView, R.id.text4);
            TextView text5 = ViewHolder.get(convertView, R.id.text5);
            TextView button1 = ViewHolder.get(convertView, android.R.id.button1);

            text1.setText(item.Title);
            text3.setText("联系电话：" + item.Phone);
            text4.setText("地　　区：" + item.Province + item.City);
            text5.setText("入库时间：" + item.Addtime);
            button1.setOnClickListener(this);
            button1.setTag(item);
            return convertView;
        }

        @Override
        public void onClick(View v) {
            Custom c = (Custom) v.getTag();
            Intent data = new Intent();
            data.putExtra("customItem", c);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Click(R.id.base_id_title)
    void titleOnClick(View view) {
        Long time = System.currentTimeMillis();
        Object tag = view.getTag();
        if (tag == null) {
            view.setTag(time);
            return;
        }
        Long tagTime = (Long)tag;
        if (time - tagTime < 400) {
            titleOnDoubleClick(view);
            view.setTag(null);
            return;
        }
        view.setTag(time);
    }

    void titleOnDoubleClick(View view) {
        if (mAdapter.getCount() > 10)mListView.setSelection(10);
        mListView.smoothScrollToPosition(0);
    }
}
