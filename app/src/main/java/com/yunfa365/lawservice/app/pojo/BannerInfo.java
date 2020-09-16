package com.yunfa365.lawservice.app.pojo;

import com.google.gson.annotations.JsonAdapter;
import com.yunfa365.lawservice.app.gson.UrlDecoderJsonAdapter;

/**
 * Created by Administrator on 2016/4/15.
 */
public class BannerInfo {

    public BannerInfo(String url) {
        FilePath = url;
    }

    public BannerInfo(int image) {
        localImage = image;
    }
    public int localImage;

    // 启动图使用
    @JsonAdapter(UrlDecoderJsonAdapter.class)
    public String FilePath;
    public String Links;
    public String Btime;
    public String Etime;
    public int Time;
}
