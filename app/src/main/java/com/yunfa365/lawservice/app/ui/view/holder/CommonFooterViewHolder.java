package com.yunfa365.lawservice.app.ui.view.holder;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yunfa365.lawservice.app.R;

public class CommonFooterViewHolder extends RecyclerView.ViewHolder {
    private ImageView image;
    private TextView text;

    // 是否正在加载
    public int mLoadingStatus = 0; // 0:未加载 1：正在加载 2：加载失败
    //首次进入
    public boolean mFirstEnter = true;
    private View.OnClickListener mListener;

    public CommonFooterViewHolder(@NonNull View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.image);
        text = itemView.findViewById(R.id.text);

        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        itemView.setLayoutParams(params);
    }

    public static CommonFooterViewHolder create(Context context, View.OnClickListener listener) {
        View view = View.inflate(context, R.layout.common_refresh_list_footer, null);
        CommonFooterViewHolder viewHolder = new CommonFooterViewHolder(view);
        viewHolder.mListener = listener;
        return viewHolder;
    }


    public void setLoadingStart() {
        mLoadingStatus = 1;

        Animation circleAnim = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.anim_round_rotate);
        LinearInterpolator interpolator = new LinearInterpolator();
        circleAnim.setInterpolator(interpolator);

        image.startAnimation(circleAnim);
        image.setVisibility(View.VISIBLE);
        text.setText("正在加载...");
        text.setOnClickListener(null);

    }

    public void setLoadingNoMore() {
        mLoadingStatus = -1;

        image.clearAnimation();
        image.setVisibility(View.GONE);
        text.setText("没有更多了");

    }

    public void setLoadingError() {
        mLoadingStatus = 2;

        image.clearAnimation();
        image.setVisibility(View.GONE);
        text.setText("加载失败，点击重新加载！");
        text.setOnClickListener(mListener);
    }

    public void resetLoadingView() {
        mLoadingStatus = 0;

        image.clearAnimation();
        image.setVisibility(View.VISIBLE);
        text.setText("正在加载...");
    }

    public void refrash() {
        if (mLoadingStatus == -1) {
            setLoadingNoMore();
        } else if (mLoadingStatus == 0) {
            resetLoadingView();
        } else if (mLoadingStatus == 1) {
            setLoadingStart();
        } else if (mLoadingStatus == 2) {
            setLoadingError();
        }
    }

}
