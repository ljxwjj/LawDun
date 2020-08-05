package com.yunfa365.lawservice.app.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.utils.ScreenUtil;

public class BottomMenuDialog implements View.OnClickListener {
    Context mContext;
    Dialog mDialog;
    Object[] mDatas;
    DialogInterface.OnClickListener mListener;
    LinearLayout containerLayout;

    public BottomMenuDialog(Context context, Object[] items, DialogInterface.OnClickListener listener) {
        mContext = context;
        mDatas = items;
        mListener = listener;
        init();
    }
    private void init() {
        View rootView = View.inflate(mContext, R.layout.dialog_bottom_menu, null);
        containerLayout = rootView.findViewById(R.id.containerLayout);
        rootView.findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        for (Object item : mDatas) {
            View itemView = createItemView(item);
            containerLayout.addView(itemView);
        }


        mDialog = new Dialog(mContext, R.style.MyDialogStyleBottom);
        mDialog.setCancelable(true);

        Window window = mDialog.getWindow();
        WindowManager.LayoutParams attr = window.getAttributes();
        attr.gravity = Gravity.BOTTOM;
        window.setAttributes(attr);
        window.setGravity(Gravity.BOTTOM);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ScreenUtil.screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        mDialog.addContentView(rootView, params);
    }

    private View createItemView(Object item) {
        View view = View.inflate(mContext, R.layout.bottom_menu_item, null);
        TextView txt = view.findViewById(android.R.id.text1);
        txt.setText(item.toString());
        view.setOnClickListener(this);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtil.dip2px(54)));
        return view;
    }

    public void show() {
        mDialog.show();
    }

    @Override
    public void onClick(View v) {
        mDialog.dismiss();
        int which = containerLayout.indexOfChild(v);
        mListener.onClick(mDialog, which);
    }
}
