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
import android.util.AttributeSet;

/**
 * Created by Administrator on 2016/11/25.
 */
public class TTFTextView extends androidx.appcompat.widget.AppCompatTextView {

    public TTFTextView(Context context) {
        super(context);
    }
    public TTFTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        FontUtils.setCustomFont(this, context, attrs);
    }
    public TTFTextView(Context context, AttributeSet attrs, int defStyleArrt) {
        super(context, attrs, defStyleArrt);
        FontUtils.setCustomFont(this, context, attrs);
    }

}
