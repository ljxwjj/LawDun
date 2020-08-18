package com.yunfa365.lawservice.app.ui.activity.office;

import android.content.Context;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyResult;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.ShenCha;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;
import com.yunfa365.lawservice.app.ui.adapter.CommonListAdapter;
import com.yunfa365.lawservice.app.ui.view.holder.CommonFooterViewHolder;
import com.yunfa365.lawservice.app.ui.view.holder.CommonItemViewHolder;
import com.yunfa365.lawservice.app.ui.view.holder.EmptyViewHolder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import java.util.List;


@EActivity(R.layout.common_list_activity)
class ChongTuActivity extends BaseUserActivity {

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

    private HeaderViewHolder mHeaderViewHolder;
    private String keyword1, keyword2;

    @AfterViews
    void init(){
        mBackView.setVisibility(View.VISIBLE);
        mBackView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleTxt.setText("利益冲突预测");

        mAdapter = new MyAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(layoutManager);
        listView.setAdapter(mAdapter);
    }

    private View createHeaderView() {
        View headerView = LinearLayout.inflate(this, R.layout.list_header_chongtu, null);

        return headerView;
    }

    private void hideKeyBord(View v) {
        InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void onClickOk() {
        keyword1 = mHeaderViewHolder.mInput1.getText().toString();
        keyword2 = mHeaderViewHolder.mInput2.getText().toString();
        if (TextUtils.isEmpty(keyword1) || TextUtils.isEmpty(keyword2)) {
            showToast("请输入委托人/当事人和对方当事人");
            return;
        }
        mAdapter.mData.clear();
        mAdapter.notifyDataSetChanged();

        hideKeyBord(mHeaderViewHolder.mInput1);
        doSearch();
    }

    private void doSearch() {
        AppRequest request = new AppRequest.Build("api/Case/Case_ChongTu")
                .addParam("Custom", keyword1)
                .addParam("DCustom", keyword2)
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
                            List<ShenCha> shenChas = resp.resultsToList(ShenCha.class);
                            mAdapter.mData.addAll(shenChas);
                            mAdapter.notifyDataSetChanged();
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

    class MyAdapter extends CommonListAdapter {
        public MyAdapter(Context context) {
            super(context);
            mHeaderCount = 1;
            mFooterCount = 0;
            hasDetail = false;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == ITEM_TYPE_HEADER) {
                View headerView = createHeaderView();
                mHeaderViewHolder = new HeaderViewHolder(headerView);
                return mHeaderViewHolder;
            } else if (viewType == ITEM_TYPE_FOOTER) {
                return null;
            } else if (viewType == ITEM_TYPE_CONTENT){
                RecyclerView.ViewHolder viewHolder = CommonItemViewHolder.create(mContext);
                return viewHolder;
            } else if (viewType == ITEM_TYPE_EMPTY) {
                View view = View.inflate(ChongTuActivity.this, R.layout.common_list_item_empty, null);
                return new EmptyViewHolder(view);
            }
            return null;
        }

        @Override
        public int getItemCount() {
            return mHeaderCount + getContentItemCount() + getFooterCount();
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
        protected CommonFooterViewHolder getFooterViewHolder() {
            return null;
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        private EditText mInput1, mInput2;
        private View mButton1;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(params);

            mInput1 = itemView.findViewById(android.R.id.text1);
            mInput2 = itemView.findViewById(android.R.id.text2);
            mButton1 = itemView.findViewById(android.R.id.button1);
            mInput2.setOnEditorActionListener(new TextView.OnEditorActionListener(){
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH){
                        onClickOk();
                    }
                    return true;
                }
            });
            mButton1.setOnClickListener(v -> onClickOk());
        }
    }


}
