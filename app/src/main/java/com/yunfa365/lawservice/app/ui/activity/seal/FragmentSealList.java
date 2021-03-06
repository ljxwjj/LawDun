package com.yunfa365.lawservice.app.ui.activity.seal;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyResult;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.BhSeal;
import com.yunfa365.lawservice.app.pojo.FieldItem;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.base.BaseActivity;
import com.yunfa365.lawservice.app.ui.dialog.BottomMenuDialog;
import com.yunfa365.lawservice.app.ui.fragment.base.BaseFragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@EFragment(R.layout.fragment_seal_list)
public class FragmentSealList extends BaseFragment {

    @ViewById
    RecyclerView listView;
    private BaseQuickAdapter mAdapter;

    private BaseActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (_contentView_ == null) {
            _contentView_ = inflater.inflate(R.layout.fragment_seal_list, container, false);
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
        mActivity = (BaseActivity) getActivity();


        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        listView.setLayoutManager(layoutManager);
        mAdapter = new BaseQuickAdapter<BhSeal, BaseViewHolder>(R.layout.seal_list_item){
            @Override
            protected void convert(@NotNull BaseViewHolder holder, BhSeal bhSeal) {
                holder.setText(R.id.title, bhSeal.ZTitle);
            }
        };
        listView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                BhSeal item = (BhSeal) mAdapter.getItem(position);
                showMenuDialog(item);
            }
        });

        loadData();
    }

    private void showMenuDialog(BhSeal item) {
        final FieldItem items[] = {new FieldItem(1, "删除")};
        new BottomMenuDialog(mActivity, items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    postDeleteSeal(item);
                }
            }
        }).show();
    }

    private void postDeleteSeal(BhSeal item) {
        mActivity.showLoading();
        AppRequest request = new AppRequest.Build("api/WebSet/Zhang_Delete")
                .addParam("Zid", item.ID + "")
                .create();
        new HttpFormFuture.Builder(mActivity)
                .setData(request)
                .setListener(new AgnettyFutureListener(){
                    @Override
                    public void onComplete(AgnettyResult result) {
                        mActivity.hideLoading();
                        AppResponse resp = (AppResponse)result.getAttach();
                        if (resp.flag) {
                            mAdapter.remove(item);
                        } else {
                            mActivity.showToast(resp.Message);
                        }
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        mActivity.hideLoading();
                        mActivity.showToast(R.string.network_exception_message);
                    }
                })
                .execute();
    }


    private void loadData() {
        mActivity.showLoading();
        AppRequest request = new AppRequest.Build("api/WebSet/Zhang_list")
                .create();
        new HttpFormFuture.Builder(mActivity)
                .setData(request)
                .setListener(new AgnettyFutureListener(){
                    @Override
                    public void onComplete(AgnettyResult result) {
                        mActivity.hideLoading();
                        AppResponse resp = (AppResponse)result.getAttach();
                        if (resp.flag) {
                            List<BhSeal> data = resp.resultsToList(BhSeal.class);
                            mAdapter.setList(data);
                        } else {
                            mActivity.showToast(resp.Message);
                        }
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        mActivity.hideLoading();
                        mActivity.showToast(R.string.network_exception_message);
                    }
                })
                .execute();
    }
}
