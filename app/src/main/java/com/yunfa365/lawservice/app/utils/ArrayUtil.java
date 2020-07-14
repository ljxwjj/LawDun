package com.yunfa365.lawservice.app.utils;

import java.util.List;

public class ArrayUtil {
    public static int replace(List list, Object t) {
        int count = list.size();
        for (int i = 0; i < count; i++) {
            Object s = list.get(i);
            if (s.equals(t) && s != t) {
                list.remove(i);
                list.add(i, t);
                return i;
            }
        }
        return -1;
    }
    public static void replace(List list, int i, Object t) {
        list.remove(i);
        list.add(i, t);
    }
    public static void replace(List list, Object s, Object t) {
        int index = list.indexOf(s);
        list.remove(index);
        list.add(index, t);
    }
}
