package com.yunfa365.lawservice.app.ui.validation;

import android.text.TextUtils;

import com.mobsandgeeks.saripaar.AnnotationRule;
import com.yunfa365.lawservice.app.utils.IdcardUtils;

public class OptionalIdCardRule extends AnnotationRule<OptionalIdCard, String> {
    protected OptionalIdCardRule(final OptionalIdCard idCard) {
        super(idCard);
    }

    @Override
    public boolean isValid(final String idCard) {
        if (TextUtils.isEmpty(idCard)) {
            return true;
        } else {
            return IdcardUtils.validateCard(idCard);
        }
    }

}
