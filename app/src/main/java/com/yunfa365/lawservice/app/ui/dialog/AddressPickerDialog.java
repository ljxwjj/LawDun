package com.yunfa365.lawservice.app.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.pojo.DiQu;
import com.yunfa365.lawservice.app.ui.view.addresspickerlib.AddressPickerView;
import com.yunfa365.lawservice.app.utils.AppUtil;
import com.yunfa365.lawservice.app.utils.ScreenUtil;


public class AddressPickerDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private AddressPickerView addressPickerView;
    private TextView text1,text2;
    private AddressPickerListener mListener;
    private boolean fullAddress;

    public AddressPickerDialog(Context context) {
        this(context, null);
    }

    public AddressPickerDialog(Context context, DiQu[] address) {
        this(context, address, false);
    }

    public AddressPickerDialog(Context context, DiQu[] address, boolean fullAddress) {
        super(context, R.style.MyDialogStyleBottom);
        this.mContext = context;
        initDialog(context, address);
        this.fullAddress = fullAddress;
    }

    private void initDialog(Context context, DiQu[] address) {

        Window window = getWindow();
        WindowManager.LayoutParams attr = window.getAttributes();
        attr.gravity = Gravity.BOTTOM;
        window.setAttributes(attr);
        window.setGravity(Gravity.BOTTOM);

        View layout = View.inflate(context, R.layout.dialog_address_picker_view, null);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ScreenUtil.screenWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        addContentView(layout, params);

        addressPickerView = layout.findViewById(R.id.apvAddress);
        text1 = layout.findViewById(R.id.text1);
        text2 = layout.findViewById(R.id.text2);
        addressPickerView.setSelectAddress(address);
        text1.setOnClickListener(this);
        text2.setOnClickListener(this);
    }

    public AddressPickerDialog setAddressPickerListener(AddressPickerListener listener) {
        mListener = listener;
        return this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text1:
                if (mListener != null) mListener.onClear();
                break;
            case R.id.text2:
                DiQu[] addresss = addressPickerView.getSelectAddress();
                if (fullAddress) {
                    for (DiQu s : addresss) {
                        if (s == null) {
                            AppUtil.showToast(mContext, "地址还没有选完整哦");
                            return;
                        }
                    }
                }
                if (mListener != null) {
                    mListener.onSure(addresss);
                }
                break;
        }
        this.dismiss();
    }

    public interface AddressPickerListener {
        void onSure(DiQu[] address);
        void onClear();
    }
}
