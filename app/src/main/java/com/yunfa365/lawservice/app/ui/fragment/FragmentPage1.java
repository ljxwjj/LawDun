package com.yunfa365.lawservice.app.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.ui.activity.HomeActivity;
import com.yunfa365.lawservice.app.ui.fragment.base.BaseFragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.Map;

@EFragment(R.layout.fragment_page1)
class FragmentPage1 extends BaseFragment {

    @ViewById(R.id.base_id_back)
    View mBackView;

    @ViewById(R.id.base_id_title)
    TextView mTitleTxt;

    @ViewById(R.id.base_right_btn)
    ImageView mRightImage;

    @ViewById(R.id.base_right_txt)
    TextView mRightTxt;

    @ViewById
    RadioGroup tabs;

    private Map<String, Fragment> fragments;
    private Fragment currentFragment;
    private HomeActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (_contentView_ == null) {
            _contentView_ = inflater.inflate(R.layout.fragment_page1, container, false);
        }
        ViewGroup parent = (ViewGroup) _contentView_.getParent();
        if (parent != null) {
            parent.removeView(_contentView_);
        }

        return _contentView_;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        mActivity = null;
        _contentView_ = null;
        super.onDestroy();
    }

    @AfterViews
    public void init() {
        if (_contentView_.getTag() != null) {
            return;
        }
        _contentView_.setTag(new Object());
        mTitleTxt.setText("消息中心");
        mActivity = (HomeActivity)getActivity();
        _contentView_.setTag(mActivity);

        fragments = new HashMap<>();
        showFragment("1");

        tabs.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.button1:
                        showFragment("1");
                    break;
                    case R.id.button2:
                        showFragment("2");
                    break;
                }
            }
        });
    }

    private Fragment getFragment(String Cols) {
        Fragment fragment = fragments.get(Cols);
        if (fragment == null) {
            fragment = FragmentMessageList_.builder().Cols(Cols).build();

            fragments.put(Cols, fragment);
        }
        return fragment;
    }

    private void showFragment(String Cols) {
        Fragment fragment = getFragment(Cols);

        if (currentFragment != fragment) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
            currentFragment = fragment;
            if (!fragment.isAdded()) {
                transaction.add(R.id.fragment_container, fragment).commit();
            } else {
                transaction.show(fragment).commit();
            }
        }
    }

}
