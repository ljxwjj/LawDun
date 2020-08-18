package com.yunfa365.lawservice.app.pojo.http;


import com.yunfa365.lawservice.app.utils.StringUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class AppResponse {
	/**
	 * 状态  0：成功   1：参数有误或未经过参数验证  2：请求失败
	 */
	public boolean flag;
	public String Code;
	/**
	 * 返回信息(当请求返回状态不为0时的提示信息，请求成功时此字段返回空)
	 */
	public String Message;
	/**
	 *  当请求成功时，返回数据(同样为json格式)
	 */
	public String RData;

	/**
	 * 响应结果原始字符串
	 */
	public String response;

	public <T> T resultsToObject(Class<T> t) {
		try {
			return StringUtil.toObject(RData, t);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			return t.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public <T> List<T> resultsToList(Class<T> t) {
		List list = null;
		try {
			list = StringUtil.toObjectList(RData, t);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (list == null) {
			list = new ArrayList();
		}
		return list;
	}

	public <T> T[] resultsToArray(Class<T> t) {
		try {
			return StringUtil.toObjectArray(RData, t);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (T[]) Array.newInstance(t.getComponentType(), 0);
	}

	public <T> T getFirstObject(Class<T> t) {
		try {
			T[] array = resultsToArray(t);
			return array[0];
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			return t.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
