package com.yunfa365.lawservice.app.pojo.event;

import android.net.Uri;

import com.google.gson.annotations.JsonAdapter;
import com.yunfa365.lawservice.app.gson.UrlDecoderJsonAdapter;

public class GaiZhangPhoto {
    @JsonAdapter(UrlDecoderJsonAdapter.class)
    public String filePath;
    public Uri photoUri;

    public GaiZhangPhoto(String filePath, Uri uri) {
        this.filePath = filePath;
        photoUri = uri;
    }
}
