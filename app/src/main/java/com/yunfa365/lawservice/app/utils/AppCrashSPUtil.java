package com.yunfa365.lawservice.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppCrashSPUtil {
	private static final String CRASH_PREFERENCE_NAME    = "crash_preference_name";
	private static final String CRASH_PHONE_MODEL        = "crash_phone_model";
	private static final String CRASH_PHONE_MANUFACTURER = "crash_phone_manufacturer";
	private static final String CRASH_LOG                = "crash_log";
	
	private static AppCrashSPUtil instance = null;
	private SharedPreferences preference;
	
	public static AppCrashSPUtil getInstance(Context context) {
		//if(instance == null) {
			instance = new AppCrashSPUtil(context);
		//}
		
		return instance;
	}
	
	private AppCrashSPUtil(Context context) {
		preference = context.getSharedPreferences(CRASH_PREFERENCE_NAME, Context.MODE_PRIVATE);
	}
	
	//设置崩溃手机型号
	public void setCrashPhoneModel(String model) {
		Editor editor = preference.edit();
		editor.putString(CRASH_PHONE_MODEL, model);
		editor.commit();
	}
	
	//获取崩溃手机型号
	public String getCrashPhoneModel() {
		return preference.getString(CRASH_PHONE_MODEL, "");
	}
	
	//设置崩溃手机制造商
	public void setCrashPhoneManufacturer(String manufacturer) {
		Editor editor = preference.edit();
		editor.putString(CRASH_PHONE_MANUFACTURER, manufacturer);
		editor.commit();
	}
	
	//获取崩溃手机制造商
	public String getCrashPhoneManufacturer() {
		return preference.getString(CRASH_PHONE_MANUFACTURER, "");
	}
	
	//设置崩溃日志
	public void setCrashLog(String log) {
		Editor editor = preference.edit();
		editor.putString(CRASH_LOG, log);
		editor.commit();
	}
	
	//获取崩溃日志
	public String getCrashLog() {
		return preference.getString(CRASH_LOG, "");
	}
}
