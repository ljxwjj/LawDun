package com.yunfa365.lawservice.app.ui.activity.user;

import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyResult;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.BhSeal;
import com.yunfa365.lawservice.app.pojo.User;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;
import com.yunfa365.lawservice.app.ui.dialog.SpinnerDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;


@EActivity(R.layout.activity_user_bind_seal)
public class UserBindSealActivity extends BaseUserActivity {

    @ViewById(R.id.base_id_back)
    View mBackView;

    @ViewById(R.id.base_id_title)
    TextView mTitleTxt;

    @ViewById(R.id.base_right_btn)
    ImageView mRightImage;

    @ViewById(R.id.base_right_txt)
    TextView mRightTxt;

    @ViewById
    TextView user;

    @ViewById
    TextView seal;

    private int loadingFlag = 0;
    private User[] allUsers;
    private BhSeal[] allSeals;

    @AfterViews
    void init() {
        mBackView.setVisibility(View.VISIBLE);
        mBackView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleTxt.setText("印章授权");

        loadData();
    }

    @Click(R.id.user)
    void userOnClick() {
        if (allUsers == null) return;

        new SpinnerDialog(this, "选择用户", allUsers, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                User item = allUsers[which];
                user.setText(item.FullName);
                user.setTag(item);
            }
        }).show();
    }

    @Click(R.id.seal)
    void sealOnClick() {
        if (allSeals == null) return;

        new SpinnerDialog(this, "选择印章", allSeals, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BhSeal item = allSeals[which];
                seal.setText(item.ZTitle);
                seal.setTag(item);
            }
        }).show();
    }

    @Click(R.id.submitBtn)
    void submitBtnOnClick() {
        User userItem = (User) user.getTag();
        BhSeal sealItem = (BhSeal) seal.getTag();

        if (userItem == null) {
            showToast("请选择印章");
        }
        if (sealItem == null) {
            showToast("请选择人员");
            return;
        }
        doSubmit(userItem, sealItem);
    }

    private void loadData() {
        AppRequest request = new AppRequest.Build("api/Users/Users_List_Get")
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
                            allUsers = resp.resultsToArray(User.class);
                        } else {
                            showToast(resp.Message);
                        }
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        hideLoading();
                        result.getException().printStackTrace();
                    }
                })
                .execute();

        request = new AppRequest.Build("api/WebSet/Zhang_list")
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
                            allSeals = resp.resultsToArray(BhSeal.class);
                        } else {
                            showToast(resp.Message);
                        }
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        hideLoading();
                        result.getException().printStackTrace();
                    }
                })
                .execute();
    }

    private void doSubmit(User userItem, BhSeal sealItem) {
        AppRequest request = new AppRequest.Build("api/WebSet/Zhang_bind_Add")
                .addParam("Uid", userItem.ID + "")
                .addParam("Zid", sealItem.ID + "")
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
                            showToast(resp.Message);
                            finish();
                        } else {
                            showToast(resp.Message);
                        }
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        hideLoading();
                        result.getException().printStackTrace();
                    }
                })
                .execute();
    }

    @Override
    public void showLoading() {
        super.showLoading();
        loadingFlag++;
    }

    @Override
    public void hideLoading() {
        loadingFlag--;
        if (loadingFlag <= 0) {
            super.hideLoading();
        }
    }
}
