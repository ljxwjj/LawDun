package com.yunfa365.lawservice.app.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BitmapUtil {

	/*
	 * bitmap转base64
	 */
	public static final String bitmapToBase64(Bitmap bitmap) {
		String result = "";
		ByteArrayOutputStream bos = null;
		try {
			if (null != bitmap) {
				bos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);// 将bitmap放入字节数组流中

				bos.flush();// 将bos流缓存在内存中的数据全部输出，清空缓存
				bos.close();

				byte[] bitmapByte = bos.toByteArray();
				result = Base64.encodeToString(bitmapByte, Base64.DEFAULT);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/*
	 * bitmap转base64
	 */
	public static final Bitmap base64ToBitmap(String base64String) {
		byte[] bytes = Base64.decode(base64String, Base64.DEFAULT);
		Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		return bitmap;
	}

	public static void displayText(ImageView view, String fullName) {
		int w = ScreenUtil.dip2px(42);
		int h = ScreenUtil.dip2px(42);
		Bitmap output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xffacd5dc;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, w, h);
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, w/2, h/2, paint);

		paint.setTextSize(ScreenUtil.sp2px(25));
		paint.setColor(0xffffffff);
		paint.setStyle(Paint.Style.FILL);
		paint.setTextAlign(Paint.Align.CENTER);
		Paint.FontMetrics fr = paint.getFontMetrics();
		float fontHeight = fr.bottom + fr.top;
		float testBaseY = (h - fontHeight)/2;
		canvas.drawText(fullName.substring(0, 1), w/2, testBaseY, paint);

		view.setImageBitmap(output);
	}
}
