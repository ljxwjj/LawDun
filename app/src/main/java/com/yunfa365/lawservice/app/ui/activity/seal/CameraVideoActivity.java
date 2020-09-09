package com.yunfa365.lawservice.app.ui.activity.seal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.params.RggbChannelVector;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.constant.AppConstant;
import com.yunfa365.lawservice.app.pojo.event.GaiZhang;
import com.yunfa365.lawservice.app.pojo.event.GaiZhangPhoto;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;
import com.yunfa365.lawservice.app.ui.view.AutoFitTextureView;
import com.yunfa365.lawservice.app.ui.view.AutoLocateHorizontalView;
import com.yunfa365.lawservice.app.utils.cameravideo.CameraHelper;
import com.yunfa365.lawservice.app.utils.cameravideo.ICamera2;
import com.yunfa365.lawservice.app.utils.cameravideo.IVideoControl;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.File;

@EActivity(R.layout.activity_camera_video)
public class CameraVideoActivity extends BaseUserActivity implements IVideoControl.PlaySeekTimeListener,
        IVideoControl.PlayStateListener, ICamera2.TakePhotoListener,
        SensorEventListener, ICamera2.CameraReady, AutoLocateHorizontalView.OnSelectedPositionChangedListener {

    private static final String TAG = "CameraVideoActivity";

    /**
     * 当前的显示面板状态
     */
    public int TEXTURE_STATE = AppConstant.TEXTURE_PREVIEW_STATE;


    @ViewById(R.id.video_texture)
    AutoFitTextureView videoTexture;

    @ViewById(R.id.video_photo_list)
    RecyclerView videoPhotoList;

    /**
     * 相机模式
     */
    private int MODE;
    /**
     * 拍照工具类
     */
    private CameraHelper cameraHelper;
    /**
     * 当前拍照模式
     */
    private int NOW_MODE;
    /**
     * 前 后 摄像头标识
     */
    private ICamera2.CameraType mNowCameraType = ICamera2.CameraType.BACK;
    /**
     * 单点标识
     */
    private boolean hasRecordClick = false;

    /**
     * 图片路径
     */
    private String mCameraPath;
    /**
     * 照片集合
     */
    private PhotoListAdapter pAdapter;
    /**
     * 是否有拍照权限
     */
    private boolean isNoPremissionPause;

    /**
     * visible与invisible之间切换的动画
     */
    private TranslateAnimation mShowAction;

    /**
     * 设置 rgb 色域
     * @param whiteBalance 0- 100
     * @return RggbChannelVector
     */
    public static RggbChannelVector colorTemperature(int whiteBalance) {
        float temperature = whiteBalance/100;
        float red;
        float green;
        float blue;

        //Calculate red
        if (temperature <= 66)
            red = 255;
        else {
            red = temperature - 60;
            red = (float) (329.698727446 * (Math.pow((double) red, -0.1332047592)));
            if (red < 0)
                red = 0;
            if (red > 255)
                red = 255;
        }


        //Calculate green
        if (temperature <= 66) {
            green = temperature;
            green = (float) (99.4708025861 * Math.log(green) - 161.1195681661);
            if (green < 0)
                green = 0;
            if (green > 255)
                green = 255;
        } else {
            green = temperature - 60;
            green = (float) (288.1221695283 * (Math.pow((double) green, -0.0755148492)));
            if (green < 0)
                green = 0;
            if (green > 255)
                green = 255;
        }

        //calculate blue
        if (temperature >= 66)
            blue = 255;
        else if (temperature <= 19)
            blue = 0;
        else {
            blue = temperature - 10;
            blue = (float) (138.5177312231 * Math.log(blue) - 305.0447927307);
            if (blue < 0)
                blue = 0;
            if (blue > 255)
                blue = 255;
        }

        Log.e(TAG, "red=" + red + ", green=" + green + ", blue=" + blue + ", paroess:"+whiteBalance);
        return new RggbChannelVector((red/255) * 2, (green/255), (green/255), (blue/255) * 2);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //注册 eventbus
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @AfterViews
    void init() {
        initView();
        initData();
    }

    @Click(R.id.submitBtn)
    void submitBtnOnClick() {
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void initView() {
        // 初始化 切换动画
        mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(100);

        MODE = getIntent().getIntExtra("mode", AppConstant.CAMERA_MODE);

        if (MODE == AppConstant.CAMERA_MODE) {
            //摄像头模式
            initCameraMode();
        }

        LinearLayoutManager ms = new LinearLayoutManager(this);
        ms.setOrientation(LinearLayoutManager.HORIZONTAL);
        videoPhotoList.setLayoutManager(ms);
        pAdapter = new PhotoListAdapter();
        videoPhotoList.setAdapter(pAdapter);

        // rv 点击事件
        initListener();
    }

    protected void initData() {
        mCameraPath = cameraHelper.getPhotoFilePath();
    }

    /**
     * 初始化 拍照
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ClickableViewAccessibility")
    private void initCameraMode() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                        PackageManager.PERMISSION_GRANTED) {
            isNoPremissionPause = true;
        }
        initCamera(mNowCameraType);
        cameraHelper = new CameraHelper(this);
        cameraHelper.setTakePhotoListener(this);
        cameraHelper.setCameraReady(this);

        registerSensor();
        initScaleSeekbar();
    }

    /**
     * 初始化摄像头
     *
     * @param cameraType
     */
    private void initCamera(ICamera2.CameraType cameraType) {
        if (cameraHelper == null) {
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        cameraHelper.setTextureView(videoTexture);
        cameraHelper.openCamera(cameraType);
    }

    /**
     * 初始化 scale seekBar
     */
    private void initScaleSeekbar() {

    }


    /**
     * 传感器继承方法 重力发生改变
     * 根据重力方向 动态旋转拍照图片角度(暂时关闭该方法)
     *
     * 使用以下方法
     * int rotation = getWindowManager().getDefaultDisplay().getRotation();
     * @param event event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
//            Log.e(TAG, "onSensorChanged: x: " + x +"   y: "+y +"  z : "+z);
            if (z > 55.0f) {
                //向左横屏
            } else if (z < -55.0f) {
                //向右横屏
            } else if (y > 60.0f) {
                //是倒竖屏
            } else {
                //正竖屏
            }
        }
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float light = event.values[0];
            cameraHelper.setLight(light);
        }
    }

    /**
     * 注册陀螺仪传感器
     */
    private void registerSensor() {
        SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        Sensor mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (mSensor == null) {
            return;
        }
        mSensorManager.registerListener(this, mSensor, Sensor.TYPE_ORIENTATION);
        mSensorManager.registerListener(this, mLightSensor, Sensor.TYPE_LIGHT);
    }

    /**
     * 当已注册传感器的精度发生变化时调用
     *
     * @param sensor   sensor
     * @param accuracy 传感器的新精度
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSeekTime(int allTime, final int time) {

    }

    @Override
    public void onStartListener(int width, int height) {
        videoTexture.setVideoAspectRatio(width, height);
    }

    @Override
    public void onCompletionListener() {

    }

    /**
     * 拍照完成回调
     *
     * @param file          文件
     * @param photoRotation 角度
     * @param width         宽度
     * @param height        高度
     */
    @Override
    public void onTakePhotoFinish(final File file, int photoRotation, int width, int height) {
        mCameraPath = cameraHelper.getPhotoFilePath();
        Uri uri = cameraHelper.getUriFromFile(this, file);
        GaiZhangPhoto item = new GaiZhangPhoto(file.getAbsolutePath(), uri);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pAdapter.addData(item);
                EventBus.getDefault().post(item);
            }
        });
    }

    /**
     * 相机准备完毕
     */
    @Override
    public void onCameraReady() {

    }

    /**
     * 横向菜单列表 修改点击事件
     *
     * @param pos
     */
    @Override
    public void selectedPositionChanged(int pos) {
        Log.e(TAG, "selectedPositionChanged: "+pos);
        switch (pos){
            case 0:{
                showLayout(0, false);
                NOW_MODE = AppConstant.VIDEO_TAKE_PHOTO;
                cameraHelper.setCameraState(ICamera2.CameraMode.TAKE_PHOTO);
                break;
            }
            case 1:{
                showLayout(0, false);
                NOW_MODE = AppConstant.VIDEO_RECORD_MODE;
                cameraHelper.setCameraState(ICamera2.CameraMode.RECORD_VIDEO);
                break;
            }
        }
    }

    /**
     * 拍照或者录像
     */
    public void recordVideoOrTakePhoto() {
        if (hasRecordClick) {
            return;
        }
        hasRecordClick = true;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "cameraOnClickListener: 动态权限获取失败...");
            return;
        }
        //拍照
        if (NOW_MODE == AppConstant.VIDEO_TAKE_PHOTO && mCameraPath!=null) {
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            cameraHelper.setDeviceRotation(rotation);
            cameraHelper.takePhone(mCameraPath, ICamera2.MediaType.JPEG);
        }
        hasRecordClick = false;
    }

    /**
     * 关闭摄像头
     */
    private void closeCamera() {
        cameraHelper.closeCamera();
        cameraHelper.stopBackgroundThread();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraHelper != null) {
            cameraHelper.startBackgroundThread();
        }

        if (videoTexture.isAvailable()) {
            if (MODE == AppConstant.CAMERA_MODE) {
                if (TEXTURE_STATE == AppConstant.TEXTURE_PREVIEW_STATE) {
                    //预览状态
                    initCamera(mNowCameraType);
                } else if (TEXTURE_STATE == AppConstant.TEXTURE_PLAY_STATE) {

                }
            }
        } else {
            videoTexture.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                    if (MODE == AppConstant.CAMERA_MODE) {
                        if (TEXTURE_STATE == AppConstant.TEXTURE_PREVIEW_STATE) {
                            //预览状态
                            initCamera(mNowCameraType);
                        } else if (TEXTURE_STATE == AppConstant.TEXTURE_PLAY_STATE) {

                        }
                    }
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    return true;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surface) {

                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isNoPremissionPause) {
            isNoPremissionPause = false;
            return;
        }
        Log.e("camera", "mode:" + MODE);
        if (MODE == AppConstant.CAMERA_MODE) {
            if (TEXTURE_STATE == AppConstant.TEXTURE_PREVIEW_STATE) {
                cameraHelper.closeCamera();
                cameraHelper.stopBackgroundThread();
            } else if (TEXTURE_STATE == AppConstant.TEXTURE_PLAY_STATE) {

            }
        }
    }


    /**
     * 显示和隐藏控件
     *
     * @param showWhat
     * @param showOrNot
     */
    private void showLayout(int showWhat, boolean showOrNot) {

    }

    /**
     * rv点击事件 初始化
     */
    private void initListener() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GaiZhang item) {
        recordVideoOrTakePhoto();
    }

    class PhotoListAdapter extends BaseQuickAdapter<GaiZhangPhoto, BaseViewHolder> {

        public PhotoListAdapter() {
            super(R.layout.photo_list_item);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder holder, GaiZhangPhoto gaiZhangPhoto) {
            ImageView videoPhoto = holder.getView(R.id.video_photo);
            videoPhoto.setImageURI(gaiZhangPhoto.photoUri);
        }
    }
}
