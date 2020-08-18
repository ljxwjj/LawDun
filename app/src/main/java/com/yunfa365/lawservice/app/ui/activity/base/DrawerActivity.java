package com.yunfa365.lawservice.app.ui.activity.base;

import androidx.drawerlayout.widget.DrawerLayout;

public abstract class DrawerActivity extends BaseUserActivity {
    public abstract DrawerLayout getDrawerLayout();
    public abstract void reLoadData(String... data);
}
