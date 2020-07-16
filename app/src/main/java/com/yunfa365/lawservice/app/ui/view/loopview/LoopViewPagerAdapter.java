package com.yunfa365.lawservice.app.ui.view.loopview;

import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class LoopViewPagerAdapter extends BasePagerAdapter {
	private static final String TAG = "LoopViewPagerAdapter";
	private static final boolean DEBUG = false;
	private BasePagerAdapter adapter;
	
	public LoopViewPagerAdapter(BasePagerAdapter adapter) {
		super();
		this.adapter = adapter;
	}

	@Override
	public int getCount() {
		if (adapter.getCount() <= 1) {
			return adapter.getCount();
		} else {
			return Integer.MAX_VALUE;
		}
	}
	
	public int getRealCount() {
		return adapter.getCount();
	}

	public int getRealItemPosition(Object object) {
		return adapter.getItemPosition(object);
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		int realPosition = position % getRealCount();
		debug("destroyItem", "realPosition : " + realPosition);
		debug("destroyItem", "position : " + position);
		adapter.destroyItem(container, realPosition, object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		int realPosition = position % getRealCount();
		debug("instantiateItem", "realPosition : " + realPosition);
		debug("instantiateItem", "position : " + position);
		return adapter.instantiateItem(container, realPosition);
	}

	/*
	 * start 传�?给包装adapter
	 */
	@Override
	public void finishUpdate(ViewGroup container) {
		adapter.finishUpdate(container);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return adapter.isViewFromObject(view, object);
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
		adapter.restoreState(state, loader);
	}

	@Override
	public Parcelable saveState() {
		return adapter.saveState();
	}

	@Override
	public void startUpdate(ViewGroup container) {
		adapter.startUpdate(container);
	}

	private void debug(String... info) {
		if (DEBUG) {
			Log.i(TAG, info[0] + " ======>>> " + info[1]);
		}
	}

}
