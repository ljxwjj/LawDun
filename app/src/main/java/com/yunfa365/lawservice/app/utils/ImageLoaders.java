package com.yunfa365.lawservice.app.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.yunfa365.lawservice.app.GlideApp;
import com.yunfa365.lawservice.app.R;

public class ImageLoaders {

	private static ImageLoaders mLoader;
	private Context mContext;

	/**
	 * 构造器
	 */
	private ImageLoaders() {
	}

	/**
	 * 获取CCImageLoader的单例
	 */
	public static ImageLoaders getInstance() {
		if (mLoader == null) {
			mLoader = new ImageLoaders();
		}
		return mLoader;
	}

	/**
	 * 初始化
	 */
	public void init(Context context) {
		this.mContext = context;
	}

	/**
	 * 显示指定尺寸的图片显示到ImageView中
	 */
	public void displayImage(ImageView targetView, String urlOrPath) {
		displayImage(targetView, urlOrPath, 0);
	}

	/**
	 * 显示指定尺寸的图片显示到ImageView中<BR>
	 */
	public void displayImage(ImageView targetView, String urlOrPath,
                             int defaultResId) {
		GlideApp.with(mContext).load(urlOrPath)
				.placeholder(R.drawable.app_loading_icon)
				.transforms(new CenterCrop())
				.error(R.drawable.app_null_icon)
				.into(targetView);
	}

	public void displayImageForIM(final ImageView targetView, String urlOrPath) {
		displayImageForIM(targetView, urlOrPath, 0);
	}
	
	/**
	 * 显示指定尺寸的图片显示到ImageView中<BR>
	 */
	public void displayImageForIM(final ImageView targetView, String urlOrPath,
                                  int defaultResId) {
		GlideApp.with(mContext).load(urlOrPath)
				.placeholder(defaultResId)
				.error(defaultResId)
				.apply(RequestOptions.circleCropTransform())
				.into(targetView);
	}

}
