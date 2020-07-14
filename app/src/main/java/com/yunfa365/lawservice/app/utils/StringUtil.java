package com.yunfa365.lawservice.app.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.$Gson$Types;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class StringUtil extends com.android.agnetty.utils.StringUtil {
	private static final Gson sGson = new Gson();

	public static String implode(List<String> list, String glue) {
		return implode(list.toArray(new String[0]), glue);
	}
	
	public static String implode(String[] array, String glue) {
		StringBuilder stringBuilder = new StringBuilder();
		for (String str : array) {
			stringBuilder.append(str).append(glue);
		}
		int length = stringBuilder.length();
		int glueLength = glue.length();
		if (length >= glueLength) {
			stringBuilder.delete(length - glueLength, length);
		}
		return stringBuilder.toString();
	}

	public static String implode(int[] array, String glue) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int str : array) {
			stringBuilder.append(str).append(glue);
		}
		int length = stringBuilder.length();
		int glueLength = glue.length();
		if (length >= glueLength) {
			stringBuilder.delete(length - glueLength, length);
		}
		return stringBuilder.toString();
	}
	
	public static Date formatDate(String dateStr, String format) {
		DateFormat smpFormat = DateFormatFactory.getDateFormat(format);
		try {
			return smpFormat.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Date formatDate(String dateStr) {
		return formatDate(dateStr, "yyyy/MM/dd HH:mm:ss");
	}

	public static String getyyyyMMdd(String text) {
		if (TextUtils.isEmpty(text)) {
			return "";
		}
		if (text.length() <= 10) {
			return text;
		}
		return text.substring(0, 10);
	}

	public static String getyyyyMMddHHmm(String text) {
		if (TextUtils.isEmpty(text)) {
			return "";
		}
		if (text.length() <= 16) {
			return text;
		}
		return text.substring(0, 16);
	}

	public static <T> T toObject(String str, Class<T> t) throws JsonSyntaxException {
		return sGson.fromJson(str, t);
	}

	public static <T> List<T> toObjectList(String str, Class<T> t) throws JsonSyntaxException {
		Type type = getListType(List.class, t);
		List<T> list = sGson.fromJson(str, type);
		return list;
	}

	public static <T> T[] toObjectArray(String str, Class<T> t) throws JsonSyntaxException {
		Type type = $Gson$Types.canonicalize(getArrayType(t));
		T[] array = sGson.fromJson(str, type);
		return array;
	}

	public static String objectToJson(Object object) {
		return sGson.toJson(object);
	}

	private static ParameterizedType getListType(final Class raw, final Type... args) {
		return new ParameterizedType() {
			public Type getRawType() {
				return raw;
			}

			public Type[] getActualTypeArguments() {
				return args;
			}

			public Type getOwnerType() {
				return null;
			}
		};
	}

	public static final GenericArrayType getArrayType(final Class raw) {
		return new GenericArrayType() {
			@Override
			public Type getGenericComponentType() {
				return raw;
			}
		};
	}

	public static boolean isSignlessInteger(String str) {
		if (isEmpty(str)) {
			return false;
		}
		return TextUtils.isDigitsOnly(str);
	}

	public static String getJsonField(String jsonStr, String field) {
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			return jsonObject.optString(field, "");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 比较版本号的大小,前者大则返回一个正数,后者大返回一个负数,相等则返回0
	 * @param version1
	 * @param version2
	 * @return
	 */
	public static int compareVersion(String version1, String version2) throws Exception {
		if (version1 == null || version2 == null) {
			throw new Exception("compareVersion error:illegal params.");
		}
		String[] versionArray1 = version1.split("\\.");//注意此处为正则匹配，不能用"."；
		String[] versionArray2 = version2.split("\\.");
		int idx = 0;
		int minLength = Math.min(versionArray1.length, versionArray2.length);//取最小长度值
		int diff = 0;
		while (idx < minLength
				&& (diff = versionArray1[idx].length() - versionArray2[idx].length()) == 0//先比较长度
				&& (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0) {//再比较字符
			++idx;
		}
		//如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大；
		diff = (diff != 0) ? diff : versionArray1.length - versionArray2.length;
		return diff;
	}

	public static boolean isSame(String str1, String str2) {
		if (isEmpty(str1) && isEmpty(str2)) return true;
		if (str1 != null) return str1.equals(str2);
		return false;
	}

	public static String formatDouble(double num) {
		DecimalFormat decimalFormat = new DecimalFormat("#.##");//格式化设置
		return decimalFormat.format(num);
	}

    public static String formatDataSize(long size) {
		DecimalFormat formater = new DecimalFormat("####.00");
		if(size<1024){
			return size+"bytes";
		}else if(size<1024*1024){
			float kbsize = size/1024f;
			return formater.format(kbsize)+"KB";
		}else if(size<1024*1024*1024){
			float mbsize = size/1024f/1024f;
			return formater.format(mbsize)+"MB";
		}else if(size<1024*1024*1024*1024){
			float gbsize = size/1024f/1024f/1024f;
			return formater.format(gbsize)+"GB";
		}else{
			return "size: error";
		}
    }
}
