package com.yunfa365.lawservice.app.ui.activity.seal;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyResult;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.constant.AppCst;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.BhSeal;
import com.yunfa365.lawservice.app.pojo.OfficialRecord;
import com.yunfa365.lawservice.app.pojo.PageInfo;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@EActivity(R.layout.activity_start_seal)
public class StartSealActivity extends BaseUserActivity {
    private final int REQUEST_CODE_SEAL_CONNECT = 1;

    @ViewById(R.id.base_id_back)
    View mBackView;

    @ViewById(R.id.base_id_title)
    TextView mTitleTxt;

    @ViewById(R.id.base_right_btn)
    ImageView mRightImage;

    @ViewById(R.id.base_right_txt)
    TextView mRightTxt;

    @ViewById
    RecyclerView listView;
    BaseQuickAdapter mAdapter;
    private PageInfo pageInfo = new PageInfo();
    private OfficialRecord mSelectedItem;

    @AfterViews
    void init() {
        mBackView.setVisibility(View.VISIBLE);
        mBackView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleTxt.setText("启动盖章");

        mAdapter = new MyAdapter();
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                OfficialRecord item = (OfficialRecord) mAdapter.getItem(position);
                if (item.WGNums > 0) {
                    gotoScanSeal(item);
                } else {
                    showToast("没有可用的盖章次数");
                }
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(layoutManager);
        listView.setAdapter(mAdapter);
        initLoadMore();
        loadData();
    }

    private void gotoScanSeal(OfficialRecord item) {
        mSelectedItem = item;
        ScanSealActivity_.intent(this).action(2).startForResult(REQUEST_CODE_SEAL_CONNECT);
    }

    @OnActivityResult(REQUEST_CODE_SEAL_CONNECT)
    void sealConnectOnResult(int result, Intent data) {
        if (result == RESULT_OK) {
            BhSeal sealItem = (BhSeal) data.getSerializableExtra("sealItem");
            SealConnectActivity_.intent(this).officialItem(mSelectedItem)
                    .sealItem(sealItem).start();
        }
    }

    private void initLoadMore() {
        mAdapter.getLoadMoreModule().setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadMore();
            }
        });
        mAdapter.getLoadMoreModule().setAutoLoadMore(true);
        //当自动加载开启，同时数据不满一屏时，是否继续执行自动加载更多(默认为true)
        mAdapter.getLoadMoreModule().setEnableLoadMoreIfNotFullPage(false);
    }

    private void loadMore() {
        loadData();
    }

    private void reLoadData() {
        mAdapter.setEmptyView(R.layout.loading_view);
        loadData();
    }

    private void loadData() {
        int loadPage = pageInfo.page + 1;
        AppRequest request = new AppRequest.Build("api/official/list_gz")
                .addParam("PageIndex", loadPage + "")
                .create();
        new HttpFormFuture.Builder(this)
                .setData(request)
                .setListener(new AgnettyFutureListener(){

                    @Override
                    public void onComplete(AgnettyResult result) {
                        mAdapter.getLoadMoreModule().setEnableLoadMore(true);

                        AppResponse resp = (AppResponse)result.getAttach();
                        if (resp.flag) {
                            List<OfficialRecord> list = resp.resultsToList(OfficialRecord.class);
                            if (pageInfo.isFirstPage()) {
                                mAdapter.setList(list);
                            } else {
                                mAdapter.addData(list);
                            }

                            if (list.size() < AppCst.PAGE_SIZE) {
                                mAdapter.getLoadMoreModule().loadMoreEnd();
                            } else {
                                mAdapter.getLoadMoreModule().loadMoreComplete();
                            }
                            pageInfo.nextPage();
                        } else {
                            showToast(resp.Message);
                            if (pageInfo.isFirstPage()) {
                                mAdapter.setEmptyView(getEmptyDataView());
                            }
                        }
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        hideLoading();
                        result.getException().printStackTrace();
                    }
                })
                .execute();
    }

    private View getEmptyDataView() {
        View notDataView = getLayoutInflater().inflate(R.layout.empty_view, listView, false);
        notDataView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reLoadData();
            }
        });
        return notDataView;
    }

    class MyAdapter extends BaseQuickAdapter<OfficialRecord, BaseViewHolder> implements LoadMoreModule {
        public MyAdapter() {
            super(R.layout.start_seal_list_item);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder holder, OfficialRecord o) {
            holder.setText(R.id.title, o.getTitle());
            holder.setText(R.id.desc, o.getDesc());
            holder.setText(R.id.status, o.getStatus());
        }
    }
}
