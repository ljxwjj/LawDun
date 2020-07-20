package com.yunfa365.lawservice.app.ui.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;
import com.yunfa365.lawservice.app.ui.dialog.SelectMapAppDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Administrator on 2016/8/11.
 */
@EActivity(R.layout.activity_common_location)
public class CommonLocationActivity extends BaseUserActivity {
    @ViewById(R.id.base_id_back)
    View mBackView;

    @ViewById(R.id.base_id_title)
    TextView mTitleTxt;

    @ViewById(R.id.base_right_btn)
    ImageView mRightImage;

    @ViewById(R.id.base_right_txt)
    TextView mRightTxt;

    @ViewById
    MapView bmapView;

    @ViewById
    TextView addressTxt;

    @Extra
    String currentAddress;

    @Extra
    String targetCity;

    @Extra
    String targetAddress;

    @Extra
    LatLng targetLatLng;

    protected BaiduMap mBaiduMap;

    @AfterViews
    void init() {
        mBackView.setVisibility(View.VISIBLE);
        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleTxt.setText("位置");

        addressTxt.setText(targetAddress);
        bmapView.showZoomControls(false);
        mBaiduMap = bmapView.getMap();
        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                if (targetLatLng != null && targetLatLng.latitude != 0) {
                    displayMarker(targetLatLng);
                } else {
                    displayAddress(targetCity, targetAddress);
                }
            }
        });
    }

    @Click(R.id.navigationBtn)
    void navigationBtnOnClick(View view) {
        new SelectMapAppDialog(this, targetCity, targetAddress).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bmapView.onDestroy();
        bmapView = null;
        mBaiduMap = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        bmapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bmapView.onPause();
    }

    private void displayAddress(String city, String address) {
        /*AppRequest request = new AppRequest.Build("Users/MapAddress")
                .addParam("City", city)
                .addParam("Adress", address)
                .create();
        new HttpJsonFuture.Builder(this)
                .setData(request)
                .setListener(new AgnettyFutureListener(){
                    @Override
                    public void onStart(AgnettyResult result) {
                        showLoading();
                    }

                    @Override
                    public void onComplete(AgnettyResult result) {
                        hideLoading();
                        AppResponse resp = (AppResponse)result.getAttach();
                        if (resp.Status == AppCst.HTTP_CODE_SUCCESS) {
                            String[] latlngs = resp.Results.split(",");
                            double latitude = Double.parseDouble(latlngs[0]);
                            double longitude = Double.parseDouble(latlngs[1]);
                            LatLng latLng = new LatLng(latitude, longitude);
                            displayMarker(latLng);
                        } else if (!TextUtils.isEmpty(resp.Message)) {
                            AppUtil.showToast(CommonLocationActivity.this, resp.Message);
                        }
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        hideLoading();
                        AppUtil.showToast(CommonLocationActivity.this, "地址定位失败");
                    }
                })
                .execute();*/

        GeoCodeOption option = new GeoCodeOption().address(address).city(city);
        GeoCoder geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                if (geoCodeResult == null) {
                    showToast("地址定位失败");
                    return;
                }
                LatLng latLng = geoCodeResult.getLocation();
                if (latLng == null) {
                    showToast("地址定位失败");
                    return;
                }
                displayMarker(latLng);
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

            }
        });
        geoCoder.geocode(option);
    }

    private void displayMarker(LatLng latLng) {
        BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.mipmap.baidumap_location);
        OverlayOptions ooA = new MarkerOptions().position(latLng).icon(bd).zIndex(9);
        mBaiduMap.addOverlay(ooA);
        MapStatus status = new MapStatus.Builder().zoom(15).target(latLng).build();
        MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(status);
        mBaiduMap.animateMapStatus(u);
    }
}
