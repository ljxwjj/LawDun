package com.yunfa365.lawservice.app.ui.validation;

import com.mobsandgeeks.saripaar.annotation.ValidateUsing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Administrator on 2016/4/22.
 */
@ValidateUsing(OptionalPhoneRule.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OptionalPhone {
    int sequence() default -1;

    int messageResId() default -1;

    String message() default "Invalid phone number";
}
