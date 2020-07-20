package com.yunfa365.lawservice.app.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunfa365.lawservice.app.R;


/**
 * Created by Administrator on 2016/4/20.
 */
public class SpinnerDialog {
    Context mContext;
    AlertDialog mDialog;
    String mTitle;
    Object[] mDatas;
    DialogInterface.OnClickListener mListener;
    public SpinnerDialog(Context context, String title, Object[] items, DialogInterface.OnClickListener listener) {
        mContext = context;
        mTitle = title;
        mDatas = items;
        mListener = listener;
        init();
    }
    private void init() {
        View customTitle = LinearLayout.inflate(mContext, R.layout.dialog_title, null);
        TextView titleTxt = (TextView) customTitle.findViewById(R.id.title);
        titleTxt.setText(mTitle);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, AlertDialog.THEME_HOLO_LIGHT);
        builder.setCustomTitle(customTitle);
        builder.setCancelable(true);
        builder.setAdapter(new MyAdapter(mDatas), mListener);

        mDialog = builder.create();
    }

    public void show() {
        mDialog.show();
    }

    class MyAdapter extends BaseAdapter {
        Object items[];

        public MyAdapter(Object[] items) {
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return items[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LinearLayout.inflate(mContext, R.layout.spinner_list_item, null);
            }
            TextView txt = (TextView) convertView.findViewById(android.R.id.text1);
            txt.setText(items[position].toString());
            return convertView;
        }

    }
}
