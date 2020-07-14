package com.yunfa365.lawservice.app.ui.validation;

import com.mobsandgeeks.saripaar.AnnotationRule;

/**
 * Created by Administrator on 2016/4/22.
 */
public class PhoneRule extends AnnotationRule<Phone, String> {
    protected PhoneRule(final Phone phone) {
        super(phone);
    }

    @Override
    public boolean isValid(final String phone) {
        return PhoneValidator.getInstance().isValid(phone);
    }
}
