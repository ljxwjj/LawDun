package com.yunfa365.lawservice.app.pojo;


import com.google.gson.annotations.JsonAdapter;
import com.yunfa365.lawservice.app.gson.UrlDecoderJsonAdapter;
import com.yunfa365.lawservice.app.utils.FileUtil;

import java.io.Serializable;

public class CaseFile implements Serializable {
    public int status = 0;     // 0：正在上传   1：上传成功  -1： 上传失败

    public String ID;
    public String FileName;
    @JsonAdapter(UrlDecoderJsonAdapter.class)
    public String FilePath;

    public CaseFile() {

    }

    public CaseFile(String path) {
        ID = null;
        FileName = FileUtil.getFileName(path);
        FilePath = path;
    }

    @Override
    public String toString() {
        return FileName;
    }
}
