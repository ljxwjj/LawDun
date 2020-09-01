package com.yunfa365.lawservice.app.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.pojo.GridItem;
import com.yunfa365.lawservice.app.ui.activity.HomeActivity;
import com.yunfa365.lawservice.app.ui.activity.common.ToolWebViewActivity_;
import com.yunfa365.lawservice.app.ui.fragment.base.BaseFragment;
import com.yunfa365.lawservice.app.ui.view.MyGridLayout;
import com.yunfa365.lawservice.app.utils.FileUtil;
import com.yunfa365.lawservice.app.utils.ResourceUtil;
import com.yunfa365.lawservice.app.utils.StringUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import java.util.List;

@EFragment(R.layout.fragment_page4)
class FragmentPage4 extends BaseFragment {

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
    MyGridLayout myGridLayout;

    private HomeActivity mActivity;
    private List<GridItem> mGridItems;
    private GridItemClickListener mItemClickListener;

    private String widgetPath;


    @Override
    public void onDestroy() {
        mActivity = null;
        _contentView_ = null;
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (_contentView_ == null) {
            _contentView_ = inflater.inflate(R.layout.fragment_page4, container, false);
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
        mActivity = (HomeActivity) getActivity();
        mTitleTxt.setText("小工具");

        String tools = FileUtil.getRawFileContent(getResources(), R.raw.tools);
        mGridItems = StringUtil.toObjectList(tools, GridItem.class);
        mItemClickListener = new GridItemClickListener();

        widgetPath = mActivity.getFilesDir().getAbsolutePath() + "/widget";

        loadGridView();
    }

    private void loadGridView() {
        if (myGridLayout.getChildCount() > 0)
            myGridLayout.removeAllViews();

        for (GridItem item : mGridItems) {
            View view = LinearLayout.inflate(mActivity, R.layout.tools_grid_item, null);
            ImageView imageView = view.findViewById(R.id.image);
            TextView textView = view.findViewById(R.id.text);
            imageView.setImageResource(ResourceUtil.getMipmapId(mActivity, item.image));
            textView.setText(item.name);
            myGridLayout.addView(view);
            view.setTag(item);
            view.setOnClickListener(mItemClickListener);
        }
        int childCount = myGridLayout.getChildCount();
        int whiteSpan = childCount%4 == 0?0:4-childCount%4;
        for (int i=0; i < whiteSpan; i++) {
            View whiteView = new View(mActivity);
            whiteView.setBackgroundResource(android.R.color.white);
            myGridLayout.addView(whiteView);
        }
    }

    private class GridItemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            GridItem item = (GridItem)v.getTag();
            Intent intent = new Intent(mActivity, ToolWebViewActivity_.class);
            intent.putExtra("toolItem", item);
            startActivity(intent);
//            mActivity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
    }
}
