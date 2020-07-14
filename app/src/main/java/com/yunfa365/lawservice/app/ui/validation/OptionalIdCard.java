package com.yunfa365.lawservice.app.ui.validation;


import com.mobsandgeeks.saripaar.annotation.ValidateUsing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ValidateUsing(OptionalIdCardRule.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OptionalIdCard {

    int sequence() default -1;

    int messageResId() default -1;

    String message() default "Invalid email";

}


