package com.yunfa365.lawservice.app.pojo.http;

import com.android.agnetty.future.upload.form.FormUploadFile;
import com.android.agnetty.utils.MD5Util;
import com.google.gson.annotations.Expose;
import com.yunfa365.lawservice.app.pojo.AppGlobal;
import com.yunfa365.lawservice.app.utils.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppRequest {
	@Expose(serialize = false)
	private String urlPath; // must start with /

	@Expose(serialize = false)
	private HashMap<String, String> header;
	@Expose(serialize = false)
	private HashMap<String, String> param;

	private List<FormUploadFile> files;
	
	public static class Build {
		private String urlPath;

		private HashMap<String, String> header;
		private HashMap<String, String> param;
		private List<FormUploadFile> files;
		public Build(String cmd) {
			this.urlPath = cmd;
			header = new HashMap<>();
			param = new HashMap<>();
			files = new ArrayList<>();
		}

		public Build setParams(Map<String, String> params) {
			param.putAll(params);
			return this;
		}
		
		public Build addParam(String key, String value) {
			if (value == null) value = "";
			param.put(key, value);
			return this;
		}

		public Build setHeaders(Map<String, String> headers) {
			headers.putAll(headers);
			return this;
		}

		public Build addHeader(String key, String value) {
			header.put(key, value);
			return this;
		}

		public Build addFile(FormUploadFile file) {
			if (file != null) files.add(file);
			return this;
		}
		
		public AppRequest create() {
			AppRequest request = new AppRequest();
			request.urlPath = urlPath;
			request.header = header;
			request.param = param;
			request.files = files;
			return request;
		}
	}

	public String getUrlPath() {
		return this.urlPath;
	}


	public HashMap<String, String> getHeader() {
		return this.header;
	}

	public HashMap<String, String> getParam() {
		return this.param;
	}

	public FormUploadFile[] getFiles() {
		if (files.isEmpty()) return null;
		return files.toArray(new FormUploadFile[0]);
	}

	public String paramToString() {
		return StringUtil.objectToJson(param);
	}

	public void signWithForm() {
		if (AppGlobal.mUser != null) {
			header.put("app_Uid", AppGlobal.mUser.ID + "");
			header.put("app_LawId", AppGlobal.mUser.LawId + "");
		} else {
			header.put("app_Uid", "0");
			header.put("app_LawId", "0");
		}
		header.put("app_code", generateSign().toLowerCase());
	}

	public void signWithJson() {
		if (AppGlobal.mUser != null) {
			header.put("app_Uid", AppGlobal.mUser.ID + "");
			header.put("app_LawId", AppGlobal.mUser.LawId + "");
		} else {
			header.put("app_Uid", "0");
			header.put("app_LawId", "0");
		}
		header.put("app_code", generateSign().toLowerCase());
	}

	private String generateSign() {
		HashMap<String, String> allParam = new HashMap<>();
		allParam.putAll(header);
		allParam.putAll(param);

		StringBuilder strBuilder = new StringBuilder();
		String[] keys = new String[allParam.size()];
		keys = allParam.keySet().toArray(keys);
		Arrays.sort(keys, new Comparator<String>() {
			@Override
			public int compare(String lhs, String rhs) {
				return lhs.compareToIgnoreCase(rhs);
			}
		});
		for (String key : keys) {
			String value = allParam.get(key);
			strBuilder.append(key);
			strBuilder.append(value);
		}
		return MD5Util.getMD5(strBuilder.toString().toLowerCase());
	}
}
