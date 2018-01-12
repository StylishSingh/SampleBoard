package com.sampleboard.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.sampleboard.bean.LikedBean;

import java.util.ArrayList;
import java.util.List;

/**
 * author by Anuj Sharma on 12/15/2017.
 */

public class DetailFragmentViewModel extends ViewModel {

    public List<LikedBean> loadDummyRelatedData() {
        List<LikedBean> likeList = new ArrayList<>();
        LikedBean obj = new LikedBean();
        obj.imageUrl = "https://i.ytimg.com/vi/x30YOmfeVTE/maxresdefault.jpg";

        likeList.add(obj);

        obj = new LikedBean();
        obj.imageName = "Testing image";
        obj.imageUrl = "https://upload.wikimedia.org/wikipedia/commons/3/36/Hopetoun_falls.jpg";

        likeList.add(obj);

        obj = new LikedBean();
        obj.imageName = "Testing image";
        obj.imageUrl = "https://static.pexels.com/photos/33109/fall-autumn-red-season.jpg";

        likeList.add(obj);

        obj = new LikedBean();
        obj.imageName = "Testing image";
        obj.imageUrl = "https://static.pexels.com/photos/39811/pexels-photo-39811.jpeg";

        likeList.add(obj);

        return likeList;

    }
    public int updateLikesCounter(int count, boolean isIncreased) {
        if (isIncreased)
            return count + 1;
        else
            return count - 1;
    }
}
