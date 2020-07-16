package com.yunfa365.lawservice.app.ui.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyManager;
import com.android.agnetty.core.AgnettyResult;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.OrderInfo;
import com.yunfa365.lawservice.app.pojo.event.OrderOperationEvent;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.fragment.base.BaseFragment;
import com.yunfa365.lawservice.app.ui.listener.PageListListener;
import com.yunfa365.lawservice.app.ui.view.holder.EmptyViewHolder;
import com.yunfa365.lawservice.app.utils.ScreenUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.common_refresh_list)
class FragmentPage1 extends BaseFragment {
    private String FUTURE_TAG = "custom_order_list_";

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
    private SwipeRefreshLayout _swipeRefreshLayout;

    @ViewById
    RecyclerView listView;
    private MyAdapter mAdapter;
    private List<OrderInfo> mData = new ArrayList<>();
    private int mPage = 0;
    private View mFooterView;
    private FooterViewHolder mFooterViewHolder;

    // 是否正在加载
    private int mLoadingStatus = 0; // 0:未加载 1：正在加载 2：加载失败
    //首次进入
    private boolean mFirstEnter = true;
    private PageListListener mOnLoadingListener;
    private boolean reloadData = false;
    private Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (reloadData) {
            reloadData = false;

            if (mLoadingStatus == 1) {
                AgnettyManager manager = AgnettyManager.getInstance(mActivity);
                manager.cancelFutureByTag(FUTURE_TAG);
            }
            reLoadOrderList();
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
            _contentView_ = inflater.inflate(R.layout.common_refresh_list, container, false);
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
        mTitleTxt.setText("消息中心");

        _swipeRefreshLayout = swipeRefreshLayout;
        _swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#FF4081"), Color.parseColor("#303F9F"));

        mFooterView = createFooterView();
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        listView.addItemDecoration(new DividerItemDecoration(12));
        listView.setLayoutManager(layoutManager);
        mAdapter = new MyAdapter();
        listView.setAdapter(mAdapter);

        initPullRefresh();
        setOnLoadingListener();
        loadOrderList();
    }


    @Subscribe
    public void onEvent(OrderInfo event) {
        for (OrderInfo item : mData) {
            /*if (item.Number.equals(event.Number)) {
                item.Stat = event.Stat;
                mAdapter.notifyDataSetChanged();
                break;
            }*/
        }
    }

    @Subscribe
    public void onEvent(OrderOperationEvent event) {
        reloadData = true;
    }

