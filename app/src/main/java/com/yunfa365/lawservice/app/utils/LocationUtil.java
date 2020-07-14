package com.yunfa365.lawservice.app.utils;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * 定位当前省市
 * Created by Administrator on 2016/8/25.
 */
public class LocationUtil {
    private static LocationUtil instance;
    private LocationClient mLocationClient = null;
    private BDLocationListener mBDLocationListener;
    private OnGetLocationListener mListener;

    private LocationUtil(Context context) {
        mBDLocationListener = new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                mLocationClient.stop();
                handlerLocation(bdLocation);
            }
        };

        mLocationClient = new LocationClient(context.getApplicationContext());
        mLocationClient.registerLocationListener(mBDLocationListener);

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        int span=1000;
        option.setScanSpan(span);
        option.setIsNeedAddress(true);
        option.SetIgnoreCacheException(false);
        option.setEnableSimulateGps(true);
        option.setIsNeedLocationDescribe(false);
        option.setIsNeedLocationPoiList(false);
        mLocationClient.setLocOption(option);
    }

    public static LocationUtil getInstance(Context context) {
        if (instance == null) {
            instance = new LocationUtil(context);
        }
        return instance;
    }

    public void startLocation(OnGetLocationListener listener) {
        mListener = listener;
        mLocationClient.start();
    }

    private void handlerLocation(BDLocation bdLocation) {
        switch (bdLocation.getLocType()) {
            case BDLocation.TypeGpsLocation: // GPS定位结果
            case BDLocation.TypeNetWorkLocation: // 网络定位结果
            case BDLocation.TypeOffLineLocation: // 离线定位结果
                String province = bdLocation.getProvince();
                String city = bdLocation.getCity();
                String district = bdLocation.getDistrict();
                province = province.replace("省", "");
                city = city.replace("市", "");
                if (district.contains("市")) {
                    district = bdLocation.getDistrict();
                } else if (district.contains("县")) {
                    district = bdLocation.getDistrict();
                } else if (district.contains("区")) {
                    district = bdLocation.getCity();
                } else {
                    district = bdLocation.getCity();
                }
                if (mListener != null)
                    mListener.onGetLocation(province, city, district);
                mListener = null;
                break;
            case BDLocation.TypeServerError: // 服务端网络定位失败
            case BDLocation.TypeNetWorkException: // 网络不同导致定位失败
            case BDLocation.TypeCriteriaException: // 无法获取有效定位依据导致定位失败
                break;
        }
    }

    public interface OnGetLocationListener {
        void onGetLocation(String province, String city, String district);
    }
}
