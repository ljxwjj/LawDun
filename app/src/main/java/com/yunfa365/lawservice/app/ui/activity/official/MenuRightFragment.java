package com.yunfa365.lawservice.app.ui.activity.official;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.ui.activity.base.DrawerActivity;
import com.yunfa365.lawservice.app.utils.DateUtil;
import com.yunfa365.lawservice.app.utils.StringUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2016/5/9.
 */
@EFragment(R.layout.fragment_official_list_right_menu)
public class MenuRightFragment extends Fragment {

    @ViewById(R.id.base_id_back)
    View mBackView;

    @ViewById(R.id.base_id_title)
    TextView mTitleTxt;

    @ViewById
    EditText keyword1;

    @ViewById
    EditText keyword2;

    @ViewById
    EditText keyword3;

    @ViewById
    EditText keyword4;

    @ViewById
    EditText keyword5;

    private DrawerActivity mActivity;

    @AfterViews
    void init(){
        mActivity = (DrawerActivity) getActivity();

        mBackView.setVisibility(View.VISIBLE);
        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBord();
                mActivity.getDrawerLayout().closeDrawers();
            }
        });
        mTitleTxt.setText("客户检索");

        initData();
    }

    private void initData() {

    }

    @Click(android.R.id.button1)
    void button1OnClick(View view) {
        hideKeyBord();
        mActivity.getDrawerLayout().closeDrawers();

        String k1 = keyword1.getText().toString();
        String k2 = keyword2.getText().toString();
        String k3 = keyword3.getText().toString();
        String k4 = keyword4.getText().toString();
        String k5 = keyword5.getText().toString();
        mActivity.reLoadData(k1, k2, k3, k4, k5);
    }

    public void hideKeyBord() {
        View v = keyword1;
        InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Click({R.id.keyword4, R.id.keyword5})
    void keyword45OnClick(EditText keywordView) {
        String rq = keywordView.getText().toString();
        final Calendar calendar = Calendar.getInstance();
        if (TextUtils.isEmpty(rq)) {
            calendar.setTime(new Date());
        } else {
            calendar.setTime(StringUtil.formatDate(rq, "yyyy-MM-dd"));
        }
        new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String rq = DateUtil.formatDate(calendar, "yyyy-MM-dd");
                keywordView.setText(rq);
            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}
