package com.yunfa365.lawservice.app.ui.activity.official;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyManager;
import com.android.agnetty.core.AgnettyResult;
import com.android.agnetty.utils.LogUtil;
import com.nineoldandroids.view.ViewHelper;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.Bill;
import com.yunfa365.lawservice.app.pojo.Custom;
import com.yunfa365.lawservice.app.pojo.OfficialRecord;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.base.DrawerActivity;
import com.yunfa365.lawservice.app.ui.activity.mycase.CustomInfoActivity_;
import com.yunfa365.lawservice.app.ui.activity.office.Office_addCustomActivity_;
import com.yunfa365.lawservice.app.ui.adapter.CommonListAdapter;
import com.yunfa365.lawservice.app.ui.view.holder.CommonFooterViewHolder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EActivity(R.layout.common_search_list)
public class OfficialListAllActivity extends DrawerActivity {
    private String FUTURE_TAG = "custom_list";
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

    @ViewById
    SwipeRefreshLayout swipeRefreshLayout;

    @ViewById
    RecyclerView listView;
    MyAdapter mAdapter;
    private int mPage = 0;

    @ViewById(R.id.id_drawerLayout)
    DrawerLayout mDrawerLayout;

    private MenuRightFragment menuRightFragment;

    private String keyword1 = "";
    private String keyword2 = "";
    private String keyword3 = "";
    private String keyword4 = "";
    private String keyword5 = "";

    private CommonFooterViewHolder mFooterViewHolder;


    @AfterViews
    void init() {

        mBackView.setVisibility(View.VISIBLE);
        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleTxt.setText("全所用单");

        menuRightFragment = MenuRightFragment_.builder().build();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.id_right_menu, menuRightFragment)
                .commit();

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
                menuRightFragment.hideKeyBord();
            }
        });
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
                Gravity.RIGHT);


        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#FF4081"), Color.parseColor("#303F9F"));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(layoutManager);
        mAdapter = new MyAdapter(this);
        listView.setAdapter(mAdapter);
        mFooterViewHolder = CommonFooterViewHolder.create(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });

        initPullRefresh();
        setOnLoadingListener();

        loadData();
    }

    void loadData() {
        LogUtil.d("doSearch page:" + mPage);
        mFooterViewHolder.setLoadingStart();
        final int loadPage = mPage + 1;

        AppRequest request = new AppRequest.Build("api/official/list_All")
                .addParam("BTitle", keyword1)
                .addParam("x", keyword2)
                .addParam("CaseIdTxt", keyword3)
                .addParam("BegTime", keyword4)
                .addParam("EndTime", keyword5)
                .addParam("PageIndex", loadPage+"")
                .create();
        new HttpFormFuture.Builder(this)
                .setTag(FUTURE_TAG)
                .setData(request)
                .setListener(new AgnettyFutureListener(){

                    @Override
                    public void onComplete(AgnettyResult result) {
                        swipeRefreshLayout.setRefreshing(false);
                        AppResponse resp = (AppResponse)result.getAttach();
                        if (resp.flag) {
                            List<OfficialRecord> data = resp.resultsToList(OfficialRecord.class);
                            if (loadPage == 1) {
                                mAdapter.mData.clear();
                                mAdapter.notifyDataSetChanged();
                            }
                            if (data.size() < 10) {
                                mPage = -1;
                                mFooterViewHolder.setLoadingNoMore();
                            } else {
                                mPage = loadPage;
                                mFooterViewHolder.resetLoadingView();
                            }
                            int oldCount = mAdapter.mData.size();
                            mAdapter.mData.addAll(data);
                            mAdapter.notifyItemRangeInserted(oldCount, data.size());
                        } else {
                            mFooterViewHolder.setLoadingError();
                        }
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        swipeRefreshLayout.setRefreshing(false);
                        mFooterViewHolder.setLoadingError();
                    }
                })
                .execute();
    }

    @Override
    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    //String title, String type, String cols, String caseYear, String auditStat
    public void reLoadData(String... params) {
        if (params != null && params.length == 5) {
            keyword1 = params[0];
            keyword2 = params[1];
            keyword3 = params[2];
            keyword4 = params[3];
            keyword5 = params[4];
        }
        mPage = 0;
        mAdapter.mData.clear();
        mAdapter.notifyDataSetChanged();
        loadData();
    }

    private void initPullRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mFooterViewHolder.mLoadingStatus == 1) {
                    AgnettyManager manager = AgnettyManager.getInstance(OfficialListAllActivity.this);
                    manager.cancelFutureByTag(FUTURE_TAG);
                }
                reLoadData();
            }
        });
    }

    private void setOnLoadingListener() {
        listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mPage == -1) return;
                if (!listView.canScrollVertically(1)) {
                    // 首次进入不加载
                    if (mFooterViewHolder.mLoadingStatus == 0 && !mFooterViewHolder.mFirstEnter) {
                        if (mPage > -1) {
                            loadData();
                        }
                    }
                    if (mFooterViewHolder.mFirstEnter) {
                        mFooterViewHolder.mFirstEnter = false;
                    }
                }
            }
        });
    }

    class MyAdapter extends CommonListAdapter {


        public MyAdapter(Context context) {
            super(context);
        }

        public boolean isOnLoading() {
            return mFooterViewHolder.mLoadingStatus == 1;
        }

        public boolean isEmpty() {
            if (isOnLoading()) return false;

            return mData == null || mData.isEmpty();
        }

        @Override
        public void onClick(View v) {
            Object obj = v.getTag();
            if (obj != null) {
                OfficialRecord item = (OfficialRecord) obj;
            }
        }

        public CommonFooterViewHolder getFooterViewHolder() {
            return mFooterViewHolder;
        }

    }

    @OnActivityResult(ADD_REQUEST_CODE)
    void addOnResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            reLoadData();
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

}
