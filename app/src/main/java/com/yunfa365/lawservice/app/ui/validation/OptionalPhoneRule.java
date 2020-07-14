package com.yunfa365.lawservice.app.ui.validation;

import android.text.TextUtils;

import com.mobsandgeeks.saripaar.AnnotationRule;

/**
 * Created by Administrator on 2016/4/22.
 */
public class OptionalPhoneRule extends AnnotationRule<OptionalPhone, String> {
    protected OptionalPhoneRule(final OptionalPhone phone) {
        super(phone);
    }

    @Override
    public boolean isValid(final String phone) {
        if (TextUtils.isEmpty(phone)) {
            return true;
        } else {
            return PhoneValidator.getInstance().isValid(phone);
        }
    }
}
