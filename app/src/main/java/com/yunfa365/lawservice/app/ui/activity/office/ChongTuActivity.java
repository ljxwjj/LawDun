package com.yunfa365.lawservice.app.ui.activity.office;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
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
import com.yunfa365.lawservice.app.ui.view.holder.EmptyViewHolder;
import com.yunfa365.lawservice.app.utils.ScreenUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
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
    List<ShenCha> mData;

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


      /*  if (selectedCaseCols == null) {
            mButton2.setVisibility(View.GONE);
        }*/
        mData = new ArrayList();
        mAdapter = new MyAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listView.addItemDecoration(new DividerItemDecoration(1));
        listView.setLayoutManager(layoutManager);
        listView.setAdapter(mAdapter);
    }

    private View createHeaderView() {
        View headerView = LinearLayout.inflate(this, R.layout.list_header_chongtu, null);

        return headerView;
    }

    /*@OnActivityResult(REQUEST_CODE)
    void onResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }*/

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
        mData.clear();
        mAdapter.notifyDataSetChanged();

        hideKeyBord(mHeaderViewHolder.mInput1);
        doSearch();
        /*if (selectedCaseCols != null) {
            mButton2.setEnabled(true);
        }*/
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
                            mData.addAll(shenChas);
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

    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        static final int ITEM_TYPE_HEADER = 0;
        static final int ITEM_TYPE_CONTENT = 1;
        static final int ITEM_TYPE_FOOTER = 2;
        static final int ITEM_TYPE_EMPTY = 3;

        private int mHeaderCount = 1;
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
                View headerView = createHeaderView();
                mHeaderViewHolder = new HeaderViewHolder(headerView);
                return mHeaderViewHolder;
            } else if (viewType == ITEM_TYPE_FOOTER) {
                return null;
            } else if (viewType == ITEM_TYPE_CONTENT){
                View view = View.inflate(ChongTuActivity.this, R.layout.item_chongtu_list, null);
                ContentViewHolder viewHolder = new ContentViewHolder(view);
                return viewHolder;
            } else if (viewType == ITEM_TYPE_EMPTY) {
                View view = View.inflate(ChongTuActivity.this, R.layout.common_list_item_empty, null);
                return new EmptyViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ContentViewHolder) {
                ShenCha item = getItem(position);
                ContentViewHolder itemViewHolder = (ContentViewHolder) holder;
                itemViewHolder.text1.setText(item.AyMake);
                itemViewHolder.text2.setText("案　　　号：" + item.CaseIdTxt);
                itemViewHolder.text3.setText("委　托　人：" + item.CustName);
                itemViewHolder.text4.setText("对方当事人：" + item.DCustName);
                itemViewHolder.text5.setText("主办律师　：" + item.UsersListName);
                itemViewHolder.text6.setText("结案状态  ：" + item.EndStatTxt);
            }
        }

        @Override
        public int getItemCount() {
            return mHeaderCount + getContentItemCount() + getFooterCount();
        }

        private ShenCha getItem(int position) {
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

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        private EditText mInput1, mInput2;
        private View mButton1, mButton2;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(params);

            mInput1 = itemView.findViewById(android.R.id.text1);
            mInput2 = itemView.findViewById(android.R.id.text2);
            mButton1 = itemView.findViewById(android.R.id.button1);
            mButton2 = itemView.findViewById(android.R.id.button2);
            mInput2.setOnEditorActionListener(new TextView.OnEditorActionListener(){
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH){
                        onClickOk();
                    }
                    return true;
                }
            });
            mButton1.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    onClickOk();
                }
            });
            mButton2.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
//                Intent intent = new Intent(ChongTuActivity.this, Office41_1Activity_.class);
//                intent.putExtra("selectedCaseCols", selectedCaseCols);
//                startActivityForResult(intent, REQUEST_CODE);
                }
            });
        }
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2, text3, text4, text5, text6;

        public ContentViewHolder(View itemView) {
            super(itemView);
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(params);

            text1 = itemView.findViewById(R.id.text1);
            text2 = itemView.findViewById(R.id.text2);
            text3 = itemView.findViewById(R.id.text3);
            text4 = itemView.findViewById(R.id.text4);
            text5 = itemView.findViewById(R.id.text5);
            text6 = itemView.findViewById(R.id.text6);
        }
    }

    private class DividerItemDecoration extends RecyclerView.ItemDecoration {
        private int dividerHeight;
        private Drawable mDivider;
        Drawable whiteDrawable;

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
