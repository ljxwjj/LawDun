/*
 * ========================================================
 * Copyright(c) 2012 杭州龙骞科技-版权所有
 * ========================================================
 * 本软件由杭州龙骞科技所有, 未经书面许可, 任何单位和个人不得以
 * 任何形式复制代码的部分或全部, 并以任何形式传播。
 * 公司网址
 * 
 * 			http://www.hzdracom.com/
 * 
 * ========================================================
 */

package com.yunfa365.lawservice.app.ui.view.loopview;


import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * @author : Zhenshui.Xia
 * @date   : 2014-3-10
 * @desc   : 
 */
public class LoopPageIndicator extends LinearLayout{
	
	private Context mContext;
    
    //其他页面标识
    private int mNormalDrawable;
    //当前页面标识
    private int mFocusDrawable;
    
    //总页面数
    private int mTotalPage=0;
    //当前页面
    private int mCurPageNum=0;
    

	public LoopPageIndicator(Context context) {
		super(context);
		this.mContext = context;
        init();
	}
	
	public LoopPageIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
        init();
	}

    private void init() {
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER);
    }
	
	public void initIndicator(int curPage, int totalPage, int normalDrawable, int focusDrawable) {
	    if (totalPage == 1) {
	        totalPage = 0;
        }
        if (totalPage != mTotalPage) {
            initChildren(totalPage);
        }
		this.mTotalPage = totalPage;
		this.mNormalDrawable = normalDrawable;
		this.mFocusDrawable = focusDrawable;
		refreshIndicator(curPage);
	}

    private void initChildren(int totalPage) {
        if (getChildCount() < totalPage) {
            for (int i = getChildCount(); i < totalPage; i++) {
                LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                layoutParams.leftMargin=3;
                layoutParams.rightMargin=3;

                //新建ImageView，用来显示分页图标
                ImageView newPageImageView = new ImageView(mContext);

                addView(newPageImageView, layoutParams);
            }
        } else if (getChildCount() > totalPage) {
            for (int i = getChildCount(); i > totalPage; i--) {
                removeViewAt(i-1);
            }
        }
    }
	
	public void refreshIndicator(int curPage) {
        mCurPageNum = curPage;

        //添加分页指导器
        for (int i=0; i< mTotalPage; i++) {
            ImageView imageView = (ImageView) getChildAt(i);
            //按照当前页还是其他页面设置对应图标
            if(i == mCurPageNum){
                imageView.setImageResource(mFocusDrawable);
            } else {
                imageView.setImageResource(mNormalDrawable);
            }
        }
    }

}
