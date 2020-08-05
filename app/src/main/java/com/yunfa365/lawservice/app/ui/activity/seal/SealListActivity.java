package com.yunfa365.lawservice.app.ui.activity.seal;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyResult;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.BhSeal;
import com.yunfa365.lawservice.app.pojo.FieldItem;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;
import com.yunfa365.lawservice.app.ui.dialog.BottomMenuDialog;
import com.yunfa365.lawservice.app.ui.view.holder.EmptyViewHolder;
import com.yunfa365.lawservice.app.utils.ScreenUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.common_list_activity)
public class SealListActivity extends BaseUserActivity {

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
    MyAdapter mAdapter;
    List<BhSeal> mData;

    @AfterViews
    void init(){
        mBackView.setVisibility(View.VISIBLE);
        mBackView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleTxt.setText("印章管理");

        mData = new ArrayList();
        mAdapter = new MyAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listView.addItemDecoration(new DividerItemDecoration(1));
        listView.setLayoutManager(layoutManager);
        listView.setAdapter(mAdapter);

        loadData();
    }

    private void loadData() {
        AppRequest request = new AppRequest.Build("api/WebSet/Zhang_list")
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
                            mData = resp.resultsToList(BhSeal.class);
                            if (mData == null) {
                                mData = new ArrayList<>();
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            showToast(resp.Message);
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

    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        static final int ITEM_TYPE_HEADER = 0;
        static final int ITEM_TYPE_CONTENT = 1;
        static final int ITEM_TYPE_FOOTER = 2;
        static final int ITEM_TYPE_EMPTY = 3;

        private int mHeaderCount = 0;
        private int mFooterCount = 0;

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
                return null;
            } else if (viewType == ITEM_TYPE_FOOTER) {
                return null;
            } else if (viewType == ITEM_TYPE_CONTENT){
                View view = View.inflate(SealListActivity.this, R.layout.item_seal_list, null);
                ContentViewHolder viewHolder = new ContentViewHolder(view);
                return viewHolder;
            } else if (viewType == ITEM_TYPE_EMPTY) {
                View view = View.inflate(SealListActivity.this, R.layout.common_list_item_empty, null);
                return new EmptyViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ContentViewHolder) {
                BhSeal item = getItem(position);
                ContentViewHolder itemViewHolder = (ContentViewHolder) holder;
                itemViewHolder.text.setText(item.ZTitle);
                itemViewHolder.item = item;
            }
        }

        @Override
        public int getItemCount() {
            return mHeaderCount + getContentItemCount() + getFooterCount();
        }

        private BhSeal getItem(int position) {
            return mData.get(position - mHeaderCount);
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
    }

    class ContentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView text;
        BhSeal item;

        public ContentViewHolder(View itemView) {
            super(itemView);
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(params);
            text = itemView.findViewById(R.id.text);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            showMenuDialog(item);
        }
    }

    private void showMenuDialog(BhSeal item) {
        final FieldItem items[] = {new FieldItem(1, "删除")};
        new BottomMenuDialog(this, items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    doDeleteSeal(item);
                }
            }
        }).show();
    }

    private void doDeleteSeal(BhSeal item) {
        AppRequest request = new AppRequest.Build("api/WebSet/Zhang_Delete")
                .addParam("Zid", item.ID + "")
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
                            showToast(resp.Message);
                            int index = mData.indexOf(item);
                            if (index != -1) {
                                mData.remove(index);
                                mAdapter.notifyItemRemoved(index);
                            }
                        } else {
                            showToast(resp.Message);
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

    private class DividerItemDecoration extends RecyclerView.ItemDecoration {
        private int dividerHeight;
        private Drawable mDivider;

        public DividerItemDecoration(float dividerHeight) {
            this.dividerHeight = ScreenUtil.dip2px(dividerHeight);
            mDivider = getResources().getDrawable(R.color.status_color);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int position = parent.getChildAdapterPosition(view);
            if (mAdapter.isHeaderView(position)) {

            } else if (mAdapter.isFooterView(position)) {

            } else {
                outRect.bottom = dividerHeight;//类似加了一个bottom padding
            }
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            final int left = 0;
            final int right = parent.getWidth() - left;

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final int top = child.getBottom();

                if (mAdapter.isHeaderView(i)) {

                } else if (mAdapter.isFooterView(i)) {

                } else {
                    mDivider.setBounds(left, top, right, top+dividerHeight);
                    mDivider.draw(c);
                }
            }
        }
    }
}
