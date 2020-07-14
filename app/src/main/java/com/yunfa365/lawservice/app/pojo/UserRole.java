package com.yunfa365.lawservice.app.pojo;

import android.text.TextUtils;


import com.yunfa365.lawservice.app.utils.StringUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/19.
 */
public class UserRole {
    public String Power2;
    public String Power3;

    public int[] roleIds = new int[0];
    public Map<String, List<String>> roleIcons = new HashMap<>();

    public void parseRoles() {
        if (!TextUtils.isEmpty(Power3)) {
            String[] roleStrs = Power3.split(",");
            roleIds = new int[roleStrs.length];
            for (int i=0; i < roleStrs.length; i++) {
                try {
                    roleIds[i] = Integer.parseInt(roleStrs[i]);
                } catch (NumberFormatException e) {
                    roleIds[i] = 0;
                }
            }
            Arrays.sort(roleIds);
        }

        if (!TextUtils.isEmpty(Power2)) {
            roleIcons = StringUtil.toObject(Power2, Map.class);
        }
    }

    public boolean checkRole(int roleId) {
        return Arrays.binarySearch(roleIds, roleId) >= 0;
    }

    public List<String> getIcons(String pid) {
        if (roleIcons.containsKey(pid)) {
            return roleIcons.get(pid);
        }
        return null;
    }
}
