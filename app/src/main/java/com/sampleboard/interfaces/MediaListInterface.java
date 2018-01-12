package com.sampleboard.interfaces;

import android.widget.ImageView;

import com.sampleboard.bean.MediaItem;
import com.sampleboard.bean.PhotosBean;

/**
 * author by Anuj Sharma on 12/13/2017.
 */

public interface MediaListInterface {
    void onItemClick(MediaItem obj, ImageView imageView, int position);
}
