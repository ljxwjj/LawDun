
package com.yunfa365.lawservice.app.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yunfa365.lawservice.app.GlideApp;
import com.yunfa365.lawservice.app.pojo.BannerInfo;
import com.yunfa365.lawservice.app.ui.activity.WebActivity_;
import com.yunfa365.lawservice.app.ui.view.loopview.BasePagerAdapter;

import java.util.List;

public class BannerAdapter extends BasePagerAdapter {
	private Context mContext;
	private List<BannerInfo> mInfos;

	public BannerAdapter(Context context, List<BannerInfo> infos) {
		this.mContext = context;
		this.mInfos = infos;
	}

	@Override
	public int getCount() {
		return mInfos==null? 0: mInfos.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == (View) object;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		final BannerInfo info = mInfos.get(position);
		ImageView asyncImgBanner = new ImageView(mContext);
		asyncImgBanner.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final String url = info.Links;
				if (TextUtils.isEmpty(url)) return;
				try {
					if (url.startsWith("http")) {
						WebActivity_.intent(mContext).url(url).start();
					} else {
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
						mContext.startActivity(intent);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	
		asyncImgBanner.setScaleType(ScaleType.FIT_XY);
        container.addView(asyncImgBanner);
		if (info.localImage > 0) {
			asyncImgBanner.setImageResource(info.localImage);
		} else {
			GlideApp.with(mContext)
					.load(info.FilePath)
					.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
					.centerCrop()
					.into(asyncImgBanner);
		}
		return asyncImgBanner;
	}
}
