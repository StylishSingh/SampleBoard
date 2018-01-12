package com.imagepicker.ui.cropper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.imagepicker.R;
import com.imagepicker.cropper.CropImage;
import com.imagepicker.cropper.CropImageOptions;
import com.imagepicker.cropper.CropImageView;
import com.imagepicker.model.MediaItemBean;
import com.imagepicker.ui.GlobalApplication;
import com.imagepicker.utils.Constants;
import com.imagepicker.utils.PermissionsAndroid;

import java.io.File;
import java.io.IOException;

import io.fabric.sdk.android.Fabric;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;

/**
 * author by Anuj Sharma on 9/25/2017.
 */

public class CropperActivity extends AppCompatActivity implements CropImageView.OnSetImageUriCompleteListener, CropImageView.OnCropImageCompleteListener, View.OnClickListener {

    private CropImageView cropImageView;
    private CropImageOptions cropImageOptions;
    //    private TextView btnReset;
//    private ImageView btnRotation, btnLayers;
    private MediaItemBean mediaObj;
    private BottomSheetBehavior mBottomSheetBehavior;
    private View transparentView;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        menu.findItem(R.id.action_count).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (PermissionsAndroid.getInstance().checkWriteExternalStoragePermission(this)) {
                    cropImage();
                } else {
                    PermissionsAndroid.getInstance().requestForWriteExternalStoragePermission(this);
                }
                break;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
//        cropImageView.setOnSetImageUriCompleteListener(this);
        cropImageView.setOnCropImageCompleteListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        cropImageView.setOnSetImageUriCompleteListener(null);
        cropImageView.setOnCropImageCompleteListener(null);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((GlobalApplication) getApplication()).getAppComponent().inject(this);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_cropper);
        initViews();
        if (getIntent() != null && getIntent().getParcelableExtra(Constants.SELECTED_MEDIA_LIST_OBJ) != null) {
            mediaObj = getIntent().getParcelableExtra(Constants.SELECTED_MEDIA_LIST_OBJ);
            System.out.println("MediaSize-> " + mediaObj.getMediaSize());
            System.out.println("MediaHeight-> " + mediaObj.getHeight());
            System.out.println("MediaWidth-> " + mediaObj.getWidth());
            if (!TextUtils.isEmpty(mediaObj.getCroppedPath())) {
                cropImageView.setImageUriAsync(Uri.fromFile(new File(mediaObj.getCroppedPath())));
            } else {
                cropImageView.setImageUriAsync(Uri.fromFile(new File(mediaObj.getMediaPath())));
            }
        }
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_crop);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cropImageView = findViewById(R.id.cropImageView);
        cropImageOptions = new CropImageOptions();
        transparentView = findViewById(R.id.bg);
        ImageView btnRotation = findViewById(R.id.btn_rotation);
        ImageView btnLayers = findViewById(R.id.btn_layer);
        TextView btnReset = findViewById(R.id.btn_reset);

        TextView btnOriginal = findViewById(R.id.btn_original);
        TextView btnSquare = findViewById(R.id.btn_square);
        TextView btn2_3 = findViewById(R.id.btn_2_3);
        TextView btn3_5 = findViewById(R.id.btn_3_5);
        TextView btnCancel = findViewById(R.id.btn_cancel);

        btnOriginal.setOnClickListener(this);
        btnSquare.setOnClickListener(this);
        btn2_3.setOnClickListener(this);
        btn3_5.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        View bottomSheet = findViewById(R.id.container_bottomsheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mBottomSheetBehavior.setPeekHeight(0);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN)
                    transparentView.setVisibility(View.GONE);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                transparentView.setVisibility(View.VISIBLE);
                transparentView.setAlpha(slideOffset);
            }
        });
        transparentView.setOnClickListener(this);
        btnRotation.setOnClickListener(this);
        btnLayers.setOnClickListener(this);
        btnReset.setOnClickListener(this);
    }

    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
        if (error == null) {
            if (cropImageOptions.initialCropWindowRectangle != null) {
                cropImageView.setCropRect(cropImageOptions.initialCropWindowRectangle);
            }
            if (cropImageOptions.initialRotation > -1) {
                cropImageView.setRotatedDegrees(cropImageOptions.initialRotation);
            }

        } else {
//            setResult(null, error, 1);
            Toast.makeText(this, "Image load failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
        setResult(result.getUri(), result.getError(), result.getSampleSize());
    }

    /**
     * Result with cropped image data or error if failed.
     */
    protected void setResult(Uri uri, Exception error, int sampleSize) {
        int resultCode = error == null ? RESULT_OK : CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE;
//        setResult(resultCode, getResultIntent(uri, error, sampleSize));
//        finish();

        Intent dataIntent = new Intent();
        dataIntent.setData(uri);
        setResult(resultCode, dataIntent);
        finish();
    }

    /**
     * Execute crop image and save the result tou output uri.
     */
    protected void cropImage() {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                Uri outputUri = getOutputUri();
                cropImageView.saveCroppedImageAsync(outputUri,
                        cropImageOptions.outputCompressFormat,
                        cropImageOptions.outputCompressQuality,
                        cropImageOptions.outputRequestWidth,
                        cropImageOptions.outputRequestHeight,
                        cropImageOptions.outputRequestSizeOptions);
            }
        }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    protected void rotateImage(int degrees) {
        cropImageView.rotateImage(degrees);
    }

    protected Uri getOutputUri() {
        Uri outputUri = cropImageOptions.outputUri;
        if (outputUri == null || outputUri.equals(Uri.EMPTY)) {
            try {
                String ext = cropImageOptions.outputCompressFormat == Bitmap.CompressFormat.JPEG ? ".jpg" :
                        cropImageOptions.outputCompressFormat == Bitmap.CompressFormat.PNG ? ".png" : ".webp";
                outputUri = Uri.fromFile(File.createTempFile("cropped", ext, getCacheDir()));
            } catch (IOException e) {
                throw new RuntimeException("Failed to create temp file for output image", e);
            }
        }
        return outputUri;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bg:
                //Hide bottom sheet
                hideBottomSheet();
                break;
            case R.id.btn_cancel:
                //Hide bottom sheet
                hideBottomSheet();
                break;
            case R.id.btn_original:
                cropImageView.clearAspectRatio();
                hideBottomSheet();
                break;
            case R.id.btn_square:
                cropImageView.setAspectRatio(1, 1);
                hideBottomSheet();
                break;
            case R.id.btn_2_3:
                cropImageView.setAspectRatio(2, 3);
                hideBottomSheet();
                break;
            case R.id.btn_3_5:
                cropImageView.setAspectRatio(3, 5);
                hideBottomSheet();
                break;
            case R.id.btn_rotation:
                rotateImage(90);
                break;
            case R.id.btn_layer:
                // show dialog or bottomsheet and choose crop ratio
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case R.id.btn_reset:
                //reset whole
                if (mediaObj != null && cropImageView != null) {
                    if (!TextUtils.isEmpty(mediaObj.getCroppedPath()))
                        cropImageView.setImageUriAsync(Uri.fromFile(new File(mediaObj.getMediaPath())));
                }
                break;
        }
    }

    private void hideBottomSheet() {
        if (mBottomSheetBehavior != null) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

}
