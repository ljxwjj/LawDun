package com.yunfa365.lawservice.app.ui.activity.law_case;

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
import com.yunfa365.lawservice.app.constant.AppCst;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.Case;
import com.yunfa365.lawservice.app.pojo.FieldItem;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.base.DrawerActivity;
import com.yunfa365.lawservice.app.ui.activity.office.Office41_1Activity_;
import com.yunfa365.lawservice.app.ui.activity.office.Office41_3Activity_;
import com.yunfa365.lawservice.app.ui.activity.office.Office41_4Activity_;
import com.yunfa365.lawservice.app.ui.activity.office.Office41_5Activity_;
import com.yunfa365.lawservice.app.ui.adapter.CommonListAdapter;
import com.yunfa365.lawservice.app.ui.dialog.BottomMenuDialog;
import com.yunfa365.lawservice.app.ui.view.holder.CommonFooterViewHolder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import static com.yunfa365.lawservice.app.constant.BaseCst.DETAIL_REQUEST_CODE;

@EActivity(R.layout.common_search_list)
public class CaseListMyActivity extends DrawerActivity {
    private String FUTURE_TAG = "custom_list";

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
    private String keyword6 = "";

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
        mTitleTxt.setText("我的案件");

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

        AppRequest request = new AppRequest.Build("api/Case/list_My")
                .addParam("CaseIdTxt", keyword1)
                .addParam("CustName", keyword2)
                .addParam("DCustName", keyword3)
                .addParam("BegTime", keyword4)
                .addParam("EndTime", keyword5)
                .addParam("Stat", keyword6)
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
                            List<Case> data = resp.resultsToList(Case.class);
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
        if (params != null && params.length == 6) {
            keyword1 = params[0];
            keyword2 = params[1];
            keyword3 = params[2];
            keyword4 = params[3];
            keyword5 = params[4];
            keyword6 = params[5];
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
                    AgnettyManager manager = AgnettyManager.getInstance(CaseListMyActivity.this);
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
                Case item = (Case) obj;
                showActionMenu(item);
            }
        }

        public CommonFooterViewHolder getFooterViewHolder() {
            return mFooterViewHolder;
        }

    }

    private void showActionMenu(Case item) {
        final FieldItem items[] = {new FieldItem(1, "详情"), new FieldItem(2, "修改")};
        new BottomMenuDialog(this, items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        gotoDetail(item);
                        break;
                    case 1:
                        loadCaseDetail(item);
                        break;
                }
            }
        }).show();
    }

    private void gotoDetail(Case item) {
        CaseDetailActivity_.intent(this).ID(item.ID).startForResult(DETAIL_REQUEST_CODE);
    }

    private void gotoEdit(Case item) {
        if ("ms".equalsIgnoreCase(item.TempCols)) {
            Office41_1Activity_.intent(this).caseItem(item).startForResult(AppCst.EDIT_REQUEST_CODE);
        } else if ("fs".equals(item.TempCols)) {
            Office41_4Activity_.intent(this).caseItem(item).startForResult(AppCst.EDIT_REQUEST_CODE);
        } else if ("gw".equals(item.TempCols)) {
            Office41_3Activity_.intent(this).caseItem(item).startForResult(AppCst.EDIT_REQUEST_CODE);
        } else if ("ds".equals(item.TempCols)) {
            Office41_5Activity_.intent(this).caseItem(item).startForResult(AppCst.EDIT_REQUEST_CODE);
        }
    }

    private void loadCaseDetail(Case item) {
        showLoading();
        AppRequest request = new AppRequest.Build("api/Case/Des_Get")
                .addParam("CaseId", item.ID + "")
                .addParam("GetType", "2") //获取类型，1：获取拼接好的键值对，2：获取数据库源数据；不传值默认为1
                .create();
        new HttpFormFuture.Builder(this)
                .setData(request)
                .setListener(new AgnettyFutureListener(){

                    @Override
                    public void onComplete(AgnettyResult result) {
                        hideLoading();
                        AppResponse resp = (AppResponse)result.getAttach();
                        if (resp.flag) {
                            Case item = resp.getFirstObject(Case.class);
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

    @OnActivityResult(AppCst.EDIT_REQUEST_CODE)
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
