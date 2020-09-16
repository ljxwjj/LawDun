package com.yunfa365.lawservice.app.ui.activity.joblog;

import android.content.Context;
import android.content.DialogInterface;
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
import com.yunfa365.lawservice.app.pojo.FieldItem;
import com.yunfa365.lawservice.app.pojo.JobLog;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.base.DrawerActivity;
import com.yunfa365.lawservice.app.ui.adapter.CommonListAdapter;
import com.yunfa365.lawservice.app.ui.dialog.BottomMenuDialog;
import com.yunfa365.lawservice.app.ui.view.holder.CommonFooterViewHolder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import static com.yunfa365.lawservice.app.constant.BaseCst.DETAIL_REQUEST_CODE;
import static com.yunfa365.lawservice.app.constant.BaseCst.EDIT_REQUEST_CODE;

@EActivity(R.layout.common_search_list)
public class JoblogListActivity extends DrawerActivity {
    private String FUTURE_TAG = "joblog_list";

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
        mTitleTxt.setText("工作日志");

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

        AppRequest request = new AppRequest.Build("api/JobLog/list_My")
                .addParam("CaseIdTxt", keyword1)
                .addParam("CustName", keyword2)
                .addParam("BegTime", keyword3)
                .addParam("EndTime", keyword4)
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
                            List<JobLog> data = resp.resultsToList(JobLog.class);
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
        if (params != null && params.length == 4) {
            keyword1 = params[0];
            keyword2 = params[1];
            keyword3 = params[2];
            keyword4 = params[3];
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
                    AgnettyManager manager = AgnettyManager.getInstance(JoblogListActivity.this);
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
                JobLog item = (JobLog) obj;
                showActionMenu(item);
            }
        }

        public CommonFooterViewHolder getFooterViewHolder() {
            return mFooterViewHolder;
        }
    }

    private void showActionMenu(JobLog item) {
        final FieldItem items[] = {new FieldItem(1, "详情"), new FieldItem(2, "修改")};
        new BottomMenuDialog(this, items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        gotoDetail(item);
                        break;
                    case 1:
                        loadItemDetail(item);
                        break;
                }
            }
        }).show();
    }

    private void gotoDetail(JobLog item) {
        JobLogDetailActivity_.intent(this).ID(item.ID).startForResult(DETAIL_REQUEST_CODE);
    }

    private void gotoEdit(JobLog item) {
        JoblogAddActivity_.intent(this).joblogItem(item).startForResult(EDIT_REQUEST_CODE);
    }

    private void loadItemDetail(JobLog item) {
        showLoading();
        AppRequest request = new AppRequest.Build("api/JobLog/Des_Get")
                .addParam("Jid", item.ID + "")
                .create();
        new HttpFormFuture.Builder(this)
                .setData(request)
                .setListener(new AgnettyFutureListener(){

                    @Override
                    public void onComplete(AgnettyResult result) {
                        hideLoading();
                        AppResponse resp = (AppResponse)result.getAttach();
                        if (resp.flag) {
                            JobLog item = resp.getFirstObject(JobLog.class);
                            gotoEdit(item);
                        } else {
                            showToast(resp.Message);
                        }
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        hideLoading();
                    }
                })
                .execute();
    }

    @OnActivityResult(EDIT_REQUEST_CODE)
    void editOnResult(int resultCode, Intent data) {
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
