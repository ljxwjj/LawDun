package com.yunfa365.lawservice.app.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.yunfa365.lawservice.app.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/21.
 */
public class MultiSelectDialog<T> {
    Context mContext;
    AlertDialog mDialog;
    String mTitle;
    T[] mDatas;

    private ListView mListView;
    private MyAdapter mAdapter;
    private MultiSelectListener mListener;
    boolean[] mCheckedItems;

    public MultiSelectDialog(Context context, String title, T[] items, boolean[] checkedItems, MultiSelectListener listener) {
        mContext = context;
        mTitle = title;
        mDatas = items;
        mCheckedItems = checkedItems;
        mListener = listener;
        init();
    }
    private void init() {
        View customTitle = LinearLayout.inflate(mContext, R.layout.dialog_title, null);
        TextView titleTxt = (TextView) customTitle.findViewById(R.id.title);
        titleTxt.setText(mTitle);

        mAdapter = new MyAdapter(mDatas);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, AlertDialog.THEME_HOLO_LIGHT);
        builder.setCustomTitle(customTitle);
        builder.setCancelable(true);
        builder.setAdapter(mAdapter, null);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onOkClick(MultiSelectDialog.this);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onCancelClick(MultiSelectDialog.this);
            }
        });

        mDialog = builder.create();
        mListView = mDialog.getListView();
        mListView.setItemsCanFocus(false);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //mListView.setItemChecked(position, !mListView.isItemChecked(position));
                if (mCheckedItems != null) {
                    mCheckedItems[position] = mListView.isItemChecked(position);
                }
                mListener.onItemClick(
                        MultiSelectDialog.this, position, mListView.isItemChecked(position));
            }
        });
    }

    public void show() {
        mDialog.show();
    }

    public T[] getSelectedItem() {
        List<T> selected = new ArrayList<T>();
        for (int i = 0; i < mDatas.length; i++) {
            if (mCheckedItems[i])
                selected.add(mDatas[i]);
        }
        T[] newArray
                = (T[]) Array.newInstance(mDatas.getClass().getComponentType(), selected.size());
        return selected.toArray(newArray);
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
                convertView = LinearLayout.inflate(mContext, R.layout.multiselect_list_item, null);
            }
            TextView txt = (TextView) convertView.findViewById(android.R.id.text1);
            txt.setText(items[position].toString());

            if (mCheckedItems != null) {
                boolean isItemChecked = mCheckedItems[position];

                mListView.setItemChecked(position, isItemChecked);

            }
            return convertView;
        }

    }

    public interface MultiSelectListener {
        public void onItemClick(MultiSelectDialog dialog, int position, boolean checked);
        public void onOkClick(MultiSelectDialog dialog);
        public void onCancelClick(MultiSelectDialog dialog);
    }
}
