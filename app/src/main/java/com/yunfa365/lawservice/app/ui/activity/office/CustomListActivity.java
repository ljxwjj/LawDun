package com.yunfa365.lawservice.app.ui.activity.office;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;

import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyResult;
import com.android.agnetty.utils.LogUtil;
import com.nineoldandroids.view.ViewHelper;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.Custom;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;
import com.yunfa365.lawservice.app.ui.activity.mycase.CustomInfoActivity_;
import com.yunfa365.lawservice.app.ui.view.pulltorefresh.XListView;
import com.yunfa365.lawservice.app.utils.AppUtil;
import com.yunfa365.lawservice.app.utils.ViewHolder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/18.
 * 客户
 */
@EActivity(R.layout.activity_custom_list)
public class CustomListActivity extends BaseUserActivity {
    private static final int ADD_REQUEST_CODE = 1;

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
    private int currentPage;

    @ViewById(R.id.id_drawerLayout)
    DrawerLayout mDrawerLayout;

    CustomMenuRightFragment menuRightFragment;

    private String keyword1 = "";
    private String keyword2 = "1";

    @AfterViews
    void init() {
        menuRightFragment = (CustomMenuRightFragment) getSupportFragmentManager().findFragmentById(R.id.id_right_menu);

        mBackView.setVisibility(View.VISIBLE);
        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleTxt.setText("我的客户");
        mRightImage.setVisibility(View.VISIBLE);
        mRightImage.setImageResource(R.mipmap.add_btn);
        mRightImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomListActivity.this, Office_addCustomActivity_.class);
                startActivityForResult(intent, ADD_REQUEST_CODE);
            }
        });

        mRightTxt.setVisibility(View.VISIBLE);
        mRightTxt.setText("");
        mRightTxt.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.search_normal, 0, 0, 0);
        mRightTxt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.RIGHT);
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED,
                        Gravity.RIGHT);
            }
        });

        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener()
        {
            @Override
            public void onDrawerStateChanged(int newState)
            {
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset)
            {
                View mContent = mDrawerLayout.getChildAt(0);
                View mMenu = drawerView;
                float scale = 1 - slideOffset;
                float rightScale = 0.8f + scale * 0.2f;

                ViewHelper.setTranslationX(mContent,
                        -mMenu.getMeasuredWidth() * slideOffset);
                ViewHelper.setPivotX(mContent, mContent.getMeasuredWidth());
                ViewHelper.setPivotY(mContent,
                        mContent.getMeasuredHeight() / 2);
                mContent.invalidate();
                ViewHelper.setScaleX(mContent, rightScale);
                ViewHelper.setScaleY(mContent, rightScale);
            }

            @Override
            public void onDrawerOpened(View drawerView)
            {

            }

            @Override
            public void onDrawerClosed(View drawerView)
            {
                mDrawerLayout.setDrawerLockMode(
                        DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
                hideKeyBord(menuRightFragment.gjc);
            }
        });
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
                Gravity.RIGHT);

        mListView.setPullRefreshEnable(false);
        mListView.setPullLoadEnable(false);
        mListView.setAutoLoadEnable(true);
        mData = new ArrayList<>();
        mAdapter = new MyAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setXListViewListener(new XListView.IXListViewListener(){

            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                if (isLoading()) {
                    return;
                }
                currentPage++;
                doSearch();
            }
        });
        currentPage = 1;
        doSearch();
    }

    void doSearch() {
        LogUtil.d("doSearch page:" + currentPage);

        AppRequest request = new AppRequest.Build("Customer/GetCustomerList")
                .addParam("Title", keyword1)
                .addParam("SearchType", keyword2)
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
                        }
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        hideLoading();
                    }
                })
                .execute();
    }

    //String title, String type, String cols, String caseYear, String auditStat
    public void reSearch(String... params) {
        if (params != null && params.length == 2) {
            this.keyword1 = params[0];
            this.keyword2 = params[1];
        }
        currentPage = 1;
        mData.clear();
        doSearch();
    }

    class MyAdapter extends BaseAdapter {

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
                convertView = LinearLayout.inflate(CustomListActivity.this, R.layout.item_custom_list, null);
            }
            TextView text1 = ViewHolder.get(convertView, R.id.text1);
            TextView text3 = ViewHolder.get(convertView, R.id.text3);
            TextView text4 = ViewHolder.get(convertView, R.id.text4);
            TextView text5 = ViewHolder.get(convertView, R.id.text5);

            text1.setText(item.Title);
            text3.setText("联系电话：" + item.Phone);
            text4.setText("地　　区：" + item.Province + item.City);
            text5.setText("入库时间：" + item.Addtime);
            return convertView;
        }

    }

    @ItemClick(android.R.id.list)
    void onItemClick(Custom item) {
        CustomInfoActivity_.intent(this).customItem(item).start();
    }

    @OnActivityResult(ADD_REQUEST_CODE)
    void addOnResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            reSearch();
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
        } else {
            super.onBackPressed();
        }
    }

    private void hideKeyBord(View v) {
        InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
