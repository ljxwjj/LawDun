package com.yunfa365.lawservice.app.ui.activity.auditing;

import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyResult;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.Audit;
import com.yunfa365.lawservice.app.pojo.YesNo;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;
import com.yunfa365.lawservice.app.ui.dialog.SpinnerDialog;
import com.yunfa365.lawservice.app.utils.FormFactory;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;


@EActivity(R.layout.activity_auditing_detail)
public class AuditingDetailActivity extends BaseUserActivity {
    private YesNo[] spzts = new YesNo[]{new YesNo(1, "审批通过"),new YesNo(2, "审批不通过")};

    @ViewById(R.id.base_id_back)
    View mBackView;

    @ViewById(R.id.base_id_title)
    TextView mTitleTxt;

    @ViewById(R.id.base_right_btn)
    ImageView mRightImage;

    @ViewById(R.id.base_right_txt)
    TextView mRightTxt;

    @ViewById
    LinearLayout containerLayout;

    @ViewById
    LinearLayout auditForm;

    @ViewById
    TextView spzt;

    @ViewById
    EditText make;

    @Extra
    int ID;

    Audit item;

    @AfterViews
    void init() {

        mBackView.setVisibility(View.VISIBLE);
        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleTxt.setText("审批详情");

        loadData();
    }

    @Click(R.id.spzt)
    void spztOnClick() {
        new SpinnerDialog(this, "选择审批状态", spzts, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                YesNo selected = spzts[which];
                spzt.setTag(selected);
                spzt.setText(selected.toString());
            }
        }).show();
    }

    @Click(R.id.submitBtn)
    void submitBtnOnClick() {
        doSubmit();
    }

    private void initView() {
        containerLayout.removeView(auditForm);

        String make = item.Make.replaceAll("；", "\n");
        containerLayout.addView(FormFactory.createGroupTitle(this, "审批内容"));
        containerLayout.addView(FormFactory.createTextField(this, "审批类型：", item.GidTxt));
        containerLayout.addView(FormFactory.createTextField(this, "提交人：", item.UsersFullName));
        containerLayout.addView(FormFactory.createTextField(this, "提交时间：", item.AddTime));
        containerLayout.addView(FormFactory.createLinkField(this, "审批主体：", make, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }));

        containerLayout.addView(FormFactory.createGroupTitle(this, "审批操作"));
        containerLayout.addView(auditForm);
        containerLayout.addView(FormFactory.createGroupTitle(this, "历史审批"));
    }

    private void loadData() {
        showLoading();
        AppRequest request = new AppRequest.Build("api/Audit/Audit_Get")
                .addParam("Rid", ID + "")
                .create();
        new HttpFormFuture.Builder(this)
                .setData(request)
                .setListener(new AgnettyFutureListener(){

                    @Override
                    public void onComplete(AgnettyResult result) {
                        hideLoading();
                        AppResponse resp = (AppResponse)result.getAttach();
                        if (resp.flag) {
                            item = resp.getFirstObject(Audit.class);
                            initView();
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

    private void doSubmit() {
        YesNo selectedStat = (YesNo) spzt.getTag();
        if (selectedStat == null) return;
        showLoading();
        AppRequest request = new AppRequest.Build("api/Audit/Audit_Post")
                .addParam("Rid", ID + "")
                .addParam("Stat", selectedStat.id + "")
                .addParam("SMake", make.getText().toString())
                .create();
        new HttpFormFuture.Builder(this)
                .setData(request)
                .setListener(new AgnettyFutureListener(){

                    @Override
                    public void onComplete(AgnettyResult result) {
                        hideLoading();
                        AppResponse resp = (AppResponse)result.getAttach();
                        if (resp.flag) {
                            setResult(RESULT_OK);
                            finish();
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
}
