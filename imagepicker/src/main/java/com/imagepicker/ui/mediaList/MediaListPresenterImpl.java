package com.imagepicker.ui.mediaList;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.imagepicker.R;
import com.imagepicker.adapter.FolderSpinnerAdapter;
import com.imagepicker.adapter.MediaListAdapter;
import com.imagepicker.model.FolderBean;
import com.imagepicker.model.MediaItemBean;
import com.imagepicker.ui.PickerActivity;
import com.imagepicker.ui.camera.CameraActivity;
import com.imagepicker.ui.selectedMedia.SelectedMediaActivity;
import com.imagepicker.utils.Constants;
import com.imagepicker.utils.PermissionsAndroid;
import com.imagepicker.utils.SpacesItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.operators.single.SingleFlatMapIterableObservable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * auther Anuj Sharma on 9/18/2017.
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class MediaListPresenterImpl implements MediaListPresenter, LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    private final int GET_FOLDER_CODE = 101, GET_MEDIA_CODE = 102;
    private MediaListActivity mediaListActivity;
    private MediaListView mediaListView;
    private SparseArray<MediaItemBean> selectedMediaMap;
    private ArrayList<FolderBean> folderList;
    private MediaListAdapter adapter;
    private FolderSpinnerAdapter folderSpinnerAdapter;
    private String selectedFolderName;

    private DisposableObserver disposableObserver;
    //Variables related to Media Projections

 /*   String allMediaSelection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
            + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
            + " OR "
            + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
            + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;


    String imageSelection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
            + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

    String videoSelection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
            + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

    // Get relevant columns for use later.
    private String[] projection = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.WIDTH,
            MediaStore.Files.FileColumns.HEIGHT,

    };
//    private Uri queryUri = MediaStore.Files.getContentUri("external");
//    private Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;*/


    MediaListPresenterImpl(MediaListActivity mediaListActivity, MediaListView mediaListView) {
        this.mediaListActivity = mediaListActivity;
        this.mediaListView = mediaListView;
        init();
    }

    private void init() {
        // observer will receive all events.

        mediaListActivity.setSupportActionBar(mediaListView.getToolbar());
        mediaListActivity.getSupportActionBar().setTitle("");
        mediaListActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mediaListView.getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaListActivity.finish();
            }
        });
        mediaListView.getCameraBtn().setOnClickListener(this);
        StaggeredGridLayoutManager sm = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mediaListView.getRecyclerView().setLayoutManager(sm);
        mediaListView.getRecyclerView().setHasFixedSize(true);
        mediaListView.getRecyclerView().setItemViewCacheSize(20);
        mediaListView.getRecyclerView().setDrawingCacheEnabled(true);
        mediaListView.getRecyclerView().setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        int spacingInPixels = mediaListActivity.getResources().getDimensionPixelSize(R.dimen.margin_4);
        mediaListView.getRecyclerView().addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        adapter = new MediaListAdapter(null, this);
        mediaListView.getRecyclerView().setAdapter(adapter);

        mediaListView.getFastScroller().setRecyclerView(mediaListView.getRecyclerView());
        mediaListView.getFastScroller().setViewsToUse(R.layout.recycler_view_fast_scroller__fast_scroller, R.id.fastscroller_bubble, R.id.fastscroller_handle);
        mediaListView.getFastScroller().setVisibility(View.GONE);
        mediaListView.getSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long l) {
                switch (parent.getId()) {
                    case R.id.spinner_folder:
                        if (folderList != null && folderList.size() > 0) {
                            //load media for selected folder
                            selectedFolderName = folderList.get(position).getCoverPicPath().substring(0, folderList.get(position).getCoverPicPath().lastIndexOf("/") + 1);
                            System.out.println("Selected Folder Path-> " + selectedFolderName);
                            if (PermissionsAndroid.getInstance().checkWriteExternalStoragePermission(mediaListActivity)) {
                                mediaListActivity.getLoaderManager().restartLoader(GET_MEDIA_CODE, null, MediaListPresenterImpl.this);
                            } else {
                                PermissionsAndroid.getInstance().requestForWriteExternalStoragePermission(mediaListActivity);
                            }
                        }
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        getMediaFolders();
    }

    void getMediaFolders() {
        //check read/write storage permission
        if (PermissionsAndroid.getInstance().checkWriteExternalStoragePermission(mediaListActivity)) {
            mediaListActivity.getLoaderManager().initLoader(GET_FOLDER_CODE, null, this);
        } else {
            PermissionsAndroid.getInstance().requestForWriteExternalStoragePermission(mediaListActivity);
        }
    }

    @Override
    public void onMediaItemLongClick(MediaItemBean obj, int positon, ImageView view) {
        //Navigate user to MediaDetail screen

    }

    @Override
    public void onMediaItemClick(final MediaItemBean obj, final int position) {
        //select
        if (selectedMediaMap == null) selectedMediaMap = new SparseArray<>();
        Single.just(obj)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<MediaItemBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        System.out.println("on subscribe-> " + d.isDisposed());
                    }

                    @Override
                    public void onSuccess(MediaItemBean mediaItemBean) {
                        if (mediaItemBean.isSelected()) {
                            mediaItemBean.setSelected(false);
                            selectedMediaMap.remove(Integer.parseInt(mediaItemBean.getId()));
                        } else {
                            mediaItemBean.setSelected(true);
                            selectedMediaMap.put(Integer.parseInt(mediaItemBean.getId()), mediaItemBean);
                        }
                        adapter.getList().set(position, mediaItemBean);
                        adapter.notifyItemChanged(position);
                        manageToolbarCount();
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("Error-> " + e.getMessage());
                    }
                });
        System.out.println("Select Id-> " + obj.getId());
    }

    protected void manageToolbarCount() {
        if (selectedMediaMap == null) return;
        //update selected media count on toolbar
        if (mediaListActivity.count != null && mediaListActivity.save != null) {
            mediaListActivity.count.setTitle(String.valueOf(selectedMediaMap.size()));
            if (selectedMediaMap.size() > 0) {
                mediaListActivity.count.setVisible(true);
                mediaListActivity.save.setVisible(true);
            } else {
                mediaListActivity.count.setVisible(false);
                mediaListActivity.save.setVisible(false);
            }
        }
    }

    protected void updateSelectedMedia(MediaItemBean mediaItemBean) {
        if (selectedMediaMap != null) {
            //Now remove selected Items from arraylist
            Single.just(mediaItemBean).subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<MediaItemBean>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(MediaItemBean mediaItemBean) {

                            for (int i = 0; i < adapter.getList().size(); i++) {
                                if (adapter.getList().get(i).getId().equals(mediaItemBean.getId())) {
                                    adapter.getList().get(i).setSelected(false);
                                    adapter.notifyItemChanged(i);
                                }
                            }
                            selectedMediaMap.remove(Integer.parseInt(mediaItemBean.getId()));

                            manageToolbarCount();
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });
        }


    }

    protected void showCameraClickedPics() {
        File dir = new File(Environment.getExternalStorageDirectory() + File.separator +
                mediaListActivity.getString(R.string.app_name));
        if (dir.isDirectory()) {
            if (folderSpinnerAdapter != null) {
                int position = folderSpinnerAdapter.getAppCameraPosition();
                if (mediaListView.getSpinner().getSelectedItemPosition() == position) {
                    selectedFolderName = folderList.get(position).getCoverPicPath().substring(0, folderList.get(position).getCoverPicPath().lastIndexOf("/") + 1);
                    System.out.println("Selected Folder Path-> " + selectedFolderName);
                    if (PermissionsAndroid.getInstance().checkWriteExternalStoragePermission(mediaListActivity)) {
                        mediaListActivity.getLoaderManager().restartLoader(GET_MEDIA_CODE, null, MediaListPresenterImpl.this);
                    } else {
                        PermissionsAndroid.getInstance().requestForWriteExternalStoragePermission(mediaListActivity);
                    }
                } else {
                    mediaListView.getSpinner().setSelection(position);
                }
            }
            /*selectedFolderName = dir.getAbsolutePath();
            System.out.println("Selected Folder Path-> " + selectedFolderName);
            if (PermissionsAndroid.getInstance().checkWriteExternalStoragePermission(mediaListActivity)) {
                mediaListActivity.getLoaderManager().restartLoader(GET_MEDIA_CODE, null, MediaListPresenterImpl.this);
            } else {
                PermissionsAndroid.getInstance().requestForWriteExternalStoragePermission(mediaListActivity);
            }*/
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri queryUri = MediaStore.Files.getContentUri("external");
        switch (i) {
            case GET_FOLDER_CODE:
                String folderSelection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE +
                        ") GROUP BY (" + MediaStore.Files.FileColumns.PARENT;
                String sortOrder = MediaStore.Files.FileColumns.PARENT + " DESC";
                String[] folderProjection = new String[]{
                        MediaStore.Files.FileColumns._ID,
                        MediaStore.Files.FileColumns.PARENT,
                        MediaStore.Files.FileColumns.DATA,
                        MediaStore.Files.FileColumns.DISPLAY_NAME,
                        MediaStore.Files.FileColumns.TITLE
                };
                return new CursorLoader(
                        mediaListActivity,
                        queryUri,
                        folderProjection,
                        folderSelection,
                        null, // Selection args (none).
                        sortOrder // Sort order.
                );
            case GET_MEDIA_CODE:
                String[] projection = {
                        MediaStore.Files.FileColumns._ID,
                        MediaStore.Files.FileColumns.DATA,
                        MediaStore.Files.FileColumns.DATE_ADDED,
                        MediaStore.Files.FileColumns.MEDIA_TYPE,
                        MediaStore.Files.FileColumns.MIME_TYPE,
                        MediaStore.Files.FileColumns.TITLE,
                        MediaStore.Files.FileColumns.DISPLAY_NAME,
                        MediaStore.Files.FileColumns.SIZE,
                        MediaStore.Files.FileColumns.WIDTH,
                        MediaStore.Files.FileColumns.HEIGHT,

                };
                return new CursorLoader(
                        mediaListActivity,
                        queryUri,
                        projection,
                        MediaStore.Images.Media.DATA + " like ? ",
                        new String[]{"%" + selectedFolderName + "%"},
                        MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
                );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
        if (data != null) {
            switch (loader.getId()) {
                case GET_FOLDER_CODE:
                    if (data.moveToFirst()) {
                        int _id = data.getColumnIndex(
                                MediaStore.Files.FileColumns._ID);
                        int _parent = data.getColumnIndex(MediaStore.Files.FileColumns.PARENT);
//                        int _coverImageName = data.getColumnIndex(
//                                MediaStore.Files.FileColumns.DISPLAY_NAME);
                        int _title = data.getColumnIndex(MediaStore.Files.FileColumns.TITLE);
                        int _coverImgPath = data.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                        if (folderList == null) folderList = new ArrayList<>();

                        do {
                            String id = data.getString(_id);
                            String parent_id = data.getString(_parent);
//                            String coverImgName = data.getString(_coverImageName);
                            String coverImgTitle = data.getString(_title);
                            String coverPicPath = data.getString(_coverImgPath);
                            //get folder name from cover pic path
                            String[] stringArray = coverPicPath.split("/");
                            String folderName = stringArray[stringArray.length - 2];

                            FolderBean obj = new FolderBean();
                            obj.setId(id);
                            obj.setCoverPicName(coverImgTitle);
                            obj.setCoverPicPath(coverPicPath);
                            obj.setFolderName(folderName);

                            folderList.add(obj);
                            System.out.println("ID column-> " + id);
                            System.out.println("Parent column-> " + parent_id);
//                            System.out.println("cover image name -> " + coverImgName);
                            System.out.println("cover Image Title -> " + coverImgTitle);
                            System.out.println("cover Image path-> " + coverPicPath);
                            System.out.println("Folder Name-> " + folderName);

                        } while (data.moveToNext());
                        folderSpinnerAdapter = new FolderSpinnerAdapter(mediaListActivity, R.layout.spinner_item, R.id.category_name, folderList);
                        mediaListView.getSpinner().setAdapter(folderSpinnerAdapter);

                    }
                    break;
                case GET_MEDIA_CODE:
                    if (data.moveToFirst()) {
                        Single.just(data).observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(new SingleObserver<Cursor>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onSuccess(Cursor cursor) {
                                        int id = data.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                                        int mediaData = data.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                                        int mediaName = data.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                                        int mimeType = data.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE);
                                        int mediaSize = data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE);
                                        int dateAdded = data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED);
                                        int mediaType = data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE);
                                        int title = data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE);
                                        int width = data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.WIDTH);
                                        int height = data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.HEIGHT);
                                        List<MediaItemBean> mediaList = new ArrayList<>();
                                        do {
                                            MediaItemBean obj = new MediaItemBean();
                                            obj.setId(data.getString(id));
                                            obj.setMediaPath(data.getString(mediaData));
                                            obj.setMediaName(data.getString(mediaName));
                                            obj.setMimeType(data.getString(mimeType));
                                            obj.setMediaSize(data.getLong(mediaSize));
                                            obj.setMediaType(data.getString(mediaType));
                                            obj.setTitle(data.getString(title));
                                            obj.setWidth(data.getInt(width));
                                            obj.setHeight(data.getInt(height));
                                            obj.setDateAdded(data.getString(dateAdded));
                                            if (selectedMediaMap != null && selectedMediaMap.get(Integer.parseInt(obj.getId())) != null) {
                                                obj.setSelected(true);
                                            }
                                            mediaList.add(obj);
                                        } while (cursor.moveToNext());
                                        if (mediaList.size() > 0) {
                                            hideEmptyView();
                                            adapter.updateList(mediaList);
                                            if (mediaList.size() > 20) {
                                                mediaListView.getFastScroller().setVisibility(View.VISIBLE);
                                            } else {
                                                mediaListView.getFastScroller().setVisibility(View.GONE);
                                            }
                                        } else {
                                            showEmptyView();
                                            adapter.updateList(null);
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }
                                });


                        /*int id = data.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                        int mediaData = data.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        int mediaName = data.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                        int mimeType = data.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE);
                        int mediaSize = data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE);
                        int dateAdded = data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED);
                        int mediaType = data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE);
                        int title = data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE);
                        int width = data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.WIDTH);
                        int height = data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.HEIGHT);
                        List<MediaItemBean> mediaList = new ArrayList<>();
                        do {
                            MediaItemBean obj = new MediaItemBean();
                            obj.setId(data.getString(id));
                            obj.setMediaPath(data.getString(mediaData));
                            obj.setMediaName(data.getString(mediaName));
                            obj.setMimeType(data.getString(mimeType));
                            obj.setMediaSize(data.getLong(mediaSize));
                            obj.setMediaType(data.getString(mediaType));
                            obj.setTitle(data.getString(title));
                            obj.setWidth(data.getInt(width));
                            obj.setHeight(data.getInt(height));
                            obj.setDateAdded(data.getString(dateAdded));
                            if (selectedMediaMap != null && selectedMediaMap.get(Integer.parseInt(obj.getId())) != null) {
                                obj.setSelected(true);
                            }
                            mediaList.add(obj);
                        }
                        while (data.moveToNext());
                        if (mediaList.size() > 0) {
                            hideEmptyView();
                            adapter.updateList(mediaList);
                            if (mediaList.size() > 20) {
                                mediaListView.getFastScroller().setVisibility(View.VISIBLE);
                            } else {
                                mediaListView.getFastScroller().setVisibility(View.GONE);
                            }
                        } else {
                            showEmptyView();
                            adapter.updateList(null);
                        }*/
                    }
                    break;
            }
        }


           /* Single.just(data)
                    .flatMapObservable(new Function<Cursor, ObservableSource<MediaItemBean>>() {
                        @Override
                        public ObservableSource<MediaItemBean> apply(final Cursor data) throws Exception {
                            return Observable.create(new ObservableOnSubscribe<MediaItemBean>() {
                                @Override
                                public void subscribe(ObservableEmitter<MediaItemBean> e) throws Exception {
                                    while (data.moveToNext()) {

                                        int id = data.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                                        int mediaData = data.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                                        int mediaName = data.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                                        int mimeType = data.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE);
                                        int mediaSize = data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE);
                                        int dateAdded = data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED);
                                        int mediaType = data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE);
                                        int title = data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE);
                                        int width = data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.WIDTH);
                                        int height = data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.HEIGHT);

                                        //Add folder name
                                        if (foldersArray == null) foldersArray = new HashSet<>();
                                        foldersArray.add(data.getString(mediaData).substring(0, data.getString(mediaData).lastIndexOf('/')));


//                                        String imagePath = data.getString(mediaData);
                                        MediaItemBean obj = new MediaItemBean();
                                        obj.setId(data.getString(id));
                                        obj.setMediaPath(data.getString(mediaData));
                                        obj.setMediaName(data.getString(mediaName));
                                        obj.setMimeType(data.getString(mimeType));
                                        obj.setMediaSize(data.getLong(mediaSize));
                                        obj.setMediaType(data.getString(mediaType));
                                        obj.setTitle(data.getString(title));
                                        obj.setWidth(data.getInt(width));
                                        obj.setHeight(data.getInt(height));
                                        obj.setDateAdded(data.getString(dateAdded));

                                        e.onNext(obj);
                                    }
                                    e.onComplete();
                                }
                            }).subscribeOn(Schedulers.computation());
                        }
                    }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .toList()
                    .subscribe(new DisposableSingleObserver<List<MediaItemBean>>() {
                        @Override
                        public void onSuccess(List<MediaItemBean> mediaItemBeans) {
                            if (foldersArray != null && foldersArray.size() > 0) {
                                for (String str : foldersArray) {
                                    System.out.println("Folder-> " + str);
                                }
                            }
                            if (foldersArray == null) {
                                showEmptyView();
                                return;
                            }
                            hideEmptyView();
                            List<String> foldersList = new ArrayList<String>(foldersArray);
                            folderSpinnerAdapter = new FolderSpinnerAdapter(mediaListActivity, R.layout.spinner_item, R.id.category_name, foldersList);
                            mediaListView.getSpinner().setAdapter(folderSpinnerAdapter);

                            mediaItemList = mediaItemBeans;

                        }



                        @Override
                        public void onError(Throwable e) {

                        }
                    });*/
    }

    private void showEmptyView() {
        mediaListView.getEmptyView().setVisibility(View.VISIBLE);
        mediaListView.getSpinner().setVisibility(View.GONE);
    }

    private void hideEmptyView() {
        mediaListView.getEmptyView().setVisibility(View.GONE);
        mediaListView.getSpinner().setVisibility(View.VISIBLE);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    void saveSelectedMedia() {
        MediaItemBean obj = new MediaItemBean();
        obj.setSeelctedItemMap(selectedMediaMap);
        //navigate selected media items to next screen.
        Intent intent = new Intent(mediaListActivity, SelectedMediaActivity.class);
        intent.putExtra(Constants.SELECTED_MEDIA_LIST_OBJ, obj);
        mediaListActivity.startActivityForResult(intent, PickerActivity.PICKER_REQUEST_CODE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_camera:
                if (checkCameraHardware(mediaListActivity))
                    mediaListActivity.startActivity(new Intent(mediaListActivity, CameraActivity.class));
                else
                    Toast.makeText(mediaListActivity, "Camera not found", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware(Context context) {
        return (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) ? true : false;
    }

    protected void onSaveInstanceState(Bundle outState) {
        if (folderList != null) {
            outState.putParcelableArrayList("folder_list", folderList);
        }
        if (null != selectedMediaMap) {
            MediaItemBean obj = new MediaItemBean();
            obj.setSeelctedItemMap(selectedMediaMap);
            outState.putParcelable("selected_media_map", obj);
        }
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.getParcelableArrayList("folder_list") != null) {
            folderList = savedInstanceState.getParcelableArrayList("folder_list");
            folderSpinnerAdapter.updateList(mediaListActivity, folderList);
        }
        if (savedInstanceState.getParcelable("selected_media_map") != null) {
            MediaItemBean obj = savedInstanceState.getParcelable("selected_media_map");
            selectedMediaMap = obj.getSeelctedItemMap();
        }
    }
}
