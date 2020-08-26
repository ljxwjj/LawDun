package com.yunfa365.lawservice.app.ui.activity.custom;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyResult;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.Custom;
import com.yunfa365.lawservice.app.pojo.YesNo;
import com.yunfa365.lawservice.app.pojo.base.CommonItem;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;
import com.yunfa365.lawservice.app.ui.adapter.CommonListAdapter;
import com.yunfa365.lawservice.app.ui.dialog.SpinnerDialog;
import com.yunfa365.lawservice.app.ui.view.holder.CommonFooterViewHolder;
import com.yunfa365.lawservice.app.ui.view.holder.CommonItemViewHolder;
import com.yunfa365.lawservice.app.ui.view.holder.EmptyViewHolder;
import com.yunfa365.lawservice.app.ui.view.pulltorefresh.XListView;
import com.yunfa365.lawservice.app.utils.ViewHolder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/21.
 * 选择委托人
 */
@EActivity(R.layout.activity_office_search_custom)
public class Office_searchCustomActivity extends BaseUserActivity {
    private static final int ADD_WTR_REQUEST_CODE = 1;

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
    private int mPage;

    private EditText mInput1;
    private View mButton1, mButton2;
    private String keyword1;

    private HeaderViewHolder mHeaderViewHolder;
    private CommonFooterViewHolder mFooterViewHolder;

    @AfterViews
    void init(){
        mBackView.setVisibility(View.VISIBLE);
        mBackView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleTxt.setText("选择委托人");

        View headerView = LinearLayout.inflate(this, R.layout.list_header_office_wtr, null);
        mHeaderViewHolder = new HeaderViewHolder(headerView);
        mInput1 = (EditText) headerView.findViewById(android.R.id.text1);
        mButton1 = headerView.findViewById(android.R.id.button1);
        mButton2 = headerView.findViewById(android.R.id.button2);

        mInput1.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    hideKeyBord(v);
                    String keyword1 = mInput1.getText().toString();
                    reLoadData(keyword1);
                }
                return true;
            }
        });

        mButton1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                hideKeyBord(v);
                String keyword = mInput1.getText().toString();
                reLoadData(keyword);
            }
        });
        mButton2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String customName = mInput1.getText().toString();
                Intent intent = new Intent(Office_searchCustomActivity.this, Office_addCustomActivity_.class);
                intent.putExtra("customName", customName);
                startActivityForResult(intent, ADD_WTR_REQUEST_CODE);
            }
        });

        mFooterViewHolder = CommonFooterViewHolder.create(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(layoutManager);
        mAdapter = new MyAdapter(this);
        listView.setAdapter(mAdapter);

        setOnLoadingListener();
        mPage = 0;
        loadData();
    }

    @OnActivityResult(ADD_WTR_REQUEST_CODE)
    void addWtrOnResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Custom custom = (Custom) data.getSerializableExtra("customItem");
            setResult(RESULT_OK, data);
            finish();
        }
    }

    private void hideKeyBord(View v) {
        InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    //String title, String type, String cols, String caseYear, String auditStat
    public void reLoadData(String... params) {
        if (params != null && params.length == 1) {
            this.keyword1 = params[0];
        }
        mPage = 0;
        loadData();
    }

    private void loadData() {// 检索所有客户
        final int loadPage = mPage + 1;
        AppRequest request = new AppRequest.Build("api/Custom/list_My")
                .addParam("CustName", keyword1)
                .addParam("PageIndex", loadPage+"")
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
                            List<Custom> data = resp.resultsToList(Custom.class);
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
                        hideLoading();
                    }
                })
                .execute();
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

    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

        protected static final int ITEM_TYPE_HEADER = 0;
        protected static final int ITEM_TYPE_CONTENT = 1;
        protected static final int ITEM_TYPE_FOOTER = 2;
        protected static final int ITEM_TYPE_EMPTY = 3;

        protected int mHeaderCount = 1;
        protected int mFooterCount = 1;
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
                return mHeaderViewHolder;
            } else if (viewType == ITEM_TYPE_FOOTER) {
                return mFooterViewHolder;
            } else if (viewType == ITEM_TYPE_CONTENT){
                View view = View.inflate(mContext, R.layout.custom_list_choice_item, null);
                ItemViewHolder viewHolder = new ItemViewHolder(view);
                viewHolder.radio.setOnClickListener(this);
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
                CommonItem item = getItem(position);
                itemViewHolder.radio.setText(item.getTitle());
                itemViewHolder.radio.setTag(item);
            } else if (holder instanceof CommonFooterViewHolder) {
                CommonFooterViewHolder footerViewHolder = (CommonFooterViewHolder) holder;
                footerViewHolder.refrash();
            }
        }

        @Override
        public int getItemCount() {
            return mHeaderCount + getContentItemCount() + getFooterCount();
        }

        private CommonItem getItem(int position) {
            return (CommonItem) mData.get(position - mHeaderCount);
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
            Custom c = (Custom) v.getTag();
            Intent data = new Intent();
            data.putExtra("customItem", c);
            setResult(RESULT_OK, data);
            finish();
        }

    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        RadioButton radio;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            radio = itemView.findViewById(R.id.radio);

            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(params);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(params);
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
}
