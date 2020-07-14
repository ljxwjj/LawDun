package com.yunfa365.lawservice.app.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** 
* @ClassName: BitmapTools 
* @Description: 图片工具类
* @author Lu Hai 
* @date 2012-10-9 上午10:59:40 
**/

/**
* @ClassName: BitmapTools 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author Lu Hai 
* @date 2012-10-9 上午11:00:18 
**/
public final class BitmapTools {

	/** 
	* @Title: getBitmap 
	* @Description: 根据输入流，获取制定缩放比例的图片 
	* @param @param is 输入流
	* @param @return 设定文件 
	* @return Bitmap 返回类型 
	* @throws 
	* @date 2012-10-9 上午10:59:22 
	*/
	public static Bitmap getBitmap(InputStream is) {
		return BitmapFactory.decodeStream(is);
	}

	/** 
	* @Title: getBitmap 
	* @Description: 根据输入流，获取制定缩放比例的图片 
	* @param @param is 输入流
	* @param @param scale 缩放比
	* @param @return 设定文件 
	* @return Bitmap 返回类型 
	* @throws 
	* @date 2012-10-9 上午10:58:14 
	*/
	public static Bitmap getBitmap(InputStream is, int scale) {
		Options options = new Options();
		options.inSampleSize = scale;
		Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
		return bitmap;
	}

	/** 
	* @Title: getBitmap 
	* @Description:根据路径获取图片
	* @param @param path 图片路径
	* @param @return 设定文件 
	* @return Bitmap 返回类型 
	* @throws 
	* @date 2012-10-9 上午10:57:11 
	*/
	public static Bitmap getBitmap(String path) {
		return BitmapFactory.decodeFile(path);
	}
	
	/** 
	* @Title: getBitmap 
	* @Description:根据路径获取图片,获取制定宽高缩放的图片 
	* @param @param path 图片路径
	* @param @param width 宽
	* @param @param height 高
	* @param @return 设定文件 
	* @return Bitmap 返回类型 
	* @throws 
	* @date 2012-10-9 上午11:00:20 
	*/
	public static Bitmap getBitmap(String path, int width, int height){
		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		int scaleX = options.outWidth / width;
		int scaleY = options.outHeight / height;
		int scale = scaleX > scaleY ? scaleX : scaleY;
		options.inJustDecodeBounds = false;
		options.inSampleSize = scale;
		return BitmapFactory.decodeFile(path, options);
	}

	
	/** 
	* @Title: getBitmap 
	* @Description:根据图片数据字节数组获取图片,获取制定宽高缩放的图片
	* @param @param bytes 图片字节数组
	* @param @param width 宽
	* @param @param height 高
	* @param @return 设定文件 
	* @return Bitmap 返回类型 
	* @throws 
	* @date 2012-10-9 上午11:01:12 
	*/
	public static Bitmap getBitmap(byte[] bytes, int width, int height) {
		if(bytes == null) return null;
		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
		int scaleX = options.outWidth / width;
		int scaleY = options.outHeight / height;
		int scale = scaleX > scaleY ? scaleX : scaleY;
		options.inJustDecodeBounds = false;
		options.inSampleSize = scale;
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
	}


	/** 
	* @Title: saveBitmap 
	* @Description: 保存图片
	* @param @param path
	* @param @param bitmap
	* @param @throws IOException 设定文件 
	* @return void 返回类型 
	* @throws 
	* @date 2012-10-9 上午11:01:19 
	*/
	public static void saveBitmap(String path, Bitmap bitmap)
			throws IOException {
		if (path != null && bitmap != null) {
			File file = new File(path);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			OutputStream out = new FileOutputStream(file);
			String fileType = file.getName().substring(
					file.getName().lastIndexOf(".") + 1);
			if ("png".equals(fileType)) {
				bitmap.compress(CompressFormat.PNG, 100, out);
			}else{
				bitmap.compress(CompressFormat.JPEG, 100, out);
			}
			out.close();
		}
	}
	
	
	public static Bitmap circleBitmap(Bitmap bitmap) {
		if(bitmap == null ) return null;
		
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        float radius = w/2f;
        Bitmap output = createBitmap(w, h, Config.ARGB_8888);
        try
	    {
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, radius, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
	    }catch(Exception e)
	    {}
        return output;
    }
	
	public static Bitmap zoomImage(Bitmap bgimage, float newWidth, float newHeight) {
	    float width = bgimage.getWidth(); 
	    float height = bgimage.getHeight();
	    float cc = height/width;
	    
	    Bitmap output = null;
	    try
	    {
		if (cc < newHeight/newWidth) {
			float zoomHeight = newHeight;
	    	float zoomWidth = newHeight/cc;

		    output = createBitmap((int)newWidth, (int)newHeight, Config.ARGB_8888);
		    Canvas canvas = new Canvas(output);
		    
		    Paint paint = new Paint();
		    int x = (int) ((newWidth - zoomWidth)/2);
		    int y = (int) ((newHeight - zoomHeight)/2);
	        final Rect src = new Rect(0, 0, (int)width, (int)height);
	        final Rect dst = new Rect(x, y, (int)(zoomWidth + x), (int)(zoomHeight + y));
		    canvas.drawBitmap(bgimage, src, dst, paint);
		} else {
			float zoomWidth = newWidth;
			float zoomHeight = newWidth * cc;

		    output = createBitmap((int)newWidth, (int)newHeight, Config.ARGB_8888);
		   
		    Canvas canvas = new Canvas(output);
		    
		    Paint paint = new Paint();
		    int x = (int) ((newWidth - zoomWidth)/2);
		    int y = (int) ((newHeight - zoomHeight)/2);
	        final Rect src = new Rect(0, 0, (int)width, (int)height);
	        final Rect dst = new Rect(x, y, (int)(zoomWidth + x), (int)(zoomHeight + y));
		    canvas.drawBitmap(bgimage, src, dst, paint);
		}
	    }catch(Exception e)
	    {
	    	
	    }
	    return output; 
	} 
	
	public static Bitmap createBitmap(int width, int height, Config config) {
	    Bitmap bitmap = null;
	    try {
	        bitmap = Bitmap.createBitmap(width, height, config);
	    } catch (OutOfMemoryError e) {
	        while(bitmap == null) {
	            System.gc();
	            System.runFinalization();
	            bitmap = createBitmap(width, height, config);
	        }
	    }
	    return bitmap;
	}

	public static byte[] bitmapToBytes(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 100, baos);
		byte[] bitmapByte = baos.toByteArray();
		return bitmapByte;
	}
	
	
}
