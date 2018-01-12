package com.imagepicker.ui.selectedMedia;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.imagepicker.R;
import com.imagepicker.model.MediaItemBean;
import com.imagepicker.model.MessageEvent;
import com.imagepicker.ui.GlobalApplication;
import com.imagepicker.ui.cropper.CropperActivity;
import com.imagepicker.utils.Constants;
import com.imagepicker.utils.DetailViewPagerTransformer;
import com.imagepicker.utils.SimpleGestureFilter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.fabric.sdk.android.Fabric;

/**
 * auther Anuj Sharma on 9/21/2017.
 */

public class SelectedMediaActivity extends AppCompatActivity implements SelectedMediaView,SimpleGestureFilter.SimpleGestureListener {
    private SelectedMediaPresenterImpl presenterImpl;
    private Toolbar toolbar;
    private ViewPager selectedViewPager;
    private RecyclerView selectedMediaRecycler;
    public static final int CROP_IMAGE_REQUEST_CODE = 501;
    public MenuItem cropMenu;

    private SimpleGestureFilter detector;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_selected_image, menu);
        cropMenu = menu.findItem(R.id.action_crop);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                if (presenterImpl != null) {
                    presenterImpl.deleteMedia();
                }
                break;
            case R.id.action_crop:
                //Move to Seperate Activity for Cropping
                if (SelectedMediaActivity.this.presenterImpl.getSelectedMediaObj() != null) {
                    Intent intent = new Intent(this, CropperActivity.class);
                    intent.putExtra(Constants.SELECTED_MEDIA_LIST_OBJ, SelectedMediaActivity.this.presenterImpl.getSelectedMediaObj());
                    startActivityForResult(intent, CROP_IMAGE_REQUEST_CODE);
                }
                break;
            case R.id.action_save:
                if (SelectedMediaActivity.this.presenterImpl!=null){
                    SelectedMediaActivity.this.presenterImpl.onSaveClick();
                }
                break;
        }
        return false;
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((GlobalApplication)getApplication()).getAppComponent().inject(this);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_selected_media);
        SparseArray<MediaItemBean> selectedMediaMap = null;
        if (getIntent() != null && getIntent().getParcelableExtra(Constants.SELECTED_MEDIA_LIST_OBJ) != null) {
            MediaItemBean obj = getIntent().getParcelableExtra(Constants.SELECTED_MEDIA_LIST_OBJ);
            selectedMediaMap = obj.getSeelctedItemMap();
        }
        initViews();
        // Detect touched area
        detector = new SimpleGestureFilter(this,this);

        presenterImpl = new SelectedMediaPresenterImpl(this, this, selectedMediaMap);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        /* Do something */
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        selectedViewPager = findViewById(R.id.selected_viewpager);
        selectedViewPager.setPageTransformer(false, new DetailViewPagerTransformer(DetailViewPagerTransformer.TransformType.DEPTH));
        selectedMediaRecycler = findViewById(R.id.recycler_selected_media);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (presenterImpl != null)
            presenterImpl.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public ViewPager getSelectedViewPager() {
        return selectedViewPager;
    }

    @Override
    public RecyclerView getSelectedMediaRecycler() {
        return selectedMediaRecycler;
    }



    @Override
    public void onSwipe(int direction) {
        String str = "";

        switch (direction) {

            case SimpleGestureFilter.SWIPE_RIGHT : str = "Swipe Right";
                break;
            case SimpleGestureFilter.SWIPE_LEFT :  str = "Swipe Left";
                break;
            case SimpleGestureFilter.SWIPE_DOWN :  str = "Swipe Down";
                break;
            case SimpleGestureFilter.SWIPE_UP :    str = "Swipe Up";
                break;

        }
//        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDoubleTap() {
        Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.enterPictureInPictureMode();
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        if (isInPictureInPictureMode) {
            // Hide the full-screen UI (controls, etc.) while in picture-in-picture mode.
            toolbar.setVisibility(View.GONE);
        } else {
            // Restore the full-screen UI.
            toolbar.setVisibility(View.VISIBLE);
        }
    }
}
