package com.yunfa365.lawservice.app.ui.view.holder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class EmptyViewHolder extends RecyclerView.ViewHolder {
    public EmptyViewHolder(@NonNull View itemView) {
        super(itemView);

        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
        itemView.setLayoutParams(params);
    }
}
