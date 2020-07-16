package com.yunfa365.lawservice.app.ui.view.loopview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class LoopViewPager extends BaseViewPager {
	private int sleepTime = 3000;
	private static boolean isFinish;
	private static boolean isRunning;
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				//只有一个广告页面时 不启动自动切换页面
				if(getAdapter() instanceof LoopViewPagerAdapter){
					LoopViewPagerAdapter adapter = (LoopViewPagerAdapter) getAdapter();
					if(adapter.getRealCount() <= 1){
						break;
					}
				}
				setCurrentItem(getCurrentItem() + 1,
						true);
				if (isRunning && !isFinish) {
					this.sendEmptyMessageDelayed(0, sleepTime);
				}
				break;

			case 1:
				if(isRunning && !isFinish) {
					this.sendEmptyMessageDelayed(0, sleepTime);
				}
				break;
			}
		}
	};
	
	public LoopViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LoopViewPager(Context context) {
		super(context);
		
	}

	@Override
	public void setAdapter(BasePagerAdapter adapter) {
		super.setAdapter(adapter);
		//设置当前展示的位�?
		setCurrentItem(0);
	}
	
	public void setInfinateAdapter(BasePagerAdapter adapter){
		setAdapter(adapter);
	}
	
	public void startScroll() {
		isRunning = true;
		handler.sendEmptyMessageDelayed(0, sleepTime);
	}
	
	public void cancelScroll() {
		isRunning = false;
		handler.removeCallbacksAndMessages(null);
	}
	
	@Override
	public void setCurrentItem(int item) {
		item = getOffsetAmount() + (item % getAdapter().getCount());
		super.setCurrentItem(item);
	}
	/**
	 * �?�?��向右(负向滑动)可以滑动的次�?
	 * @return
	 */
	private int getOffsetAmount() {
		if (getAdapter() instanceof LoopViewPagerAdapter) {
			LoopViewPagerAdapter infiniteAdapter = (LoopViewPagerAdapter) getAdapter();
			// 允许向前滚动400000�?
			return infiniteAdapter.getRealCount() * 100000;
		} else {
			return 0;
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			isRunning = false;
			isFinish = true;
			handler.removeCallbacksAndMessages(null);
//			System.out.println("InfiniteLoopViewPager  dispatchTouchEvent =====>>> ACTION_DOWN");
		} else if (action == MotionEvent.ACTION_UP) {
			isRunning = true;
			isFinish = false;
			handler.removeCallbacksAndMessages(null);
			handler.sendEmptyMessage(1);
//			System.out.println("InfiniteLoopViewPager  dispatchTouchEvent =====>>> ACTION_UP");
		}
		return super.dispatchTouchEvent(ev);
	}
	
	@Override
	public void setOffscreenPageLimit(int limit) {
		super.setOffscreenPageLimit(limit);
	}
	

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        //每次进行onTouch事件都记录当前的按下的坐标
        if(arg0.getAction() == MotionEvent.ACTION_MOVE){
            //此句代码是为了通知他的父ViewPager现在进行的是本控件的操作，不要对我的操作进行干扰
            getParent().requestDisallowInterceptTouchEvent(true);
        }

        return super.onTouchEvent(arg0);
    }
}