    private void loadOrderList() {
        setLoadingStart();
        final int loadPage = mPage + 1;
        AppRequest request = new AppRequest.Build("Lawyer/Requires/List.ashx")
                .addParam("Pages", loadPage + "")
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
                            List<OrderInfo> data = resp.resultsToList(OrderInfo.class);
                            if (loadPage == 1) {
                                mData.clear();
                                mAdapter.notifyDataSetChanged();
                            }
                            if (data.size() < 10) {
                                mPage = -1;
                                setLoadingNoMore();
                            } else {
                                mPage = loadPage;
                                resetLoadingView();
                            }
                            int oldCount = mData.size();
                            mData.addAll(data);
                            mAdapter.notifyItemRangeInserted(oldCount, data.size());
                        } else {
                            setLoadingError();
                        }
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        _swipeRefreshLayout.setRefreshing(false);
                        setLoadingError();
                    }
                })
                .execute();
    }

    private View createFooterView() {
        View view = View.inflate(mActivity, R.layout.common_refresh_list_footer, null);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        return view;
    }

    private void reLoadOrderList() {
        mPage = 0;
        loadOrderList();
    }

    private void initPullRefresh() {
        _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mLoadingStatus == 1) {
                    AgnettyManager manager = AgnettyManager.getInstance(mActivity);
                    manager.cancelFutureByTag(FUTURE_TAG);
                }
                reLoadOrderList();
            }
        });
    }

    private void setOnLoadingListener() {
        mOnLoadingListener = new PageListListener() {
            @Override
            public void onLoadingMore() {
                if (mPage > -1) {
                    loadOrderList();
                }
            }
        };
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
                        if (mOnLoadingListener != null) {
                            mOnLoadingListener.onLoadingMore();
                        }
                    }
                    if (mFirstEnter) {
                        mFirstEnter = false;
                    }
                }
            }
        });
    }

    private void setLoadingStart() {
        mLoadingStatus = 1;

        if (mFooterViewHolder != null) {
            Animation circleAnim = AnimationUtils.loadAnimation(mActivity, R.anim.anim_round_rotate);
            LinearInterpolator interpolator = new LinearInterpolator();
            circleAnim.setInterpolator(interpolator);

            mFooterViewHolder.image.startAnimation(circleAnim);
            mFooterViewHolder.image.setVisibility(View.VISIBLE);
            mFooterViewHolder.text.setText("正在加载...");
            mFooterViewHolder.text.setOnClickListener(null);
        }
    }

    private void setLoadingNoMore() {
        mLoadingStatus = -1;
        if (mFooterViewHolder != null) {
            mFooterViewHolder.image.clearAnimation();
            mFooterViewHolder.image.setVisibility(View.GONE);
            mFooterViewHolder.text.setText("没有更多了");
        }
    }

    private void setLoadingError() {
        mLoadingStatus = 2;
        if (mFooterViewHolder != null) {
            mFooterViewHolder.image.clearAnimation();
            mFooterViewHolder.image.setVisibility(View.GONE);
            mFooterViewHolder.text.setText("加载失败，点击重新加载！");
            mFooterViewHolder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadOrderList();
                }
            });
        }
    }

    private void resetLoadingView() {
        mLoadingStatus = 0;
        if (mFooterViewHolder != null) {
            mFooterViewHolder.image.clearAnimation();
            mFooterViewHolder.image.setVisibility(View.VISIBLE);
            mFooterViewHolder.text.setText("正在加载...");
        }
    }

    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
        static final int ITEM_TYPE_HEADER = 0;
        static final int ITEM_TYPE_CONTENT = 1;
        static final int ITEM_TYPE_FOOTER = 2;
        static final int ITEM_TYPE_EMPTY = 3;

        private int mHeaderCount = 0;
        private int mFooterCount = 1;

        public int getContentItemCount() {
            return mData.size();
        }

        public int getFooterCount() {
            return mFooterCount;
        }

        public boolean isHeaderView(int position) {
            return mHeaderCount > 0 && position < mHeaderCount;
        }

        public boolean isFooterView(int position) {
            return mFooterCount > 0 && position >= (mHeaderCount + getContentItemCount());
        }

        public boolean isLastContentView(int position) {
            return position + 1 == mHeaderCount + getContentItemCount();
        }

        public boolean isEmptyView(int position) {
            return isEmpty() && position == 0;
        }

        public boolean isEmpty() {
            return mData == null || mData.isEmpty();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == ITEM_TYPE_HEADER) {
                return new HeaderViewHolder(null);
            } else if (viewType == ITEM_TYPE_FOOTER) {
                mFooterViewHolder = new FooterViewHolder(mFooterView);
                return mFooterViewHolder;
            } else if (viewType == ITEM_TYPE_CONTENT){
                View view = View.inflate(mActivity, R.layout.order_list_item, null);
                view.setOnClickListener(this);
                ItemViewHolder viewHolder = new ItemViewHolder(view);
                viewHolder.detailBtn.setOnClickListener(this);
                return viewHolder;
            } else if (viewType == ITEM_TYPE_EMPTY) {
                View view = View.inflate(mActivity, R.layout.common_list_item_empty, null);
                return new EmptyViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ItemViewHolder) {
                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                OrderInfo item = getItem(position);
//                itemViewHolder.productTitle.setText(item.TypeName);
//                itemViewHolder.statusValue.setText(item.getStatName());
//                itemViewHolder.desText.setText("说明：" + item.Contents);
//                itemViewHolder.remark.setText(item.Remark);
//                itemViewHolder.orderTime.setText(item.AddTime);
//                itemViewHolder.detailBtn.setTag(item);
//                itemViewHolder.itemView.setTag(item);
            } else if (holder instanceof FooterViewHolder) {
                if (mLoadingStatus == -1) {
                    setLoadingNoMore();
                } else if (mLoadingStatus == 0) {
                    resetLoadingView();
                } else if (mLoadingStatus == 1) {
                    setLoadingStart();
                } else if (mLoadingStatus == 2) {
                    setLoadingError();
                }
            }
        }

        @Override
        public int getItemCount() {
            if (isEmpty())
                return 1;
            else
                return mHeaderCount + getContentItemCount() + getFooterCount();
        }

        private OrderInfo getItem(int position) {
            return mData.get(position - mHeaderCount);
        }

        @Override
        public int getItemViewType(int position) {
            if (isEmptyView(position)) {
                return ITEM_TYPE_EMPTY;
            } else if (isHeaderView(position)) {
                return ITEM_TYPE_HEADER;
            } else if (isFooterView(position)) {
                return ITEM_TYPE_FOOTER;
            } else {
                return ITEM_TYPE_CONTENT;
            }
        }

        @Override
        public void onClick(View v) {
            Object obj = v.getTag();
            if (obj != null) {
                OrderInfo item = (OrderInfo) obj;
//                OrderDetailActivity_.intent(mActivity).ID(item.ID).start();
            }
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView productTitle;
        TextView statusValue;
        TextView desText;
        TextView remark;
        TextView orderTime;
        Button detailBtn;

        public ItemViewHolder(View itemView) {
            super(itemView);
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, ScreenUtil.dip2px(130));
            itemView.setLayoutParams(params);

//            productTitle = itemView.findViewById(R.id.productTitle);
//            statusValue = itemView.findViewById(R.id.statusValue);
//            desText = itemView.findViewById(R.id.desText);
//            remark = itemView.findViewById(R.id.remark);
//            orderTime = itemView.findViewById(R.id.orderTime);
//            detailBtn = itemView.findViewById(R.id.detailBtn);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView text;
        public FooterViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
        }
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
            if (position == 0) {
                outRect.top = ScreenUtil.dip2px(2);
            } else if (mAdapter.isHeaderView(position)) {

            } else if (mAdapter.isFooterView(position)) {

            } else {
                outRect.top = dividerHeight;//类似加了一个bottom padding
            }
        }

    }

}
