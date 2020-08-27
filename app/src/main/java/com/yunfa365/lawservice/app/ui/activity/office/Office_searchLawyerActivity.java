package com.yunfa365.lawservice.app.ui.activity.office;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyResult;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.User;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;
import com.yunfa365.lawservice.app.ui.view.holder.CommonFooterViewHolder;
import com.yunfa365.lawservice.app.ui.view.holder.EmptyViewHolder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_office_search_lawyer)
public class Office_searchLawyerActivity extends BaseUserActivity {

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
    RecyclerView listView;
    MyAdapter mAdapter;

    private ArrayList<User> selectedLawyers = new ArrayList<>();


    @AfterViews
    void init(){
        mBackView.setVisibility(View.VISIBLE);
        mBackView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleTxt.setText("选择执业人员");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(layoutManager);
        mAdapter = new MyAdapter(this);
        listView.setAdapter(mAdapter);

        loadData();
    }


    private void loadData() {// 检索所有客户
        AppRequest request = new AppRequest.Build("api/Users/Users_List_Get")
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
                            List<User> data = resp.resultsToList(User.class);

                            mAdapter.mData.addAll(data);
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        hideLoading();
                    }
                })
                .execute();
    }


    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        protected static final int ITEM_TYPE_HEADER = 0;
        protected static final int ITEM_TYPE_CONTENT = 1;
        protected static final int ITEM_TYPE_FOOTER = 2;
        protected static final int ITEM_TYPE_EMPTY = 3;

        protected int mHeaderCount = 0;
        protected int mFooterCount = 0;
        protected Context mContext;
        public List mData = new ArrayList();

        public MyAdapter(Context context) {
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

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == ITEM_TYPE_HEADER) {
                return null;
            } else if (viewType == ITEM_TYPE_FOOTER) {
                return null;
            } else if (viewType == ITEM_TYPE_CONTENT){
                View view = View.inflate(mContext, R.layout.lawyer_list_choice_item, null);
                ItemViewHolder viewHolder = new ItemViewHolder(view);
                return viewHolder;
            } else if (viewType == ITEM_TYPE_EMPTY) {
                View view = View.inflate(mContext, R.layout.common_list_item_empty, null);
                return new EmptyViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ItemViewHolder) {
                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                User item = getItem(position);
                itemViewHolder.checkBox.setText(item.FullName);
                itemViewHolder.item = item;
            } else if (holder instanceof CommonFooterViewHolder) {
                CommonFooterViewHolder footerViewHolder = (CommonFooterViewHolder) holder;
                footerViewHolder.refrash();
            }
        }

        @Override
        public int getItemCount() {
            return mHeaderCount + getContentItemCount() + getFooterCount();
        }

        private User getItem(int position) {
            return (User) mData.get(position);
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

    class ItemViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        CheckBox checkBox;
        User item;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            checkBox.setOnCheckedChangeListener(this);
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(params);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                selectedLawyers.add(item);
            } else {
                selectedLawyers.remove(item);
            }
        }
    }


    @Click(R.id.base_id_title)
    void titleOnClick(View view) {
        Long time = System.currentTimeMillis();
        Object tag = view.getTag();
        if (tag == null) {
            view.setTag(time);
            return;
        }
        Long tagTime = (Long)tag;
        if (time - tagTime < 400) {
            titleOnDoubleClick(view);
            view.setTag(null);
            return;
        }
        view.setTag(time);
    }

    void titleOnDoubleClick(View view) {
        listView.scrollToPosition(0);
    }

    @Click(R.id.cancelBtn)
    void cancelBtnOnClick() {
        finish();
    }

    @Click(R.id.okBtn)
    void okBtnOnClick() {
        Intent data = new Intent();
        data.putExtra("lawyers", selectedLawyers);
        setResult(RESULT_OK, data);
        finish();
    }
}
