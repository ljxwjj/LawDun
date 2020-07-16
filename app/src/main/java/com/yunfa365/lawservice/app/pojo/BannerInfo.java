package com.yunfa365.lawservice.app.pojo;

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
    public String FilePath;
    public String Links;
    public String Btime;
    public String Etime;
    public int Time;
}
