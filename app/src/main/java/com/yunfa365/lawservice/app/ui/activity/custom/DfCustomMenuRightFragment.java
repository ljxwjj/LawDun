package com.yunfa365.lawservice.app.ui.activity.custom;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.ui.activity.base.DrawerActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Administrator on 2016/5/9.
 */
@EFragment(R.layout.fragment_df_custom_list_right_menu)
public class DfCustomMenuRightFragment extends Fragment {

    @ViewById(R.id.base_id_back)
    View mBackView;

    @ViewById(R.id.base_id_title)
    TextView mTitleTxt;

    @ViewById
    EditText gjc;

    private DrawerActivity mActivity;

    @AfterViews
    void init(){
        mActivity = (DrawerActivity) getActivity();

        mBackView.setVisibility(View.VISIBLE);
        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBord(gjc);
                mActivity.getDrawerLayout().closeDrawers();
            }
        });
        mTitleTxt.setText("高级检索");

        initData();
    }

    private void initData() {

    }

    @Click(android.R.id.button1)
    void button1OnClick(View view) {
        hideKeyBord(gjc);
        mActivity.getDrawerLayout().closeDrawers();

        String keyword1 = gjc.getText().toString();
        mActivity.reLoadData(keyword1);
    }

    private void hideKeyBord(View v) {
        InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
