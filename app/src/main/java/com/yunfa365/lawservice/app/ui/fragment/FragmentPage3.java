package com.yunfa365.lawservice.app.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyResult;
import com.android.agnetty.utils.LogUtil;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.AppGlobal;
import com.yunfa365.lawservice.app.pojo.BannerInfo;
import com.yunfa365.lawservice.app.pojo.GridItem;
import com.yunfa365.lawservice.app.pojo.event.UserRoleEvent;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.HomeActivity;
import com.yunfa365.lawservice.app.ui.activity.auditing.AuditedListActivity_;
import com.yunfa365.lawservice.app.ui.activity.auditing.AuditingListActivity_;
import com.yunfa365.lawservice.app.ui.activity.custom.CustomListActivity_;
import com.yunfa365.lawservice.app.ui.activity.custom.DfCustomListActivity_;
import com.yunfa365.lawservice.app.ui.activity.finance.BillListAllActivity_;
import com.yunfa365.lawservice.app.ui.activity.finance.BillListMyActivity_;
import com.yunfa365.lawservice.app.ui.activity.law_case.CaseListAllActivity_;
import com.yunfa365.lawservice.app.ui.activity.law_case.CaseListMyActivity_;
import com.yunfa365.lawservice.app.ui.activity.office.ChongTuActivity_;
import com.yunfa365.lawservice.app.ui.activity.office.Office41Activity_;
import com.yunfa365.lawservice.app.ui.activity.official.OfficialAddActivity_;
import com.yunfa365.lawservice.app.ui.activity.official.OfficialListAllActivity_;
import com.yunfa365.lawservice.app.ui.activity.official.OfficialListMyActivity_;
import com.yunfa365.lawservice.app.ui.activity.seal.ScanSealActivity_;
import com.yunfa365.lawservice.app.ui.activity.seal.SealManagerActivity_;
import com.yunfa365.lawservice.app.ui.activity.seal.StartSealActivity_;
import com.yunfa365.lawservice.app.ui.activity.user.UserBindSealActivity_;
import com.yunfa365.lawservice.app.ui.adapter.BannerAdapter;
import com.yunfa365.lawservice.app.ui.fragment.base.BaseFragment;
import com.yunfa365.lawservice.app.ui.view.loopview.BaseViewPager;
import com.yunfa365.lawservice.app.ui.view.loopview.LoopPageIndicator;
import com.yunfa365.lawservice.app.ui.view.loopview.LoopViewPager;
import com.yunfa365.lawservice.app.ui.view.loopview.LoopViewPagerAdapter;
import com.yunfa365.lawservice.app.utils.FileUtil;
import com.yunfa365.lawservice.app.utils.ScreenUtil;
import com.yunfa365.lawservice.app.utils.SpUtil;
import com.yunfa365.lawservice.app.utils.StringUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.fragment_page3)
class FragmentPage3 extends BaseFragment {

    @ViewById(R.id.base_id_title)
    TextView mTitleTxt;

    @ViewById
    LoopViewPager loopViewPager;

    @ViewById
    LoopPageIndicator loopViewIndicator;

    @ViewById
    RecyclerView listView;
    private MyAdapter mAdapter;
    private List<GridItem> allOffices = new ArrayList<>();
    private List<GridItem> mGridItems = new ArrayList<>();

