package com.yunfa365.lawservice.app.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.pojo.base.CommonItem;
import com.yunfa365.lawservice.app.ui.view.holder.CommonFooterViewHolder;
import com.yunfa365.lawservice.app.ui.view.holder.CommonItemViewHolder;
import com.yunfa365.lawservice.app.ui.view.holder.EmptyViewHolder;

import java.util.ArrayList;
import java.util.List;

public abstract class CommonListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    protected static final int ITEM_TYPE_HEADER = 0;
    protected static final int ITEM_TYPE_CONTENT = 1;
    protected static final int ITEM_TYPE_FOOTER = 2;
    protected static final int ITEM_TYPE_EMPTY = 3;

    protected int mHeaderCount = 0;
    protected int mFooterCount = 1;
    protected boolean hasDetail = true;
    protected Context mContext;
    public List mData = new ArrayList();

    public CommonListAdapter(Context context) {
        mContext = context;
    }

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
            return getFooterViewHolder();
        } else if (viewType == ITEM_TYPE_CONTENT){
            CommonItemViewHolder viewHolder = CommonItemViewHolder.create(mContext);
            if (hasDetail) {
                viewHolder.itemView.setOnClickListener(this);
            } else {
                viewHolder.detail.setVisibility(View.GONE);
            }
            return viewHolder;
        } else if (viewType == ITEM_TYPE_EMPTY) {
            View view = View.inflate(mContext, R.layout.common_list_item_empty, null);
            return new EmptyViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CommonItemViewHolder) {
            CommonItemViewHolder itemViewHolder = (CommonItemViewHolder) holder;
            CommonItem item = getItem(position);
            itemViewHolder.bindPojo(item);
        } else if (holder instanceof CommonFooterViewHolder) {
            CommonFooterViewHolder footerViewHolder = (CommonFooterViewHolder) holder;
            footerViewHolder.refrash();
        }
    }

    @Override
    public int getItemCount() {
        if (isEmpty())
            return 1;
        else
            return mHeaderCount + getContentItemCount() + getFooterCount();
    }

    private CommonItem getItem(int position) {
        return (CommonItem) mData.get(position - mHeaderCount);
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

    }

    protected abstract CommonFooterViewHolder getFooterViewHolder();
}
