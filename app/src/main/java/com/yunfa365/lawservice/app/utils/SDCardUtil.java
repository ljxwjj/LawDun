package com.yunfa365.lawservice.app.utils;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/2.
 */
public class SDCardUtil {

    /**
     * 获取外置SD卡路径
     */
    public static String getExtSDCardPath(Context context) {
        List<String> lResult = new ArrayList<String>();
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("/storage/")) {
                    String[] arr = line.split(" ");
                    String path = arr[1];
                    File file = new File(path);
                    if (file.isDirectory()) {
                        lResult.add(path);
                    }
                }
            }
            isr.close();
        } catch (Exception e) {

        }
        if (lResult.isEmpty()) {
            return "";
        } else {
            return lResult.get(0);
        }
    }

    /**
     * 获取sd卡文件的路径
     */
    public static String getSDdir(Context mContext) {
        getMountedSDCardCount1(mContext);
        return ExternalPath1;
    }


    /**
     * 工具方法
     */
    private static String ExternalPath1;
    private static String ExternalPath2;
    private static int getMountedSDCardCount1(Context context) {
        ExternalPath1 = null;
        ExternalPath2 = null;
        int readyCount = 0;
        StorageManager storageManager = (StorageManager) context
                .getSystemService(Context.STORAGE_SERVICE);
        if (storageManager == null)
            return 0;
        Method method;
        Object obj;
        try {
            method = storageManager.getClass().getMethod("getVolumePaths",
                    (Class[]) null);
            obj = method.invoke(storageManager, (Object[]) null);


            String[] paths = (String[]) obj;
            if (paths == null)
                return 0;

            method = storageManager.getClass().getMethod("getVolumeState",
                    new Class[]{String.class});
            for (String path : paths) {
                obj = method.invoke(storageManager, new Object[]{path});
                if (Environment.MEDIA_MOUNTED.equals(obj)) {
                    readyCount++;
                    if (2 == readyCount) {
                        ExternalPath1 = path;
                    }
                    if (3 == readyCount) {
                        ExternalPath2 = path;
                    }
                }
            }
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch (InvocationTargetException ex) {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                readyCount = 1;
            }
            Log.d("Test", ex.getMessage());
            return readyCount;
        }


        Log.d("Test", "mounted sdcard unmber: " + readyCount);
        return readyCount;
    }

    String getPath() {

        Runtime mRuntime = Runtime.getRuntime();
        try {
            Process mProcess = mRuntime.exec("ls /storage");
            BufferedReader mReader = new BufferedReader(new InputStreamReader(
                    mProcess.getInputStream()));
            StringBuffer mRespBuff = new StringBuffer();
            char[] buff = new char[1024];
            int ch = 0;
            while ((ch = mReader.read(buff)) != -1) {
                mRespBuff.append(buff, 0, ch);
            }
            mReader.close();
            String[] result = mRespBuff.toString().trim().split("\n");
            for (String str : result) {
                if (str.equals("emulate") || str.equals("self"))
                    continue;
                return str;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }
}
