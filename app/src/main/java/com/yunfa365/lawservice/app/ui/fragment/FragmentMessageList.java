package com.yunfa365.lawservice.app.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyManager;
import com.android.agnetty.core.AgnettyResult;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.OrderInfo;
import com.yunfa365.lawservice.app.pojo.PushMessage;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.law_case.CaseDetailActivity_;
import com.yunfa365.lawservice.app.ui.adapter.CommonListAdapter;
import com.yunfa365.lawservice.app.ui.fragment.base.BaseFragment;
import com.yunfa365.lawservice.app.ui.view.holder.CommonFooterViewHolder;
import com.yunfa365.lawservice.app.ui.view.holder.CommonItemViewHolder;
import com.yunfa365.lawservice.app.utils.ScreenUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import java.util.List;

@EFragment(R.layout.fragment_message_list)
class FragmentMessageList extends BaseFragment {
    private String FUTURE_TAG = "custom_order_list_";

    @ViewById
    SwipeRefreshLayout swipeRefreshLayout;
    private SwipeRefreshLayout _swipeRefreshLayout;

    @ViewById
    RecyclerView listView;
    private MyAdapter mAdapter;
    private int mPage = 0;
    private CommonFooterViewHolder mFooterViewHolder;

    @FragmentArg
    String Cols;

    // 是否正在加载
    private int mLoadingStatus = 0; // 0:未加载 1：正在加载 2：加载失败
    //首次进入
    private boolean mFirstEnter = true;
    private boolean reloadData = false;
    private Activity mActivity;

    @Override
    public void onResume() {
        super.onResume();
        if (reloadData) {
            reloadData = false;

            if (mLoadingStatus == 1) {
                AgnettyManager manager = AgnettyManager.getInstance(mActivity);
                manager.cancelFutureByTag(FUTURE_TAG);
            }
            reLoadData();
        }
    }

    @Override
    public void onDestroy() {
        mActivity = null;
        _contentView_ = null;
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (_contentView_ == null) {
            _contentView_ = inflater.inflate(R.layout.fragment_message_list, container, false);
        }
        ViewGroup parent = (ViewGroup) _contentView_.getParent();
        if (parent != null) {
            parent.removeView(_contentView_);
        }

        return _contentView_;
    }

    @AfterViews
    public void init() {
        if (_contentView_.getTag() != null) {
            return;
        }
        _contentView_.setTag(new Object());
        mActivity = getActivity();
        FUTURE_TAG += Cols;

        _swipeRefreshLayout = swipeRefreshLayout;
        _swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#FF4081"), Color.parseColor("#303F9F"));

        mFooterViewHolder = CommonFooterViewHolder.create(mActivity, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reLoadData();
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        listView.addItemDecoration(new DividerItemDecoration(8));
        listView.setLayoutManager(layoutManager);
        mAdapter = new MyAdapter(mActivity);
        listView.setAdapter(mAdapter);

        initPullRefresh();
        setOnLoadingListener();
        loadData();
    }


    private void loadData() {
        mFooterViewHolder.setLoadingStart();
        final int loadPage = mPage + 1;
        AppRequest request = new AppRequest.Build("api/WebSet/Mess_list")
                .addParam("Cols", Cols)
                .addParam("PageIndex", loadPage + "")
                .create();
        new HttpFormFuture.Builder(mActivity)
                .setTag(FUTURE_TAG)
                .setData(request)
                .setListener(new AgnettyFutureListener(){
                    @Override
                    public void onComplete(AgnettyResult result) {
                        _swipeRefreshLayout.setRefreshing(false);
                        AppResponse resp = (AppResponse)result.getAttach();
                        if (resp.flag) {
                            List<PushMessage> data = resp.resultsToList(PushMessage.class);
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
                        _swipeRefreshLayout.setRefreshing(false);
                        mFooterViewHolder.setLoadingError();
                    }
                })
                .execute();
    }

    private void reLoadData() {
        mPage = 0;
        loadData();
    }

    private void initPullRefresh() {
        _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mLoadingStatus == 1) {
                    AgnettyManager manager = AgnettyManager.getInstance(mActivity);
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
                    if (mLoadingStatus == 0 && !mFirstEnter) {
                        if (mPage > -1) {
                            loadData();
                        }
                    }
                    if (mFirstEnter) {
                        mFirstEnter = false;
                    }
                }
            }
        });
    }

    class MyAdapter extends CommonListAdapter {

        public MyAdapter(Context context) {
            super(context);
            mHeaderCount = 0;
            mFooterCount = 1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == ITEM_TYPE_CONTENT) {
                View view = View.inflate(mActivity, R.layout.message_list_item, null);
                view.setOnClickListener(this);
                return new CommonItemViewHolder(view);
            }
            return super.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onClick(View v) {
            Object obj = v.getTag();
            if (obj != null) {
                PushMessage item = (PushMessage) obj;
                if (item.WxUrl.startsWith("pages/Case/des/des")) {
                    gotoCaseDetail(item.CaseId);
                }
            }
        }

        @Override
        protected CommonFooterViewHolder getFooterViewHolder() {
            return mFooterViewHolder;
        }
    }

    private void gotoCaseDetail(int ID) {
        CaseDetailActivity_.intent(this).ID(ID).start();
    }

    public class DividerItemDecoration extends RecyclerView.ItemDecoration {
        private int dividerHeight;

        public DividerItemDecoration(int dividerHeight) {
            this.dividerHeight = ScreenUtil.dip2px(dividerHeight);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int position = parent.getChildAdapterPosition(view);
            outRect.top = dividerHeight;
        }

    }

}
