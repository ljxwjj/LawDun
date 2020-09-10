package com.yunfa365.lawservice.app.ui.dialog;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.utils.DateUtil;
import com.yunfa365.lawservice.app.utils.StringUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * 日期时间选择控件 使用方法： private EditText inputDate;//需要设置的日期时间文本编辑框 private String
 * initDateTime="2012年9月3日 14:44",//初始日期时间值 在点击事件中使用：
 * inputDate.setOnClickListener(new OnClickListener() {
 * 
 * @Override public void onClick(View v) { DateTimePickDialogUtil
 *           dateTimePicKDialog=new
 *           DateTimePickDialogUtil(SinvestigateActivity.this,initDateTime);
 *           dateTimePicKDialog.dateTimePicKDialog(inputDate);
 * 
 *           } });
 * 
 * @author
 */
public class DateTimePickDialogUtil implements OnDateChangedListener,
		OnTimeChangedListener {
	private static final String DATETIME_FORMAT = "yyyy年MM月dd日 HH:mm";
	private Calendar mCalendar;
	private TextView mTitleView;
	private DatePicker datePicker;
	private TimePicker timePicker;
	private AlertDialog ad;
	private String dateTime;
	private String initDateTime;
	private Activity activity;
	private DateTimeChangeListener mListener;

	/**
	 * 日期时间弹出选择框构造函数
	 * 
	 * @param activity
	 *            ：调用的父activity
	 * @param initDateTime
	 *            初始日期时间值，作为弹出窗口的标题和日期时间初始值
	 */
	public DateTimePickDialogUtil(Activity activity, String initDateTime) {
		this.activity = activity;
		this.initDateTime = initDateTime;
	}

	public void init(DatePicker datePicker, TimePicker timePicker) {
		mCalendar = Calendar.getInstance();
		if (!TextUtils.isEmpty(initDateTime)) {
			try {
				Date initDate = StringUtil.formatDate(initDateTime, "yyyy-MM-dd HH:mm");
				mCalendar.setTime(initDate);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		datePicker.init(mCalendar.get(Calendar.YEAR),
				mCalendar.get(Calendar.MONTH),
				mCalendar.get(Calendar.DAY_OF_MONTH), this);
		timePicker.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
	}

	/**
	 * 弹出日期时间选择框方法
	 * 
	 * @param inputDate
	 *            :为需要设置的日期时间文本编辑框
	 * @return
	 */
	public AlertDialog dateTimePicKDialog(final TextView inputDate) {
		View customTitle = LinearLayout.inflate(activity, R.layout.dialog_title, null);
		mTitleView = (TextView) customTitle.findViewById(R.id.title);

		LinearLayout dateTimeLayout = (LinearLayout) activity
				.getLayoutInflater().inflate(R.layout.dialog_datetime_pick, null);
		datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
		timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timepicker);
		init(datePicker, timePicker);
		timePicker.setIs24HourView(true);
		timePicker.setOnTimeChangedListener(this);

		ad = new AlertDialog.Builder(activity)
				.setCustomTitle(customTitle)
				.setView(dateTimeLayout)
				.setPositiveButton("设置", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String showTime = DateUtil.formatDate(mCalendar, "yyyy-MM-dd HH:mm");
						inputDate.setText(showTime);
						if (mListener != null) {
							mListener.onDateTimeChanged(showTime);
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				}).show();

		onDateTimeChanged();
		return ad;
	}

	public AlertDialog dateTimePicKDialog(final TextView inputDate, boolean canClear) {
		AlertDialog ad = dateTimePicKDialog(inputDate);
		if (canClear) {
			ad.setButton(DialogInterface.BUTTON_NEUTRAL, "清除", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					inputDate.setText("");
				}
			});
		}
		return ad;
	}

	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		onDateTimeChanged();
	}

	public void onDateChanged(DatePicker view, int year, int monthOfYear,
							  int dayOfMonth) {
		onDateTimeChanged();
	}

	private void onDateTimeChanged() {
		mCalendar.set(datePicker.getYear(), datePicker.getMonth(),
				datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
				timePicker.getCurrentMinute());

		dateTime = DateUtil.formatDate(mCalendar, DATETIME_FORMAT);
		mTitleView.setText(dateTime);
	}

	public void setDateTimeListener(DateTimeChangeListener l) {
		mListener = l;
	}

	public static interface DateTimeChangeListener{
		public void onDateTimeChanged(String dateTime);
	}
}

