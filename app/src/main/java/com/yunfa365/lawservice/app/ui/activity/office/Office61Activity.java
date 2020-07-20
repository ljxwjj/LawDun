package com.yunfa365.lawservice.app.ui.activity.office;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyResult;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.CaseCols;
import com.yunfa365.lawservice.app.pojo.ShenCha;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;
import com.yunfa365.lawservice.app.ui.view.holder.EmptyViewHolder;
import com.yunfa365.lawservice.app.utils.ScreenUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/4/19.
 * 新增案件-利益冲突预测
 */
@EActivity(R.layout.activity_office61)
public class Office61Activity extends BaseUserActivity {
    private static final int REQUEST_CODE = 1;

    @Extra
    CaseCols selectedCaseCols;

    @ViewById(R.id.base_id_back)
    View mBackView;

    @ViewById(R.id.base_id_title)
    TextView mTitleTxt;

    @ViewById(R.id.base_right_btn)
    ImageView mRightImage;

    @ViewById(R.id.base_right_txt)
    TextView mRightTxt;

    @ViewById(R.id.search_layout)
    View searchLayout;

    @ViewById(R.id.search_result_layout)
    View resultLayout;

    @ViewById(android.R.id.text1)
    EditText mInput1;

    @ViewById(android.R.id.text2)
    EditText mInput2;

    @ViewById(android.R.id.button1)
    TextView mButton1;

    @ViewById(android.R.id.button2)
    TextView mButton2;

    @ViewById(android.R.id.button3)
    TextView mButton3;

    @ViewById
    RecyclerView listView;
    MyAdapter mAdapter;
    List<ShenCha> mData;

    private String keyword1, keyword2;
//    private int currentPage;

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
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLayout.setVisibility(View.VISIBLE);
                resultLayout.setVisibility(View.GONE);
            }
        });
        mButton3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                gotoOffice41();
            }
        });
        if (selectedCaseCols == null) {
            mButton2.setVisibility(View.GONE);
        }

        mData = new ArrayList();
        mAdapter = new MyAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listView.addItemDecoration(new DividerItemDecoration(1));
        listView.setLayoutManager(layoutManager);
        listView.setAdapter(mAdapter);
    }

    private void gotoOffice41() {
        /*Intent intent = new Intent(this, Office41_1Activity_.class);
        intent.putExtra("selectedCaseCols", selectedCaseCols);
        startActivityForResult(intent, REQUEST_CODE);*/
    }

    @OnActivityResult(REQUEST_CODE)
    void onResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private void hideKeyBord(View v) {
        InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void onClickOk() {
        keyword1 = mInput1.getText().toString();
        keyword2 = mInput2.getText().toString();
        if (TextUtils.isEmpty(keyword1) || TextUtils.isEmpty(keyword2)) {
            showToast("请输入委托人/当事人和对方当事人");
            return;
        }
        mData.clear();
        mAdapter.notifyDataSetChanged();

        hideKeyBord(mInput1);
        doSearch();
    }

    private void doSearch() {
        AppRequest request = new AppRequest.Build("Case/CaseExamine")
                .addParam("Custom", keyword1)
                .addParam("Tdcustom", keyword2)
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
                            searchLayout.setVisibility(View.GONE);
                            resultLayout.setVisibility(View.VISIBLE);

                            List<ShenCha> shenChas = resp.resultsToList(ShenCha.class);
                            mData.addAll(shenChas);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            showAddDialog();
                        }
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        hideLoading();
                    }
                })
                .execute();
    }

    private void showAddDialog() {
        new AlertDialog.Builder(this).setTitle(R.string.notice)
                .setMessage("未检测到相关案件，去立案？")
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("去立案", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gotoOffice41();
                    }
                }).show();
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
                View view = View.inflate(Office61Activity.this, R.layout.item_chongtu_list, null);
                ContentViewHolder viewHolder = new ContentViewHolder(view);
                return viewHolder;
            } else if (viewType == ITEM_TYPE_EMPTY) {
                View view = View.inflate(Office61Activity.this, R.layout.common_list_item_empty, null);
                return new EmptyViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ChongTuActivity.ContentViewHolder) {
                ShenCha item = getItem(position);
                ChongTuActivity.ContentViewHolder itemViewHolder = (ChongTuActivity.ContentViewHolder) holder;
                itemViewHolder.text1.setText(item.AyMake);
                itemViewHolder.text2.setText("案　　　号：" + item.CaseIdTxt);
                itemViewHolder.text3.setText("委　托　人：" + item.CustName);
                itemViewHolder.text4.setText("对方当事人：" + item.DCustName);
                itemViewHolder.text5.setText("主办律师　：" + item.UsersListName);
                itemViewHolder.text6.setText("结案状态　：" + item.EndStatTxt);
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
