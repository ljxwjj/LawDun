package com.yunfa365.lawservice.app.ui.activity.law_case;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.pojo.TSearchType;
import com.yunfa365.lawservice.app.ui.activity.base.DrawerActivity;
import com.yunfa365.lawservice.app.ui.dialog.SpinnerDialog;
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
@EFragment(R.layout.fragment_case_list_right_menu)
public class MenuRightFragment extends Fragment {
    private static final TSearchType[] spzts = {new TSearchType(1000, "全部")
            , new TSearchType(0, "待审批")
            , new TSearchType(1, "审批通过")
            , new TSearchType(2, "未通过")};

    @ViewById(R.id.base_id_back)
    View mBackView;

    @ViewById(R.id.base_id_title)
    TextView mTitleTxt;

    @ViewById
    EditText ahss;

    @ViewById
    EditText wtr;

    @ViewById
    EditText dfdsr;

    @ViewById
    EditText ksrq;

    @ViewById
    EditText jzrq;

    @ViewById
    EditText spzt;

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
        mTitleTxt.setText("高级检索");
        spzt.setTag(spzts[0]);
        initData();
    }

    private void initData() {

    }

    @Click(android.R.id.button1)
    void button1OnClick(View view) {
        hideKeyBord();
        mActivity.getDrawerLayout().closeDrawers();

        String k2 = ahss.getText().toString();
        String k3 = wtr.getText().toString();
        String k4 = dfdsr.getText().toString();
        String k5 = ksrq.getText().toString();
        String k6 = jzrq.getText().toString();
        String k7 = ((TSearchType)spzt.getTag()).id + "";
        mActivity.reLoadData(k2, k3, k4, k5, k6, k7);
    }

    public void hideKeyBord() {
        View v = ahss;
        InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Click({R.id.ksrq, R.id.jzrq})
    void rqOnClick(EditText keywordView) {
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

    @Click(R.id.spzt)
    void spztOnclick(View view) {
        new SpinnerDialog(mActivity, "审批状态", spzts, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TSearchType fs = spzts[which];
                spzt.setText(fs.toString());
                spzt.setTag(fs);
            }
        }).show();
    }
}
