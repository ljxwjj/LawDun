package com.yunfa365.lawservice.app.utils;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * @author : Zhenshui.Xia
 * @date   : 2013-9-19
 * @desc   : 文件工具类
 * 
 * public method
 * 	<li>createFile(String)			创建文件 </li>
 * 	<li>makeDirs(String)			根据文件路径，创建目录</li>
 * 	<li>makeFolders(String)			根据文件夹路径，创建目录 </li>
 */
public class FileUtil {

    public final static String FILE_EXTENSION_SEPARATOR = ".";

    public static byte[] inputStream2byte(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int rc = 0;
        while ((rc = inputStream.read(buff, 0, 1024)) > 0) {
            byteArrayOutputStream.write(buff, 0, rc);
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    public static StringBuilder readFile(String filePath, String charsetName) throws FileNotFoundException {
        File file = new File(filePath);
        StringBuilder fileContent = new StringBuilder("");
        if (file == null || !file.isFile()) {
            throw new FileNotFoundException("找不到文件" + filePath);
        }

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line);
                fileContent.append("\r\n");
            }
            reader.close();
            return fileContent;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

   
    public static boolean writeFile(String filePath, String content, boolean append) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filePath, append);
            fileWriter.write(content);
            fileWriter.close();
            return true;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    
    public static boolean writeFile(String filePath, InputStream stream) {
        OutputStream o = null;
        try {
            o = new FileOutputStream(filePath);
            byte data[] = new byte[1024];
            int length = -1;
            while ((length = stream.read(data)) != -1) {
                o.write(data, 0, length);
            }
            o.flush();
            return true;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException occurred. ", e);
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (o != null) {
                try {
                    o.close();
                    stream.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    
    public static List<String> readFileToList(String filePath, String charsetName) {
        File file = new File(filePath);
        List<String> fileContent = new ArrayList<String>();
        if (file == null || !file.isFile()) {
            return null;
        }

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                fileContent.add(line);
            }
            reader.close();
            return fileContent;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

   
    public static String getFileNameWithoutExtension(String filePath) {
        if (StringUtil.isEmpty(filePath)) {
            return filePath;
        }

        int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePosi = filePath.lastIndexOf(File.separator);
        if (filePosi == -1) {
            return (extenPosi == -1 ? filePath : filePath.substring(0, extenPosi));
        }
        if (extenPosi == -1) {
            return filePath.substring(filePosi + 1);
        }
        return (filePosi < extenPosi ? filePath.substring(filePosi + 1, extenPosi) : filePath.substring(filePosi + 1));
    }

   
    public static String getFileName(String filePath) {
        if (StringUtil.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? filePath : filePath.substring(filePosi + 1);
    }

    
    

    
    public static String getFileExtension(String filePath) {
        if (StringUtil.isBlank(filePath)) {
            return filePath;
        }

        int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePosi = filePath.lastIndexOf(File.separator);
        if (extenPosi == -1) {
            return "";
        }
        return (filePosi >= extenPosi) ? "" : filePath.substring(extenPosi + 1);
    }

    
    

    
    public static boolean isFileExist(String filePath) {
        if (StringUtil.isBlank(filePath)) {
            return false;
        }

        File file = new File(filePath);
        return (file.exists() && file.isFile());
    }

    
    public static boolean isFolderExist(String directoryPath) {
        if (StringUtil.isBlank(directoryPath)) {
            return false;
        }

        File dire = new File(directoryPath);
        return (dire.exists() && dire.isDirectory());
    }

   
    public static boolean deleteFile(String path) {
        if (StringUtil.isBlank(path)) {
            return true;
        }

        File file = new File(path);
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                f.delete();
            } else if (f.isDirectory()) {
                deleteFile(f.getAbsolutePath());
            }
        }
        return file.delete();
    }

    
    public static long getFileSize(String path) {
        if (StringUtil.isBlank(path)) {
            return -1;
        }

        File file = new File(path);
        return (file.exists() && file.isFile() ? file.length() : -1);
    }
    
    
	
	
	
	
	
	
	
	
	
	
	/**
	 * 文件拷贝
	 * @param source 源文件
	 * @param dest	  目标文件
	 * @return
	 */
	public static boolean copyFile(String source, String dest) {
		try { 
           if (FileUtil.isFileExist(source) && StringUtil.isNotBlank(dest)) { 
               FileInputStream fileIn = new FileInputStream(source);
               FileOutputStream fileOut = new FileOutputStream(dest);
               byte[] buffer = new byte[8192]; 
               int length = 0; 
               while ( (length = fileIn.read(buffer)) != -1) { 
                   fileOut.write(buffer, 0, length); 
               }
               
               fileIn.close(); 
               fileOut.close();
               return true;
           } 
        } catch (Exception ex) {
        	ex.printStackTrace(); 
        } 
		
		return false;
	}
	
	/**
	 * 拷贝assets文件夹下的文件
	 * @param context   上下文
	 * @param source	assets文件夹下源文件
	 * @param dest		目标文件
	 * @return
	 */
	public static boolean copyAssetsFile(Context context, String source, String dest) {
		try { 
			if (context!=null && StringUtil.isNotBlank(dest)) {
	            BufferedInputStream bufferIn = new BufferedInputStream(context.getResources().getAssets().open(source));
	            FileOutputStream fileOut = new FileOutputStream(dest);
	            byte[] buffer = new byte[8192]; 
	            int length = 0; 
	            while ( (length = bufferIn.read(buffer)) != -1) { 
	               fileOut.write(buffer, 0, length); 
	            }
	           
	            bufferIn.close(); 
	            fileOut.close();
	            return true;
			}
        } catch (Exception ex) {
        	ex.printStackTrace(); 
        } 
		
		return false;
	}

    public static boolean copyAssetsDir(Context context, String src, String des) {
        boolean isSuccess = true;
        String[] files;
        try {
            files = context.getResources().getAssets().list(src);
        } catch (IOException e) {
            return false;
        }
        if (files.length == 0) {
            isSuccess = copyAssetsFile(context, src, des + "/" + src);
            if (!isSuccess)
                return isSuccess;
        } else {
            File srcfile = new File(des + "/" + src);
            if (!srcfile.exists()) {
                if (srcfile.mkdir()) {
                    for (int i = 0; i < files.length; i++) {
                        isSuccess = copyAssetsDir(context, src + "/" + files[i], des);
                        if (!isSuccess)
                            return isSuccess;
                    }
                } else {
                    return false;
                }
            }
        }
        return isSuccess;
    }
	
	public static String getFolderName(String filePath) {
        if (StringUtil.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? "" : filePath.substring(0, filePosi);
    }
	
	
	
	
	
	
	/**
	 * 创建文件
	 * @param filePath
	 * @return
	 */
	public static boolean createFile(String filePath) {
		if(StringUtil.isEmpty(filePath)) return false;
		
		File file = new File(filePath);
		if(file.exists()) return true;
		
		boolean result = false;
		try {
			if(makeDirs(filePath)) result = file.createNewFile();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 根据文件路径，创建目录
	 * @param filePath
	 * @return
	 */
	public static boolean makeDirs(String filePath) {
        String folderName = getFolderName(filePath);
        if (StringUtil.isEmpty(folderName)) {
            return false;
        }

        File folder = new File(folderName);
        return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
    }

    /**
     * 根据文件夹路径，创建目录
     * @param fileDir
     * @return
     */
    public static boolean makeFolders(String fileDir) {
    	if (StringUtil.isEmpty(fileDir)) {
            return false;
        }

        File folder = new File(fileDir);
        return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
    }
    

    public static String getRawFileContent(Resources resources, int raw) {
        StringBuffer content = new StringBuffer();
        InputStreamReader inputReader = null;
        BufferedReader bufReader = null;
        try{
            inputReader = new InputStreamReader(resources.openRawResource(raw));
            bufReader = new BufferedReader(inputReader);
            String line;
            while ((line = bufReader.readLine()) != null)
                content.append(line);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(bufReader != null)
            {
                try{
                    bufReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputReader != null) {
                try {
                    inputReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content.toString();
    }

    public static String getAssetsFileContent(Resources resources, String fileName) {
        StringBuffer content = new StringBuffer();
        String line;
        InputStreamReader inputReader = null;
        BufferedReader bufReader = null;
        try {
            inputReader = new InputStreamReader(resources.getAssets().open(fileName));
            bufReader = new BufferedReader(inputReader);
            while((line = bufReader.readLine()) != null)
                content.append(line);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufReader != null) {
                try {
                    bufReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputReader != null) {
                try {
                    inputReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content.toString();
    }

    public static String[] getAssetsFileContents(Resources resources, String fileName) {
        StringBuffer content = new StringBuffer();
        List<String> contents = new ArrayList<>();
        InputStreamReader inputReader = null;
        BufferedReader bufReader = null;
        try {
            inputReader = new InputStreamReader(resources.getAssets().open(fileName));
            bufReader = new BufferedReader(inputReader);
            String line;
            while((line = bufReader.readLine()) != null)
                contents.add(line);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufReader != null) {
                try {
                    bufReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputReader != null) {
                try {
                    inputReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return contents.toArray(new String[0]);
    }

    /*
     * file转base64
     */
    public static final String fileToBase64(String path) {
        String result = "";
        FileInputStream inputFile = null;
        try {
            if (TextUtils.isEmpty(path)) return result;
            File file = new File(path);
            if (!file.exists()) return result;

            inputFile = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            inputFile.read(buffer);
            inputFile.close();
            result = Base64.encodeToString(buffer, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputFile != null) {
                try {
                    inputFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
