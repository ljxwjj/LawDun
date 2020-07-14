package com.yunfa365.lawservice.app.ui.validation;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/4/22.
 */
public class PhoneValidator implements Serializable {
    private static final PhoneValidator PHONE_VALIDATOR = new PhoneValidator();

    private static final String PHONE_REGEX = "^(1\\d{10})|((0\\d{2,3}\\-?)?\\d{7,8})$";
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);

    public static PhoneValidator getInstance() {
        return PHONE_VALIDATOR;
    }

    public boolean isValid(String phone) {
        if (phone == null) {
            return false;
        }
        Matcher emailMatcher = PHONE_PATTERN.matcher(phone);
        if (!emailMatcher.matches()) {
            return false;
        }
        return true;
    }
}
