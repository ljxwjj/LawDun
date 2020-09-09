package com.yunfa365.lawservice.app.ui.activity.seal;

import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.Map;

@EActivity(R.layout.activity_seal_manager)
public class SealManagerActivity extends BaseUserActivity {

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

    @ViewById
    RadioButton button1;

    @ViewById
    RadioButton button2;

    @Extra
    int defaultTab;

    private Class fragmentArray[] = {FragmentSealUserList_.class, FragmentSealList_.class};
    private Map<Class, Fragment> fragments;
    private Fragment currentFragment;

    @AfterViews
    void init() {
        mBackView.setVisibility(View.VISIBLE);
        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleTxt.setText("印章管理");

        fragments = new HashMap<>();
        if (defaultTab < 0 || defaultTab > 1) {
            defaultTab = 0;
        }
        showFragment(fragmentArray[defaultTab]);
        if (defaultTab == 1) {
            button2.setChecked(true);
        }

        tabs.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.button1:
                        showFragment(fragmentArray[0]);
                        break;
                    case R.id.button2:
                        showFragment(fragmentArray[1]);
                        break;
                }
            }
        });
    }

    private Fragment getFragment(Class fragmentClass) {
        Fragment fragment = fragments.get(fragmentClass);
        if (fragment == null) {
            fragment = Fragment.instantiate(this, fragmentClass.getName(), null);
            fragments.put(fragmentClass, fragment);
        }
        return fragment;
    }

    private void showFragment(Class fragmentClass) {
        Fragment fragment = getFragment(fragmentClass);

        if (currentFragment != fragment) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
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
