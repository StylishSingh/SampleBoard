package com.imagepicker.ui.camera;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.imagepicker.R;
import com.imagepicker.model.MessageEvent;
import com.imagepicker.utils.PermissionsAndroid;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.view.OrientationEventListener.ORIENTATION_UNKNOWN;

/**
 * author by Anuj Sharma on 10/3/2017.
 */

public class CameraActivityPresenterImpl implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "custom camera";
    private CameraPresenterView.CameraView cameraView;
    private CameraActivity cameraActivity;
    private Camera camera;
    //    private Camera mCamera;
//    private CameraPreview mPreview;
    private Preview mPreview;
    private boolean isFrontCameraOpened;

    public CameraActivityPresenterImpl(CameraActivity cameraActivity, CameraPresenterView.CameraView cameraView) {
        this.cameraActivity = cameraActivity;
        this.cameraView = cameraView;
        init();
    }

    protected void init() {
        if (PermissionsAndroid.getInstance().checkCameraPermission(cameraActivity)) {
            cameraView.getCaptureBtn().setOnClickListener(this);
            cameraView.getSwitchCameraBtn().setOnClickListener(this);
            cameraView.getRecentImgView().setOnClickListener(this);
            // Create an instance of Camera
//            mCamera = getCameraInstance();
            // Create our Preview view and set it as the content of our activity.
            mPreview = new Preview(cameraActivity, cameraView.getSurfaceView());
            mPreview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            cameraView.getFrame().addView(mPreview);
            mPreview.setKeepScreenOn(true);

            cameraActivity.getLoaderManager().initLoader(101, null, this);
        } else {
            PermissionsAndroid.getInstance().requestForCameraPermission(cameraActivity);
        }
    }

    public void onResume() {
        int numCams = Camera.getNumberOfCameras();
        if (numCams > 0) {
            try {
                if (isFrontCameraOpened) {
                    camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                    isFrontCameraOpened = true;
                } else {
                    camera = Camera.open();
                    isFrontCameraOpened = false;
                }

                camera.startPreview();
                mPreview.setCamera(camera);
            } catch (RuntimeException ex) {
                Toast.makeText(cameraActivity, "Camera Not found", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onPause() {
        if (camera != null) {
            camera.stopPreview();
            mPreview.setCamera(null);
            camera.release();
            camera = null;
        }
    }

    private void resetCam() {
        camera.startPreview();
        mPreview.setCamera(camera);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_capture:
                disableEnableBtn(true);
                onOrientationChanged(270);
                camera.getParameters().setJpegQuality(90);
                camera.getParameters().setJpegThumbnailQuality(90);
                camera.takePicture(shutterCallback, rawCallback, jpegCallback);
                break;
            case R.id.recent_clicked_img:
                //Move to previious screen with current camera gallery
                MessageEvent obj = new MessageEvent();
                obj.setCameraEventClicked(true);
                EventBus.getDefault().postSticky(obj);
                cameraActivity.finish();
                break;
            case R.id.btn_switch_camera:
                /*camera.stopPreview();
                mPreview.setCamera(null);
                if (isFrontCameraOpened) {
                    camera = Camera.open();
                    isFrontCameraOpened = false;
                } else {
                    System.out.println("front camera-> " + Camera.CameraInfo.CAMERA_FACING_FRONT);

                    camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                    isFrontCameraOpened = true;
                }
                mPreview.setCamera(camera);
                camera.startPreview();*/
                break;
        }
    }

    public void onOrientationChanged(int orientation) {
        if (orientation == ORIENTATION_UNKNOWN) return;
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(0, info);
        orientation = (orientation + 45) / 90 * 90;
        int rotation = 0;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            rotation = (info.orientation - orientation + 360) % 360;
        } else {  // back-facing camera
            rotation = (info.orientation + orientation) % 360;
        }
        camera.getParameters().setRotation(rotation);
    }

    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            //			 Log.d(TAG, "onShutter'd");
        }
    };

    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            //			 Log.d(TAG, "onPictureTaken - raw");
        }
    };

    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {

            Single.just(data).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<byte[]>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(byte[] bytes) {
                            FileOutputStream outStream = null;
                            // Write to SD Card
                            try {
                                File dir = new File(Environment.getExternalStorageDirectory() + File.separator +
                                        cameraActivity.getString(R.string.app_name));
                                if (!dir.isDirectory()) dir.mkdir();

                                String fileName = String.format("%d.jpg", System.currentTimeMillis());
                                File outFile = new File(dir, fileName);
                                outStream = new FileOutputStream(outFile);
                                outStream.write(bytes);
                                outStream.flush();
                                outStream.close();
                                Log.d(TAG, "onPictureTaken - wrote bytes: " + bytes.length + " to " + outFile.getAbsolutePath());
                                refreshGallery(outFile);
                                if (cameraActivity != null) {
                                    disableEnableBtn(false);
                                    if (outFile != null) {
                                        Uri uri = Uri.fromFile(outFile);
                                        cameraView.getRecentImgView().setImageURI(uri);
                                    }
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            resetCam();
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });


//            new SaveImageTask().execute(data);
//            resetCam();
            Log.d(TAG, "onPictureTaken - jpeg");
        }
    };


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri queryUri = MediaStore.Files.getContentUri("external");
        String folderSelection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE +
                ") GROUP BY (" + MediaStore.Files.FileColumns.PARENT + "AND" + MediaStore.Images.Media.DATA + " like ? ";
        String sortOrder = MediaStore.Files.FileColumns.PARENT + " DESC";
        String[] folderProjection = new String[]{
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.PARENT,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.TITLE
        };
        File dir = new File(Environment.getExternalStorageDirectory() + File.separator +
                cameraActivity.getString(R.string.app_name));

        if (!dir.isDirectory()) {
            return null;
        }

        return new CursorLoader(
                cameraActivity,
                queryUri,
                folderProjection,
                MediaStore.Images.Media.DATA + " like ? ",
                new String[]{"%" + dir.getAbsolutePath() + "%"}, // Selection args (none).
                sortOrder // Sort order.
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            int _id = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int mediaData = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            int mediaName = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            String id = cursor.getString(_id);
            String coverImgTitle = cursor.getString(mediaName);
            String coverPicPath = cursor.getString(mediaData);


            System.out.println("ID column-> " + id);
            System.out.println("cover Image Title -> " + coverImgTitle);
            System.out.println("cover Image path-> " + coverPicPath);
            Uri uri = Uri.fromFile(new File(coverPicPath));
            cameraView.getRecentImgView().setImageURI(uri);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void disableEnableBtn(boolean isDisableBtn) {
        if (isDisableBtn) {
            cameraView.getCaptureBtn().setEnabled(false);
            cameraView.getCaptureBtn().setAlpha(0.3f);
            cameraView.getSwitchCameraBtn().setEnabled(false);
            cameraView.getSwitchCameraBtn().setAlpha(0.3f);
        } else {
            cameraView.getCaptureBtn().setEnabled(true);
            cameraView.getCaptureBtn().setAlpha(1f);
            cameraView.getSwitchCameraBtn().setEnabled(true);
            cameraView.getSwitchCameraBtn().setAlpha(1f);
        }
    }

    /**
     * Save Image on Disk
     */
    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {
        File outFile;

        @Override
        protected Void doInBackground(byte[]... data) {
            FileOutputStream outStream = null;
            // Write to SD Card
            try {
                File dir = new File(Environment.getExternalStorageDirectory() + File.separator +
                        cameraActivity.getString(R.string.app_name));
                if (!dir.isDirectory()) dir.mkdir();

                String fileName = String.format("%d.jpg", System.currentTimeMillis());
                outFile = new File(dir, fileName);
                outStream = new FileOutputStream(outFile);
                outStream.write(data[0]);
                outStream.flush();
                outStream.close();
                Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length + " to " + outFile.getAbsolutePath());
                refreshGallery(outFile);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //show recent clicked image on view left side
            if (cameraActivity != null) {
                disableEnableBtn(false);
                if (outFile != null) {
                    Uri uri = Uri.fromFile(outFile);
                    cameraView.getRecentImgView().setImageURI(uri);
                }
            }
        }
    }

    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        cameraActivity.sendBroadcast(mediaScanIntent);
    }
}
