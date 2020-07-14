/*
 * ========================================================
 * Copyright(c) 2012 杭州龙骞科技-版权所有
 * ========================================================
 * 本软件由杭州龙骞科技所有, 未经书面许可, 任何单位和个人不得以
 * 任何形式复制代码的部分或全部, 并以任何形式传播。
 * 公司网址
 * 
 * 			http://www.hzdracom.com/
 * 
 * ========================================================
 */

package com.yunfa365.lawservice.app.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * @author : Zhenshui.Xia
 * @date   : 2014-3-25
 * @desc   :
 */
public class DateUtil {
	private static final String CHINESE_WEEKS[] = {"星期天", "星期一", "星期二",
		"星期三", "星期四", "星期五", "星期六"};
	/**
	 * 获取中国星期
	 * @param calendar
	 * @return
	 */
	public static final String getChineseWeek(Calendar calendar) {
        int weekIndex = calendar.get(Calendar.DAY_OF_WEEK);
        return CHINESE_WEEKS[weekIndex-1];
	}
	
	
	/**
	 * 格式化时间
	 * @param calendar
	 * @param format
	 * @return
	 */
	public static final String formatDate(Calendar calendar, String format) {
		return formatDate(calendar.getTime(), format);
	}
	
	/**
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static final String formatDate(Date date, String format){
		java.text.DateFormat f = DateFormatFactory.getDateFormat(format);
		return f.format(date);
	}
	
}