    private HomeActivity mActivity;
    private LoopViewPagerAdapter mLoopPagerAdapter;
    private List<BannerInfo> mBannerInfo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (_contentView_ == null) {
            _contentView_ = inflater.inflate(R.layout.fragment_page3, container, false);
        }
        ViewGroup parent = (ViewGroup) _contentView_.getParent();
        if (parent != null) {
            parent.removeView(_contentView_);
        }
        return _contentView_;
    }

    @Override
    public void onResume() {
        super.onResume();
        loopViewPager.startScroll();
    }

    @Override
    public void onPause() {
        super.onPause();
        loopViewPager.cancelScroll();
    }

    @Override
    public void onDestroy() {
        mActivity = null;
        _contentView_ = null;
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onEvent(UserRoleEvent event) {
        mActivity.hideLoading();
        if (AppGlobal.mUser.mRole != null) {
            loadGridView();
        } else if (event.state == -1) {
            reloadDialog();
        }
    }

    private void reloadDialog() {
        new AlertDialog.Builder(mActivity).setMessage("数据加载失败，是否重试?")
                .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppGlobal.mUser.loadUserRole(mActivity);
                        mActivity.showLoading();
                    }
                }).setNegativeButton("算了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mActivity.finish();
            }
        }).show();
    }

    @AfterViews
    void init(){
        if (_contentView_.getTag() != null) {
            return;
        }
        _contentView_.setTag(new Object());
        String title = AppGlobal.mUser.FullName;
        if (TextUtils.isEmpty(title)) {
            title = "e律师";
        }
        mTitleTxt.setText(title);
        mActivity = (HomeActivity)getActivity();

        String homeBannerInfo = SpUtil.getHomeBannerInfo(mActivity);
        if (TextUtils.isEmpty(homeBannerInfo)) {
            mBannerInfo = new ArrayList<>();
            mBannerInfo.add(new BannerInfo(R.mipmap.s3));
            mBannerInfo.add(new BannerInfo(R.mipmap.s1));
            mBannerInfo.add(new BannerInfo(R.mipmap.s2));
        } else {
            mBannerInfo = StringUtil.toObjectList(homeBannerInfo, BannerInfo.class);
        }

        BannerAdapter bannerAdapter = new BannerAdapter(mActivity, mBannerInfo);
        mLoopPagerAdapter = new LoopViewPagerAdapter(bannerAdapter);
        loopViewPager.setInfinateAdapter(mLoopPagerAdapter);
        loopViewPager.setOnPageChangeListener(new BannerPageChangeListener(mBannerInfo.size()));

        loopViewIndicator.initIndicator(0,
                mBannerInfo.size(),
                R.mipmap.indicator_dot_normal,
                R.mipmap.indicator_dot_focus);

        String offices = FileUtil.getRawFileContent(getResources(), R.raw.offices);
        allOffices = StringUtil.toObjectList(offices, GridItem.class);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new GridLayoutManager(mActivity, 4));
        listView.addItemDecoration(new DividerItemDecoration(0.3f));
        mAdapter = new MyAdapter();
        listView.setAdapter(mAdapter);

        if (AppGlobal.mUser.mRole != null) {
            loadGridView();
        } else if (AppGlobal.mUser.isOnLoadUserRole()) {
            mActivity.showLoading();
        } else {
            LogUtil.d("用户权限请求失败");
            reloadDialog();
        }
        loadBannerList();
    }

    private void loadGridView() {
        mGridItems.clear();
        for (GridItem item : allOffices) {
            if (item.pid > 0 && !AppGlobal.mUser.mRole.checkRole(item.pid)) {
                continue;
            }
            mGridItems.add(item);
        }
        mAdapter.notifyDataSetChanged();
    }

    private class BannerPageChangeListener implements BaseViewPager.OnPageChangeListener {
        //
        public static final int SCROLL_STATE_IDLE = 0;
        //
        public static final int SCROLL_STATE_DRAGGING = 1;
        //
        public static final int SCROLL_STATE_SETTLING = 2;

        private int mSize;

        public BannerPageChangeListener(int size) {
            this.mSize = size;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            switch (state) {
                case SCROLL_STATE_IDLE:
                    break;

                case SCROLL_STATE_DRAGGING:
                    break;

                case SCROLL_STATE_SETTLING:
                    break;
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            int index = position % mSize;
            loopViewIndicator.refreshIndicator(index);
        }
    }

    private void onItemClick(GridItem item) {
        switch (item.id) {
            case 1:
                ChongTuActivity_.intent(this).start();
                break;
            case 2:
                Office41Activity_.intent(this).start();
                break;
            case 4:
                CaseListMyActivity_.intent(this).start();
                break;
            case 5:
                CustomListActivity_.intent(this).start();
                break;
            case 6:
                DfCustomListActivity_.intent(this).start();
                break;
            case 7:
                OfficialAddActivity_.intent(this).start();
                break;
            case 8:
                OfficialListMyActivity_.intent(this).start();
                break;
            case 9:
                StartSealActivity_.intent(this).start();
                break;
            case 14:
                BillListMyActivity_.intent(this).isDone(false).start();
                break;
            case 15:
                BillListMyActivity_.intent(this).isDone(true).start();
                break;
            case 16:
                ScanSealActivity_.intent(this).action(2).start();
                break;
            case 18:
                ScanSealActivity_.intent(this).action(1).start();
                break;
            case 19:
                SealManagerActivity_.intent(this).defaultTab(1).start();
                break;
            case 20:
                UserBindSealActivity_.intent(this).start();
                break;
            case 21:
                SealManagerActivity_.intent(this).start();
                break;
            case 9991:
                AuditingListActivity_.intent(this).start();
                break;
            case 9992:
                AuditedListActivity_.intent(this).start();
                break;
            case 9993:
                BillListAllActivity_.intent(this).isDone(false).start();
                break;
            case 9994:
                CaseListAllActivity_.intent(this).start();
                break;
            case 9995:
                OfficialListAllActivity_.intent(this).start();
                break;
            case 9996:
                BillListAllActivity_.intent(this).isDone(true).start();
                break;
            default:
                break;
        }
    }

    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
        static final int ITEM_TYPE_HEADER = 0;
        static final int ITEM_TYPE_CONTENT = 1;
        static final int ITEM_TYPE_FOOTER = 2;

        private int mHeaderCount = 0;
        private int mFooterCount = 0;

        public int getContentItemCount() {
            return mGridItems.size();
        }

        public int getFooterCount() {
            int itemCount = getContentItemCount();
            int lastRowCount = itemCount%4;
            mFooterCount = lastRowCount == 0? 0 : (4-lastRowCount);
            return mFooterCount;
        }

        public boolean isHeaderView(int position) {
            return mHeaderCount > 0 && position < mHeaderCount;
        }

        public boolean isFooterView(int position) {
            return mFooterCount > 0 && position >= (mHeaderCount + getContentItemCount());
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == ITEM_TYPE_HEADER) {
                return new HeaderViewHolder(null);
            } else if (viewType == ITEM_TYPE_FOOTER) {
                View view = View.inflate(mActivity, R.layout.home_grid_empty, null);
                return new FooterViewHolder(view);
            } else if (viewType == ITEM_TYPE_CONTENT){
                View view = View.inflate(mActivity, R.layout.home_grid_item, null);
                view.setOnClickListener(this);
                ItemViewHolder viewHolder = new ItemViewHolder(view);
                return viewHolder;
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ItemViewHolder) {
                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                GridItem item = getItem(position);
                itemViewHolder.textView.setText(item.name);
                itemViewHolder.itemView.setTag(item);

                String image = StringUtil.unescape(item.image);
                itemViewHolder.imageView.setText(image);
            }
        }

        @Override
        public int getItemCount() {
            return mHeaderCount + getContentItemCount() + getFooterCount();
        }

        private GridItem getItem(int position) {
            return mGridItems.get(position - mHeaderCount);
        }

        @Override
        public int getItemViewType(int position) {
            if (isHeaderView(position)) {
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
                GridItem item = (GridItem) obj;
                onItemClick(item);
            }
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView imageView;
        TextView textView;
        ImageView flagView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            textView = itemView.findViewById(R.id.text);
            flagView = itemView.findViewById(R.id.flag);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class DividerItemDecoration extends RecyclerView.ItemDecoration {
        private int dividerHeight;

        public DividerItemDecoration(float dividerHeight) {
            this.dividerHeight = ScreenUtil.dip2px(dividerHeight);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int position = parent.getChildAdapterPosition(view);
            if (position%4 > 0) {
                outRect.left = dividerHeight;
            }
            outRect.bottom = dividerHeight;//类似加了一个bottom padding
        }
    }


    private void loadBannerList() {
        AppRequest request = new AppRequest.Build("Login/GetBannerList")
                .create();
        new HttpFormFuture.Builder(mActivity)
                .setData(request)
                .setListener(new AgnettyFutureListener(){
                    @Override
                    public void onComplete(AgnettyResult result) {
                        AppResponse resp = (AppResponse)result.getAttach();
                        if (resp.flag) {
                            SpUtil.setHomeBannerInfo(mActivity, resp.RData);

                            List mInfos = resp.resultsToList(BannerInfo.class);
                            mBannerInfo.clear();
                            mBannerInfo.addAll(mInfos);
                            mLoopPagerAdapter.notifyDataSetChanged();
                            int curPage = loopViewPager.getCurrentItem() % mLoopPagerAdapter.getRealCount();
                            loopViewIndicator.initIndicator(curPage,
                                    mBannerInfo.size(),
                                    R.mipmap.indicator_dot_normal,
                                    R.mipmap.indicator_dot_focus);
                        }
                    }
                })
                .execute();
    }
}
