package com.yunfa365.lawservice.app.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;

import java.lang.reflect.Field;

import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

public class ScreenUtil {
	public static int   screenWidth;
	public static int   screenHeight;
	public static float density;
	public static float scaleDensity;
	public static float xdpi;
	public static float ydpi;
	public static int   densityDpi;
	public static int   screenMin;   // 宽高中，最小的值
	                                  
	public static void GetInfo(Context context) {
		if (null == context) { return; }
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getApplicationContext().getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		screenMin = (screenWidth > screenHeight) ? screenHeight : screenWidth;
		density = dm.density;
		scaleDensity = dm.scaledDensity;
		xdpi = dm.xdpi;
		ydpi = dm.ydpi;
		densityDpi = dm.densityDpi;
		//#mdebug 
		String str = "";
		str += "The absolute width:" + String.valueOf(screenWidth) + "pixels\n";
		
		str += "The absolute heightin:" + String.valueOf(screenHeight) + "pixels\n";
		str += "The logical density of the display.:" + String.valueOf(density) + "\n";
		str += "The logical density of the scaledisplay.:" + String.valueOf(scaleDensity) + "\n";
		str += "X dimension :" + String.valueOf(xdpi) + "pixels per inch\n";
		str += "Y dimension :" + String.valueOf(ydpi) + "pixels per inch\n";
		str += "The logical densityDpi of the display.:" + String.valueOf(densityDpi) + "\n";
		//#enddebug
	}
	
	
	/**
	 * dip转px
	 * 
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static final int dip2px(float dipValue) {
		final float scale = ScreenUtil.density;
		return (int) (dipValue * scale + 0.5f);
	}
	
	public static final int getStatusBarHeight(Context context){
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        } 
        return statusBarHeight;
    }

    public static final int sp2px(float spValue) {
		final float scale = ScreenUtil.scaleDensity;
		return (int) (spValue * scale + 0.5f);
	}

    public static final float px2dip(int pxValue) {
		final float scale = ScreenUtil.density;
		return (pxValue - 0.5f )/scale;
	}

	/**
	 * 隐藏导航栏
	 * @param activity
	 * @param visible
	 */
	public static void setNavigationBar(Activity activity, int visible) {
		View decorView = activity.getWindow().getDecorView();
		//显示NavigationBar
		if (View.GONE == visible) {
			int option = SYSTEM_UI_FLAG_HIDE_NAVIGATION;
			decorView.setSystemUiVisibility(option);
		}
	}
}
