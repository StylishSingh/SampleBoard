package com.imagepicker.ui.mediaList;

import android.widget.ImageView;

import com.imagepicker.model.MediaItemBean;

/**
 * Created by Anuj Sharma on 9/18/2017.
 */

public interface MediaListPresenter {

    void onMediaItemLongClick(MediaItemBean obj, int position, ImageView view);

    void onMediaItemClick(MediaItemBean obj, int position);

}