package com.yunfa365.lawservice.app.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class MyGridLayout extends ViewGroup {

	public MyGridLayout(Context context) {
		super(context);
		init(context);
	}

	public MyGridLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	private void init(Context context) {

	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 获得它的父容器为它设置的测量模式和大小
		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
		int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);


		final int minimumWidth = getSuggestedMinimumWidth();
		final int minimumHeight = getSuggestedMinimumHeight();
		int computedWidth = resolveMeasured(widthMeasureSpec, minimumWidth);
		int computedHeight = resolveMeasured(heightMeasureSpec, minimumHeight);



		int childWidth = (int)((sizeWidth - 3.0) / 4 + 0.5);
		int childHeight = childWidth - 3;
//		LogUtil.d("MyGridLayout childWidth:" + childWidth + ", childHeight:" + childHeight);

		int childWidthMS = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
		int childHeightMS = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);

		int cCount = getChildCount();
		// 遍历每个子元素
		for (int i = 0; i < cCount; i++) {
			View child = getChildAt(i);
			// 得到child的lp
			LayoutParams lp = (LayoutParams) child.getLayoutParams();
			lp.width = childWidth;
			lp.height = childHeight;
			child.setLayoutParams(lp);

			// 测量每一个child的宽和高
			measureChild(child, childWidthMS, childHeightMS);
		}
		int lins = (cCount - 1)/4 + 1;
		int height = childHeight * lins + lins + 1;
		if (cCount == 0) {
			lins = 0;
			height = 0;
		}
		//setMeasuredDimension(sizeWidth, sizeHeight);
		setMeasuredDimension(computedWidth, height);
	}

	private int resolveMeasured(int measureSpec, int desired)
	{
		int result = 0;
		int specSize = MeasureSpec.getSize(measureSpec);
		switch (MeasureSpec.getMode(measureSpec)) {
			case MeasureSpec.UNSPECIFIED:
				result = desired;
				break;
			case MeasureSpec.AT_MOST:
				result = Math.min(specSize, desired);
				break;
			case MeasureSpec.EXACTLY:
			default:
				result = specSize;
		}
		return result;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
//		LogUtil.d("onLayout getHeight():" + getHeight());
		int cCount = getChildCount();
		int dip1 = 1;
		// 遍历所有的孩子
		for (int i = 0; i < cCount; i++) {
			View child = getChildAt(i);
			LayoutParams lp = (LayoutParams) child.getLayoutParams();
			int childWidth = child.getMeasuredWidth();
			int childHeight = child.getMeasuredHeight();
//			Log.d("MyGridLayout", "--onLayout childWidth:" + childWidth + ", childHeight:" + childHeight);
			
			int lc = (int)(i%4 * (childWidth + dip1) + 0.5);
			int tc = (int)(i/4*(childHeight + dip1) + 0.5) + 1;
			int rc = lc + childWidth;
			int bc = tc + childHeight;
			child.layout(lc, tc, rc, bc);
			
//			LogUtil.d("onLayout child:" + i + "   lc:" + lc + "   tc:" + tc + "    rc:" + rc + "   :bc" + bc);
		}
//		LogUtil.d("onLayout mMaxScrollY :" + mMaxScrollY);
	}

	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new MarginLayoutParams(getContext(), attrs);
	}
    
}
