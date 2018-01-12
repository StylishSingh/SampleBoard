package com.imagepicker.ui.camera;

import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * author by Anuj Sharma on 10/3/2017.
 */

public interface CameraPresenterView {
    interface CameraPresenter {
        void onCaptureImage();
    }

    interface CameraView {
        FrameLayout getFrame();
        SurfaceView getSurfaceView();
        ImageView getCaptureBtn();
        ImageView getRecentImgView();
        ImageView getSwitchCameraBtn();
    }
}
