package com.yunfa365.lawservice.app.ui.validation;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;

import com.mobsandgeeks.saripaar.QuickRule;

/**
 * Created by Administrator on 2016/5/27.
 */
public class NotEmptyQuickRule extends QuickRule<EditText> {
    private String message;

    public NotEmptyQuickRule(String message) {
        this.message = message;
    }

    @Override
    public boolean isValid(EditText view) {
        String text = view.getText().toString();
        if (TextUtils.isEmpty(text)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String getMessage(Context context) {
        return message;
    }
}
