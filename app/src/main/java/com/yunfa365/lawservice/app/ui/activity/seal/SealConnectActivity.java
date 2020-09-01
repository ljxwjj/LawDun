package com.yunfa365.lawservice.app.ui.activity.seal;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.model.LatLng;
import com.baihe.bhsdk.util.BleHelper;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.pojo.BhSeal;
import com.yunfa365.lawservice.app.pojo.OfficialRecord;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;
import com.yunfa365.lawservice.app.utils.DateUtil;
import com.yunfa365.lawservice.app.utils.LocationUtil;
import com.yunfa365.lawservice.app.utils.StringUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.util.Calendar;
import java.util.List;

@EActivity(R.layout.activity_seal_connect)
public class SealConnectActivity extends BaseUserActivity {
    private static final int GPS_REQUEST_CODE = 3;

    @ViewById(R.id.base_id_back)
    View mBackView;

    @ViewById(R.id.base_id_title)
    TextView mTitleTxt;

    @ViewById(R.id.base_right_btn)
    ImageView mRightImage;

    @ViewById(R.id.base_right_txt)
    TextView mRightTxt;

    @ViewById
    TextView zhangName;

    @ViewById
    TextView battery;

    @ViewById
    TextView address;

    @ViewById
    TextView officialName;

    @ViewById
    TextView expireTime;

    @ViewById
    TextView sycs;

    @ViewById
    TextView ygcs;

    @Extra
    BhSeal sealItem;

    @Extra
    OfficialRecord officialItem;

    LocationClient mLocationClient = null;
    private String expireTimeStr;

    @AfterViews
    void init() {
        mBackView.setVisibility(View.VISIBLE);
        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleTxt.setText("启动印章");

        Calendar today = Calendar.getInstance();
        today.add(Calendar.DAY_OF_MONTH, 1);
        expireTimeStr = DateUtil.formatDate(today, "yyyy-MM-dd HH:mm:ss");

        zhangName.setText(sealItem.ZTitle);
        expireTime.setText("失效时间：" + expireTimeStr);
        officialName.setText(officialItem.Title);
        sycs.setText(officialItem.ZNums + "");
        ygcs.setText("0");

        BleHelper.getBleHelper(this).shakeHand().subscribe(dataAfterShakehand -> {
            getBattery();
        });

        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(mBDLocationListener);
        requestPermission();
    }

    private void getBattery() {
        BleHelper.getBleHelper(this).getBattery().subscribe(batteryInt -> {
            battery.setText("电量:" + batteryInt + "%");
            startSeal();
        });
    }

    private void startSeal() {
        BleHelper.getBleHelper(this).startSeal(officialItem.ZNums, expireTimeStr).subscribe(result -> {
            if (result != -1) {
//                showToast(String.format("启动印章成功，盖章%d次，启动序号:%d", officialItem.ZNums, result));
                //监听盖章
                BleHelper.getBleHelper(SealConnectActivity.this).setlistenerForStamp().subscribe( r-> {
                    sycs.setText((officialItem.ZNums - r.getStampNumber()) + "");
                    ygcs.setText(r.getStampNumber() + "");
//                    showToast("盖章次数:" + r.getStampNumber());
                });
            } else {
                showToast("启动印章失败");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleHelper.getBleHelper(this).disconnectBle();
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        int span=1000;
        option.setOpenGps(true);
        option.setScanSpan(span);
        option.setIsNeedAddress(true);
        option.setNeedDeviceDirect(true);
        option.SetIgnoreCacheException(false);
        option.setEnableSimulateGps(false);
        option.setIsNeedLocationDescribe(true);
        option.setIsNeedLocationPoiList(true);
        option.setIsNeedAltitude(true);
        option.disableCache(true);
        mLocationClient.setLocOption(option);

    }

    private void requestPermission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        LocationUtil.getInstance(SealConnectActivity.this).startLocation(new LocationUtil.OnGetLocationListener() {
                            @Override
                            public void onGetLocation(String province, String city, String district) {
                                openGPSSettings();
                            }
                        });
                    }
                }, Throwable::printStackTrace);
    }


    private BDLocationListener mBDLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            handlerLocation(bdLocation);
        }
    };

    private void handlerLocation(BDLocation bdLocation) {
        switch (bdLocation.getLocType()) {
            case BDLocation.TypeGpsLocation: // GPS定位结果
            case BDLocation.TypeNetWorkLocation: // 网络定位结果
            case BDLocation.TypeOffLineLocation: // 离线定位结果
                mLocationClient.stop();

                LatLng latlng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
//                loadPOISearch(latlng);

                String locationDes = bdLocation.getLocationDescribe();
                if (StringUtil.isEmpty(locationDes)) {
                    List<Poi> poiList = bdLocation.getPoiList();
                    if (poiList != null && poiList.size() > 0) {
                        locationDes = poiList.get(0).getName();
                    }
                }
                String addrStr = bdLocation.getAddrStr();
                address.setText(addrStr);
                break;
            case BDLocation.TypeServerError: // 服务端网络定位失败
            case BDLocation.TypeNetWorkException: // 网络不同导致定位失败
            case BDLocation.TypeCriteriaException: // 无法获取有效定位依据导致定位失败
                break;
        }
    }

    /**
     * 检测GPS是否打开
     */
    private boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gps || network;
    }

    /**
     * 跳转GPS 设置
     */
    private void openGPSSettings() {
        if (checkGPSIsOpen()) {
            initLocation();
            mLocationClient.start();
        } else {
            //没有则打开则弹出对话框
            new AlertDialog.Builder(this)
                    .setTitle(R.string.notice)
                    .setMessage("当前应用需要打开定位功能。\n\n请点击\"设置\"-\"定位服务\"-打开定位功能。")
                    // 拒绝, 退出应用
                    .setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })

                    .setPositiveButton("设置",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //跳转GPS设置界面
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivityForResult(intent, GPS_REQUEST_CODE);
                                }
                            })

                    .setCancelable(false)
                    .show();
        }
    }

    @OnActivityResult(GPS_REQUEST_CODE)
    void gpsOnResult(int resultCode, Intent data) {
        openGPSSettings();
    }
}
