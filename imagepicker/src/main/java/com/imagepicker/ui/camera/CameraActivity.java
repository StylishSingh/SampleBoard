package com.imagepicker.ui.camera;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.imagepicker.R;
import com.imagepicker.model.MessageEvent;
import com.imagepicker.utils.PermissionsAndroid;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * author by Anuj Sharma on 9/27/2017.
 */

public class CameraActivity extends AppCompatActivity implements CameraPresenterView.CameraView {
    private FrameLayout frameLayout;
    private SurfaceView surfaceView;
    private ImageView btnCapture, recentImg, btnSwitchCamera;
    private CameraActivityPresenterImpl presenterImpl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        frameLayout = findViewById(R.id.frame_container);
        surfaceView = findViewById(R.id.surface_camera);
        recentImg = findViewById(R.id.recent_clicked_img);
        btnCapture = findViewById(R.id.btn_capture);
        btnSwitchCamera = findViewById(R.id.btn_switch_camera);
        presenterImpl = new CameraActivityPresenterImpl(this, this);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onPause() {
        if (presenterImpl != null) presenterImpl.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (presenterImpl != null) presenterImpl.onResume();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        /* Do something */

    }


    @Override
    public FrameLayout getFrame() {
        return frameLayout;
    }

    @Override
    public SurfaceView getSurfaceView() {
        return surfaceView;
    }

    @Override
    public ImageView getCaptureBtn() {
        return btnCapture;
    }

    @Override
    public ImageView getRecentImgView() {
        return recentImg;
    }

    @Override
    public ImageView getSwitchCameraBtn() {
        return btnSwitchCamera;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionsAndroid.CAMERA_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Show option to update profile
                    if (presenterImpl != null) {
                        presenterImpl.init();
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Camera/Storage permission Denied", Toast.LENGTH_SHORT).show();
//                    Utils.getInstance().showToast("Camera/Storage permission Denied");
                }
            }

        }
    }
}
