package com.yunfa365.lawservice.app.ui.view.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.pojo.base.CommonItem;

public class CommonItemViewHolder extends RecyclerView.ViewHolder {
    TextView title, desc, status;
    public ImageView detail;

    public CommonItemViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        desc = itemView.findViewById(R.id.desc);
        status = itemView.findViewById(R.id.status);
        detail = itemView.findViewById(R.id.detail);

        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        itemView.setLayoutParams(params);
    }

    public static CommonItemViewHolder create(Context context) {
        View view = View.inflate(context, R.layout.common_list_item, null);
        return new CommonItemViewHolder(view);
    }

    public void bindPojo(CommonItem item) {
        itemView.setTag(item);
        title.setText(item.getTitle());
        desc.setText(item.getDesc());
        status.setText(item.getStatus());
    }
}
