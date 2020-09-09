package com.yunfa365.lawservice.app.pojo.event;

import android.net.Uri;

public class GaiZhangPhoto {
    public String filePath;
    public Uri photoUri;

    public GaiZhangPhoto(String filePath, Uri uri) {
        this.filePath = filePath;
        photoUri = uri;
    }
}
