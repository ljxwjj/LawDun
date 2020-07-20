package com.yunfa365.lawservice.app.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;


import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.utils.AppUtil;
import com.yunfa365.lawservice.app.utils.ScreenUtil;

import java.net.URISyntaxException;

/**
 * Created by Administrator on 2016/8/23.
 */
public class SelectMapAppDialog extends Dialog implements View.OnClickListener {
    private static final String[] MAP_APPS = {"com.baidu.BaiduMap", "com.tencent.map", "com.autonavi.minimap", "com.google.android.apps.maps"};
    private View button;
    private View.OnClickListener mClickListener;
    private Context mContext;
    private String targetCity;
    private String targetAddress;

    public SelectMapAppDialog(Context context, int theme, String city, String address) {
        super(context, theme);
        targetCity = city;
        targetAddress = address;
        initDialog(context);
    }

    public SelectMapAppDialog(Context context, String city, String address) {
        super(context, R.style.MyDialogStyleBottom);
        targetCity = city;
        targetAddress = address;
        initDialog(context);
    }

    private void initDialog(Context context) {
        mContext = context;
        int padding = ScreenUtil.dip2px(10);

        Window window = getWindow();
        WindowManager.LayoutParams attr = window.getAttributes();
        attr.y = padding;
        attr.gravity = Gravity.BOTTOM;
        window.setAttributes(attr);
        window.setGravity(Gravity.BOTTOM);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_select_mapapp, null);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ScreenUtil.screenWidth - (padding * 2),
                ViewGroup.LayoutParams.WRAP_CONTENT);
        addContentView(layout, params);

        button = layout.findViewById(R.id.text);
        button.setOnClickListener(this);

        int count = 0;
        for (String app : MAP_APPS) {
            if (AppUtil.isAvilible(context, app)) {
                TextView view = new TextView(context);
                view.setTag(app);
                view.setGravity(Gravity.CENTER);
                view.setTextColor(ActivityCompat.getColor(context, R.color.common_color_blue));
                view.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(R.dimen.common_size_normal));
                view.setText(AppUtil.getAppNameByPackageName(context, app));
                view.setOnClickListener(this);
                LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, ScreenUtil.dip2px(50));
                layout.addView(view, layout.getChildCount() - 1, param);
                count++;
            }
        }
        if (count == 0) {
            TextView view = new TextView(context);
            view.setGravity(Gravity.CENTER);
            view.setTextColor(ActivityCompat.getColor(context, R.color.common_text_color));
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(R.dimen.common_size_normal));
            view.setText("没有找到可用的地图APP");
            LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, ScreenUtil.dip2px(50));
            layout.addView(view, layout.getChildCount() - 1, param);
        }
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        Object object = v.getTag();
        if (object != null) {
            forwordActivity(object.toString());
        }
        this.dismiss();
    }

    /**
     * 参考 http://blog.csdn.net/qwer4755552/article/details/51659833
     * @param packageName
     */
    private void forwordActivity(String packageName) {
        if ("com.baidu.BaiduMap".equals(packageName)) { // 百度地图
            try {
                Intent intent = Intent.getIntent("intent://map/geocoder?address="+targetAddress+"&src=thirdapp.geo.yourCompanyName.yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                mContext.startActivity(intent); //启动调用
            } catch (URISyntaxException e) {
                Log.e("intent", e.getMessage());
            }
        } else if ("com.tencent.map".equals(packageName)) { // 腾讯地图
            try{
                Intent intent = Intent.getIntent("qqmap://map/search?referer=e律师&keyword="+targetAddress + "&region=" + targetCity);
                mContext.startActivity(intent);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else if ("com.autonavi.minimap".equals(packageName)) { // 高德地图
            try{
                Intent intent = new Intent("android.intent.action.VIEW",
                        Uri.parse("androidamap://viewGeo?sourceApplication=e律师&addr="+targetAddress));
                intent.setPackage("com.autonavi.minimap");
                mContext.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("com.google.android.apps.maps".equals(packageName)) { // google地图
            Uri gmmIntentUri = Uri.parse("geo:0,0?q="+targetAddress);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            mContext.startActivity(mapIntent);
        }
    }
}
