package com.yunfa365.lawservice.app.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/6/3.
 */
public class DateFormatFactory {
    public static final Map<String, DateFormat> mMap = new HashMap<String, DateFormat>();

    public static final DateFormat getDateFormat(String format) {
        DateFormat dateFormat = mMap.get(format);
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat(format);
            mMap.put(format, dateFormat);
        }
        return dateFormat;
    }

    public static final void clear() {
        mMap.clear();
    }
}
