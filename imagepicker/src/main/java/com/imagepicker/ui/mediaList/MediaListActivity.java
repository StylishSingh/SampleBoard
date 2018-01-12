package com.imagepicker.ui.mediaList;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.imagepicker.R;
import com.imagepicker.model.MediaItemBean;
import com.imagepicker.model.MessageEvent;
import com.imagepicker.ui.GlobalApplication;
import com.imagepicker.ui.PickerActivity;
import com.imagepicker.utils.Constants;
import com.imagepicker.utils.PermissionsAndroid;
import com.imagepicker.utils.RecyclerViewFastScroller;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

/**
 * auther Anuj Sharma on 9/18/2017.
 */

public class MediaListActivity extends AppCompatActivity implements MediaListView {
    private Toolbar toolbar;
    private AppCompatSpinner spinnerFolder;
    private ImageView emptyView;
    private RecyclerView recyclerView;
    private FloatingActionButton fabCamera;
    private RecyclerViewFastScroller fastScroller;
    public MenuItem save, count;

    //    @Inject
    MediaListPresenterImpl presenterImpl;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_save, menu);
        return true;

    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        System.out.println("on prepareoption panel");
        save = menu.findItem(R.id.action_save);
        count = menu.findItem(R.id.action_count);
        save.setVisible(false);
        count.setVisible(false);

        if (presenterImpl != null) {
            presenterImpl.manageToolbarCount();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (presenterImpl != null) {
                    presenterImpl.saveSelectedMedia();
                }
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((GlobalApplication) getApplication()).getAppComponent().inject(this);
        Fabric.with(this, new Crashlytics());
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_media_list);
        initViews();
        presenterImpl = new MediaListPresenterImpl(this, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (presenterImpl != null) {
            presenterImpl.onSaveInstanceState(outState);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (presenterImpl != null) {
            presenterImpl.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        /* Do something */
        if (presenterImpl != null && event != null) {
            if (event.isCameraEventClicked()) {
                presenterImpl.showCameraClickedPics();
            } else {
                presenterImpl.updateSelectedMedia(event.getMediaItemBean());
            }
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        spinnerFolder = findViewById(R.id.spinner_folder);
        emptyView = findViewById(R.id.empty_view);
        recyclerView = findViewById(R.id.media_recycler);
        fabCamera = findViewById(R.id.fab_camera);
        fastScroller = findViewById(R.id.fastscroller);
    }

    @Override
    public ImageView getEmptyView() {
        return emptyView;
    }

    @Override
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    public FloatingActionButton getCameraBtn() {
        return fabCamera;
    }

    @Override
    public RecyclerViewFastScroller getFastScroller() {
        return fastScroller;
    }

    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public AppCompatSpinner getSpinner() {
        return spinnerFolder;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionsAndroid.WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Show option to update profile
                    if (presenterImpl != null) {
                        presenterImpl.getMediaFolders();
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Camera/Storage permission Denied", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PickerActivity.PICKER_REQUEST_CODE) {
                ArrayList<MediaItemBean> selectedMediaList = data.getParcelableArrayListExtra(Constants.SelectedMediaObj);
                if (selectedMediaList != null) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(Constants.SelectedMediaObj, selectedMediaList);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        }
    }
}
