package com.yunfa365.lawservice.app.ui.activity.seal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.constant.AppConstant;
import com.yunfa365.lawservice.app.pojo.event.GaiZhang;
import com.yunfa365.lawservice.app.pojo.event.GaiZhangPhoto;
import com.yunfa365.lawservice.app.ui.activity.base.BaseActivity;
import com.yunfa365.lawservice.app.ui.view.AutoFitTextureView;
import com.yunfa365.lawservice.app.utils.cameravideo.CameraHelper;
import com.yunfa365.lawservice.app.utils.cameravideo.ICamera2;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.File;


@EActivity(R.layout.activity_camera)
public class CameraActivity extends BaseActivity implements ICamera2.TakePhotoListener,
        SensorEventListener, ICamera2.CameraReady {

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

    @AfterViews
    void init() {
        initView();
        initData();
    }

    protected void initView() {
        // 初始化 切换动画
        MODE = getIntent().getIntExtra("mode", AppConstant.CAMERA_MODE);

        if (MODE == AppConstant.CAMERA_MODE) {
            //摄像头模式
            initCameraMode();
            NOW_MODE = AppConstant.VIDEO_TAKE_PHOTO;
            cameraHelper.setCameraState(ICamera2.CameraMode.TAKE_PHOTO);
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
    @SuppressLint("ClickableViewAccessibility")
    private void initCameraMode() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                        PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            isNoPremissionPause = true;
        }
        initCamera(mNowCameraType);
        cameraHelper = new CameraHelper(this);
        cameraHelper.setTakePhotoListener(this);
        cameraHelper.setCameraReady(this);

        registerSensor();
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
        Uri uri = cameraHelper.getUriFromFile(CameraActivity.this, file);
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
     * 拍照或者录像
     */
    public void recordVideoOrTakePhoto() {
        if (hasRecordClick) {
            return;
        }
        hasRecordClick = true;
        if (ContextCompat.checkSelfPermission
                (this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
                    //视频播放状态
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
                            //视频播放状态
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
//                mVideoPlayer.pause();
            }
        }
    }

    /**
     * 刷新相册
     *
     * @param mediaFile 文件
     */
    private void saveMedia(File mediaFile) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(mediaFile);
        intent.setData(uri);
        sendBroadcast(intent);
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
