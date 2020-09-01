package com.yunfa365.lawservice.app.pojo;

public class PageInfo {
    public int page = 0;
    public void nextPage() {
        page++;
    }

    public void reset() {
        page = 0;
    }

    public boolean isFirstPage() {
        return page == 0;
    }
}
