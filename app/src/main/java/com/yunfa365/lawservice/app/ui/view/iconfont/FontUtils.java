/*
 * ========================================================
 * Copyright(c) 2016 杭州律联科技-版权所有
 * ========================================================
 * 本软件由杭州律联科技所有, 未经书面许可, 任何单位和个人不得以
 * 任何形式复制代码的部分或全部, 并以任何形式传播。
 * 公司网址
 * 
 * 			http://www.elvshi.com/
 * 
 * ========================================================
 */
package com.yunfa365.lawservice.app.ui.view.iconfont;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;


import com.yunfa365.lawservice.app.R;

import java.util.Hashtable;

/**
 * Created by Administrator on 2016/11/25.
 */
public class FontUtils {

    private static Hashtable<String, Typeface> fontCache = new Hashtable<String, Typeface>();

    /**
     * Sets a font on a textview based on the custom com.my.package:font attribute
     * If the custom font attribute isn't found in the attributes nothing happens
     * @param textview
     * @param context
     * @param attrs
     */
    public static void setCustomFont(TextView textview, Context context, AttributeSet attrs) {
        TypedArray attributes = null;
        String fontName;
        try {
            attributes = context.obtainStyledAttributes(attrs, R.styleable.CustomFont);
            fontName = attributes.getString(R.styleable.CustomFont_ttf_font);
            setCustomFont(textview, fontName, context);
        } finally {
            if(attributes != null) {
                attributes.recycle();
            }
        }
    }

    /**
     * Sets a font on a textview
     * @param textview
     * @param font
     * @param context
     */
    public static void setCustomFont(TextView textview, String font, Context context) {
        if(font == null) {
            return;
        }
        Typeface tf = getFromCache(font, context);
        if(tf != null) {
            textview.setTypeface(tf);
        }
    }

    public static Typeface getFromCache(String name, Context context) {
        if(!name.endsWith(".tff")) {
            name = name + ".ttf";
        }
        Typeface tf = fontCache.get(name);
        if(tf == null) {
            try {
                tf = Typeface.createFromAsset(context.getAssets(), "fonts/"+name);
            }
            catch (Exception e) {
                return null;
            }
            fontCache.put(name, tf);
        }
        return tf;
    }
}
