package com.yunfa365.lawservice.app.ui.activity.auditing;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.pojo.TSearchType;
import com.yunfa365.lawservice.app.ui.activity.base.DrawerActivity;
import com.yunfa365.lawservice.app.ui.dialog.SpinnerDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Administrator on 2016/5/9.
 */
@EFragment(R.layout.fragment_custom_list_right_menu)
public class MenuRightFragment extends Fragment {
    private static final TSearchType[] jsfss = {new TSearchType(1, "根据委托人"), new TSearchType(4, "根据电话")};

    @ViewById(R.id.base_id_back)
    View mBackView;

    @ViewById(R.id.base_id_title)
    TextView mTitleTxt;

    @ViewById
    EditText gjc;

    @ViewById
    EditText jsfs;

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
        mTitleTxt.setText("客户检索");

        initData();
    }

    private void initData() {
        jsfs.setText(jsfss[0].toString());
        jsfs.setTag(jsfss[0]);
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

    @Click(R.id.jsfs)
    void jsfsOnClick(View view) {
        new SpinnerDialog(mActivity, "检索方式", jsfss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TSearchType fs = jsfss[which];
                jsfs.setText(fs.toString());
                jsfs.setTag(fs);
            }
        }).show();
    }
}
