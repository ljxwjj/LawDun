package com.yunfa365.lawservice.app.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Created by Administrator on 2017/6/26.
 */

public class ZIP {

    public static void unZipFile(String archive, String decompressDir)
    {
        File mInput = new File(archive);
        File mOutput = new File(decompressDir);
        if(!mOutput.exists()){
            if(!mOutput.mkdirs()){

            }
        }

        Enumeration<ZipEntry> entries;
        ZipFile zip = null;
        try {
            zip = new ZipFile(mInput);
            entries = (Enumeration<ZipEntry>) zip.entries();
            while(entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                if(entry.isDirectory()){
                    continue;
                }
                File destination = new File(mOutput, entry.getName());
                if(!destination.getParentFile().exists()){
                    destination.getParentFile().mkdirs();
                }

                FileOutputStream outStream = new FileOutputStream(destination);
                copy(zip.getInputStream(entry),outStream);
                outStream.close();
            }
        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                zip.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static int copy(InputStream input, OutputStream output){
        byte[] buffer = new byte[1024*8];
        BufferedInputStream in = new BufferedInputStream(input, 1024*8);
        BufferedOutputStream out  = new BufferedOutputStream(output, 1024*8);
        int count = 0, n = 0;
        try {
            while((n = in.read(buffer, 0, 1024*8)) != -1){
                out.write(buffer, 0, n);
                count += n;
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return count;
    }

}

