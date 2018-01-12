package com.imagepicker.ui.selectedMedia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;

import com.imagepicker.R;
import com.imagepicker.adapter.MediaPagerAdapter;
import com.imagepicker.adapter.SelectedMediaAdapter;
import com.imagepicker.model.MediaItemBean;
import com.imagepicker.model.MessageEvent;
import com.imagepicker.utils.Constants;
import com.imagepicker.utils.SpacesItemDecoration;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

/**
 * auther Anuj Sharma on 9/21/2017.
 */

public class SelectedMediaPresenterImpl implements SelectedMediaPresenter {

    private SelectedMediaActivity selectedMediaActivity;
    private SelectedMediaView selectedMediaView;
    private ArrayList<MediaItemBean> selectedMediaList;

    private SelectedMediaAdapter adapter;

    private PagerAdapter pagerAdapter;

    SelectedMediaPresenterImpl(SelectedMediaActivity selectedMediaActivity, SelectedMediaView selectedMediaView, SparseArray<MediaItemBean> selectedMediaMap) {
        this.selectedMediaActivity = selectedMediaActivity;
        this.selectedMediaView = selectedMediaView;
        if (this.selectedMediaList == null) this.selectedMediaList = new ArrayList<>();
        for (int i = 0; i < selectedMediaMap.size(); i++) {
            int key = selectedMediaMap.keyAt(i);
            this.selectedMediaList.add(selectedMediaMap.get(key));
        }
        init();
    }

    private void init() {
        selectedMediaActivity.setSupportActionBar(selectedMediaView.getToolbar());
        selectedMediaActivity.getSupportActionBar().setTitle(R.string.title_crop_image);
        selectedMediaActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        selectedMediaView.getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedMediaActivity.finish();
            }
        });

        LinearLayoutManager lm = new LinearLayoutManager(selectedMediaActivity);
        lm.setOrientation(LinearLayoutManager.HORIZONTAL);
        selectedMediaView.getSelectedMediaRecycler().setLayoutManager(lm);
        int spacingInPixels = selectedMediaActivity.getResources().getDimensionPixelSize(R.dimen.margin_4);
        selectedMediaView.getSelectedMediaRecycler().addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        adapter = new SelectedMediaAdapter(selectedMediaActivity, selectedMediaList, this);
        selectedMediaView.getSelectedMediaRecycler().setAdapter(adapter);
        adapter.selectedItem = 0;
        adapter.notifyDataSetChanged();


        selectedMediaView.getSelectedViewPager().addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (adapter != null) {
                    //change adapter current position
                    adapter.selectedItem = position;
                    adapter.notifyDataSetChanged();
                    if (selectedMediaList.get(position).getMimeType().equalsIgnoreCase("video/mp4")) {
                        //hide crop option
                        selectedMediaActivity.cropMenu.setVisible(false);
                    } else {
                        selectedMediaActivity.cropMenu.setVisible(true);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //set pager pagerAdapter
        pagerAdapter = new MediaPagerAdapter(selectedMediaActivity,
                selectedMediaList, this);
        selectedMediaView.getSelectedViewPager().setAdapter(pagerAdapter);
    }

    MediaItemBean getSelectedMediaObj() {
        if (selectedMediaView.getSelectedViewPager() != null && adapter != null) {
            return adapter.getList().get(selectedMediaView.getSelectedViewPager().getCurrentItem());
        }
        return null;
    }

    /*
    Delete Selected current Media
     */
    void deleteMedia() {
        if (adapter != null && selectedMediaView.getSelectedViewPager() != null && pagerAdapter != null) {
            //get current position
            int position = selectedMediaView.getSelectedViewPager().getCurrentItem();
            MessageEvent obj = new MessageEvent();
            obj.setMediaItemBean(adapter.getList().get(position));
            selectedMediaList.remove(position);
            adapter.notifyItemRemoved(position);
            ((MediaPagerAdapter) pagerAdapter).updateList(selectedMediaList);

            if (adapter.getList().size() == 0) {
                //reset all view
                selectedMediaActivity.finish();
            } else {
                //update adapter
                if (adapter.selectedItem > 0) {
                    adapter.selectedItem = position - 1;
                    selectedMediaView.getSelectedViewPager().setCurrentItem(adapter.selectedItem);
                } else {
                    adapter.selectedItem = 0;
                    selectedMediaView.getSelectedViewPager().setCurrentItem(0);
                }

            }
            adapter.notifyItemChanged(position);
            //set broadcast so that mediaList items can also be removed
            EventBus.getDefault().postSticky(obj);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == selectedMediaActivity.CROP_IMAGE_REQUEST_CODE) {
                if (data.getData() == null) {
                    return;
                }
                saveCroppedImage(data.getData().toString());
            }
        }
    }


    private void saveCroppedImage(final String croppedPath) {
        System.out.println("Cropped Path-> " + croppedPath);
        final int position = selectedMediaView.getSelectedViewPager().getCurrentItem();
        File croppedFile = saveMediaToDirectory(selectedMediaActivity, selectedMediaList.get(position).getMediaName());

        if (croppedFile != null) {
            Single.just(croppedFile)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<File>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(File file) {
                            FileOutputStream out = null;
                            try {
                                out = new FileOutputStream(file);
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(selectedMediaActivity.getContentResolver(), Uri.parse(croppedPath));
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                                // PNG is a lossless format, the compression factor (100) is ignored


                                //update adapter
                                selectedMediaList.get(position).setCroppedPath(file.getAbsolutePath());
//                                selectedMediaList.get(position).setMediaPath(file.getAbsolutePath());
                                adapter.notifyItemChanged(position);
                                ((MediaPagerAdapter) pagerAdapter).updateList(selectedMediaList);

                                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                mediaScanIntent.setData(Uri.fromFile(file));
                                selectedMediaActivity.sendBroadcast(mediaScanIntent);

                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    if (out != null) {
                                        out.close();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });
        }
    }

    private File saveMediaToDirectory(Context ctx, String name) {
        File dir = new File(Environment.getExternalStorageDirectory() + File.separator +
                ctx.getString(R.string.app_name) + File.separator + ctx.getString(R.string.folder_name_crop));
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }
        File file = new File(dir, name);
        if (file.isFile()) file.delete();
        try {
            file.createNewFile();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void onSaveClick() {
        Intent returnIntent = new Intent();
        returnIntent.putParcelableArrayListExtra(Constants.SelectedMediaObj, selectedMediaList);
        selectedMediaActivity.setResult(Activity.RESULT_OK, returnIntent);
        selectedMediaActivity.finish();
    }

    @Override
    public void onMediaClick(MediaItemBean obj, int position) {
        if (obj != null) {
            selectedMediaView.getSelectedViewPager().setCurrentItem(position);
            adapter.notifyItemChanged(position);
        }
    }

    @Override
    public void onMediaLongClick(MediaItemBean obj, int position, ImageView view) {

    }

    @Override
    public void onMediaUpSwipe() {

    }

    @Override
    public void onMediaDownSwipe() {

    }


}
