package com.yunfa365.lawservice.app.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.ui.fragment.base.BaseFragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_page2)
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

    private Activity mActivity;



    @Override
    public void onDestroy() {
        mActivity = null;
        _contentView_ = null;
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (_contentView_ == null) {
            _contentView_ = inflater.inflate(R.layout.fragment_page2, container, false);
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
        mActivity = getActivity();
        mTitleTxt.setText("小工具");

    }


}
