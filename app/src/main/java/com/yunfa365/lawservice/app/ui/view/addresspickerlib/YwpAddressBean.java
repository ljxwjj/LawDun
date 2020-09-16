package com.yunfa365.lawservice.app.ui.view.addresspickerlib;

import android.content.Context;


import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.pojo.DiQu;
import com.yunfa365.lawservice.app.utils.FileUtil;
import com.yunfa365.lawservice.app.utils.StringUtil;

import java.util.List;

public class YwpAddressBean {
    private static YwpAddressBean instance;
    List<DiQu> allData;

    public static YwpAddressBean getInstance(Context context) {
        if (instance == null) instance = new YwpAddressBean(context);
        return instance;
    }

    private YwpAddressBean(Context context) {
        String diquStr = FileUtil.getRawFileContent(context.getResources(), R.raw.citys);
        allData = StringUtil.toObjectList(diquStr, DiQu.class);
    }


    public List<DiQu> getProvince() {
        return allData;
    }

    public void initAddress(DiQu[] addressTag) {
        for (DiQu s : allData) {
            if (s.id == addressTag[0].id) {
                addressTag[0] = s;
                break;
            }
        }
        if (addressTag[0].children == null) return;
        for (DiQu s : addressTag[0].children) {
            if (s.id == addressTag[1].id) {
                addressTag[1] = s;
                break;
            }
        }
        if (addressTag[1].children == null) return;
        for (DiQu s : addressTag[1].children) {
            if (s.id == addressTag[2].id) {
                addressTag[2] = s;
                break;
            }
        }
    }

    public DiQu[] initAddress(String province, String city, String district) {
        DiQu[] address = {
                new DiQu(),
                new DiQu(),
                new DiQu(),
        };
        for (DiQu s : allData) {
            if (s.text.contains(province)) {
                address[0] = s;
                break;
            }
        }
        if (address[0].children == null) return address;
        for (DiQu s : address[0].children) {
            if (s.text.contains(city)) {
                address[1] = s;
                break;
            }
        }
        if (address[1].children == null) return address;
        for (DiQu s : address[1].children) {
            if (s.text.contains(district)) {
                address[2] = s;
                break;
            }
        }
        return address;
    }
}
