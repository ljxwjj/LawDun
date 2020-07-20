package com.yunfa365.lawservice.app.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.yunfa365.lawservice.app.BuildConfig;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.pojo.AppGlobal;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppUtil {
	private static final String PARAMETERS_SEPARATOR   = "&";
	private static final String EQUAL_SIGN             = "=";
	private static final int BLACK 					   = 0xff000000;
	private static final int WHITE 					   = 0xffffffff;
	
	/**
	 * 获取当前时间，格式为yyyy-MM-dd hh:mm:ss
	 * @return
	 */
	public static String getTime() {
		Date today=new Date();
		DateFormat f = DateFormatFactory.getDateFormat("yyyy-MM-dd HH:mm:ss");
		return f.format(today);
	}
	
	/**
	 * 获取当前运行界面的包名
	 * @param context
	 * @return
	 */
	public static String getTopPackageName(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = activityManager.getRunningTasks(1).get(0).topActivity;
		return cn.getPackageName();
	}

	/**
	 * 显示Toast
	 * @param context
	 * @param resId
	 */
	public static final void showToast(Context context, int resId) {
		Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 显示Toast
	 * @param context
	 * @param text
	 */
	public static final void showToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static final void showDialog(Context context, String text) {
		new AlertDialog.Builder(context)
				.setTitle(R.string.notice)
				.setMessage(text)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.show();
	}

	/**
	 * 获取当前应用的versionName
	 * @param context
	 * @return
	 */
	public static final String getVersionName(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			return info.versionName;
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * 获取当前运行的进程名
	 * @param context
	 * @return
	 */
	public static String getProcessName(Context context) {
		int pid = android.os.Process.myPid();
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {
				return appProcess.processName;
			}
		}
		
		return null;
	}
	

	/**
	 * 获取url所带的文件名
	 * @param url
	 * @return
	 */
	public static String urlToName(String url) {
		String name = url.substring(url.lastIndexOf(File.separator) + 1);
		int index = name.indexOf("?");
		if (index > -1) {
			name = name.substring(0, index);
		}
		name = name.replace("\\", "_")
				.replace("/", "_")
				.replace(":", "_")
				.replace("*", "_")
				.replace("?", "_")
				.replace("\"", "_")
				.replace("<", "_")
				.replace(">", "_")
				.replace("|", "_");
		
		return name;
	}
	
	/**
	 * 获取url所带的文件名
	 * @param url
	 * @param suffix
	 * @return
	 */
	public static String urlToName(String url, String suffix) {
		String name = url.substring(url.lastIndexOf(File.separator) + 1)
				.replace("\\", "_")
				.replace("/", "_")
				.replace(":", "_")
				.replace("*", "_")
				.replace("?", "_")
				.replace("\"", "_")
				.replace("<", "_")
				.replace(">", "_")
				.replace("|", "_")+ suffix;
		return name;
	}

	/**
	 * 显示loading dialog
	 * @param context
	 * @param resId
	 * @return
	 */
	public static ProgressDialog showProgressDialog(Context context, int resId) {
		String text = context.getResources().getString(resId);
		return showProgressDialog(context, text);
	}

	/**
	 * 显示loading dialog
	 * @param context
	 * @param text
	 * @return
	 */
	public static ProgressDialog showProgressDialog(Context context, String text) {
		return ProgressDialog.show(context, "", text, false, false);

//		ProgressDialog progressDialog = new ProgressDialog(context);
//
//		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//		progressDialog.setIcon(R.drawable.alert_dialog_icon);
//		progressDialog.setMessage("Loading...");
//		progressDialog.setCancelable(false);
	}

	// 获取ApiKey
	public static String getMetaValue(Context context, String metaKey) {
		Bundle metaData = null;
		String apiKey = null;
		if (context == null || metaKey == null) {
			return null;
		}
		try {
			ApplicationInfo ai = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			if (null != ai) {
				metaData = ai.metaData;
			}
			if (null != metaData) {
				apiKey = metaData.getString(metaKey);
			}
		} catch (PackageManager.NameNotFoundException e) {

		}
		return apiKey;
	}

	/*
	* 检查手机上是否安装了指定的软件
	* @param context
	* @param packageName：应用包名
	* @return
	*/
	public static boolean isAvilible(Context context, String packageName){
		//获取packagemanager
		final PackageManager packageManager = context.getPackageManager();
		//获取所有已安装程序的包信息
		List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
		//用于存储所有已安装程序的包名
		List<String> packageNames = new ArrayList<String>();
		//从pinfo中将包名字逐一取出，压入pName list中
		if(packageInfos != null){
			for(int i = 0; i < packageInfos.size(); i++){
				String packName = packageInfos.get(i).packageName;
				packageNames.add(packName);
			}
		}
		//判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
		return packageNames.contains(packageName);
	}

	public static String getAppNameByPackageName(Context context, String packageName) {
		PackageManager packageManager = null;
		ApplicationInfo applicationInfo = null;
		try {
			packageManager = context.getPackageManager();
			applicationInfo = packageManager.getApplicationInfo(packageName, 0);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return (String) packageManager.getApplicationLabel(applicationInfo);
	}

	/**
	 * 获取跳转到隐私授权的Intent
	 * @param context
	 * @return
     */
	public static Intent getAppDetailSettingIntent(Context context) {
		Intent localIntent = new Intent();
		localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (Build.VERSION.SDK_INT >= 9) {
			localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
			localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
		} else if (Build.VERSION.SDK_INT <= 8) {
			localIntent.setAction(Intent.ACTION_VIEW);
			localIntent.setClassName("com.android.settings","com.android.settings.InstalledAppDetails");
			localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
		}
		return localIntent;
	}


	/**
	 * 安装应用
	 * @param context
	 * @param filePath
	 * @return
	 */
	public static boolean installApk(Context context, String filePath) {
		File file = new File(filePath);

		if (context == null
				|| file == null
				|| !file.exists()
				|| !file.isFile()
				|| file.length() <= 0) {
			return false;
		}

		try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri data;
            //判断版本是否是 7.0 及 7.0 以上
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                data = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file);
                //添加对目标应用临时授权该Uri所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            } else {
                data = Uri.fromFile(file);
            }
            intent.setDataAndType(data, "application/vnd.android.package-archive");
            //查询所有符合 intent 跳转目标应用类型的应用，注意此方法必须放置setDataAndType的方法之后
            List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            //然后全部授权
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                context.grantUriPermission(packageName, data, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            context.startActivity(intent);
        } catch (Exception e) {
		    e.printStackTrace();
        }
		return true;
	}

	public static String generateMid() {
		if (AppGlobal.mUser == null) return "";

		String time = DateUtil.formatDate(new Date(), "yyyyMMddHHmmssSSS");
		int random = (int)(Math.random() * 100000);
		return String.format("%d_%d_%s_%d", AppGlobal.mUser.LawId, AppGlobal.mUser.ID, time, random);
	}

}
