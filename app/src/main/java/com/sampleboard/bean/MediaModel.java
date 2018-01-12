package com.sampleboard.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * author by Anuj Sharma on 12/7/2017.
 */

public class MediaModel extends BaseModel {

    /**
     * data : {"media_list":[{"mediaId":1,"type":"image","title":"Natuaral view on railway track","desc":"Natuaral view on railway track","media":"https://static.pexels.com/photos/34950/pexels-photo.jpg","media_fullsize":"https://static.pexels.com/photos/34950/pexels-photo.jpg","thumbnail":""},{"mediaId":2,"type":"image","title":"Walking Pug","desc":"101","media":"http://cdn2-www.dogtime.com/assets/uploads/gallery/pug-dog-breed-pictures/7-running.jpg","media_fullsize":"http://cdn2-www.dogtime.com/assets/uploads/gallery/pug-dog-breed-pictures/7-running.jpg","thumbnail":""},{"mediaId":3,"type":"image","title":"Marvelous White tiger","desc":"Marvelous White tiger","media":"https://c1.staticflickr.com/8/7573/27561606350_c3571d97d7_b.jpg","media_fullsize":"https://c1.staticflickr.com/8/7573/27561606350_c3571d97d7_b.jpg","thumbnail":""},{"mediaId":4,"type":"image","title":"John Snow","desc":"101","media":"https://www.thesun.co.uk/wp-content/uploads/2017/08/nintchdbpict0003465565164.jpg","media_fullsize":"https://www.thesun.co.uk/wp-content/uploads/2017/08/nintchdbpict0003465565164.jpg","thumbnail":""},{"mediaId":5,"type":"image","title":"Cute baby","desc":"101","media":"https://i.ytimg.com/vi/DSnbZUjIyAc/maxresdefault.jpg","media_fullsize":"https://i.ytimg.com/vi/DSnbZUjIyAc/maxresdefault.jpg","thumbnail":""},{"mediaId":6,"type":"image","title":"Mother's love","desc":"101","media":"https://www.babycenter.com/ims/2016/09/iStock_83513033_4x3.jpg","media_fullsize":"https://www.babycenter.com/ims/2016/09/iStock_83513033_4x3.jpg","thumbnail":""},{"mediaId":7,"type":"Amazing waterfall","title":"","desc":"101","media":"http://ichef.bbci.co.uk/wwfeatures/wm/live/1280_640/images/live/p0/54/n6/p054n6rx.jpg","media_fullsize":"http://ichef.bbci.co.uk/wwfeatures/wm/live/1280_640/images/live/p0/54/n6/p054n6rx.jpg","thumbnail":""},{"mediaId":8,"type":"image","title":"Dolphin","desc":"101","media":"https://d3jkudlc7u70kh.cloudfront.net/dolphin-fact.jpg","media_fullsize":"https://d3jkudlc7u70kh.cloudfront.net/dolphin-fact.jpg","thumbnail":"3"},{"mediaId":9,"type":"image","title":"Birds","desc":"101","media":"http://howtodoright.com/wp-content/uploads/2017/05/blue-birds.jpg.824x0_q71.jpg","media_fullsize":"http://howtodoright.com/wp-content/uploads/2017/05/blue-birds.jpg.824x0_q71.jpg","thumbnail":""},{"mediaId":10,"type":"image","title":"Apache Helicopter","desc":"101","media":"https://i.ytimg.com/vi/bGrNkl8HzPw/maxresdefault.jpg","media_fullsize":"https://i.ytimg.com/vi/bGrNkl8HzPw/maxresdefault.jpg","thumbnail":""}]}
     */

    @SerializedName("data")
    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        @SerializedName("media_list")
        private List<MediaItem> mediaList;

        public List<MediaItem> getMediaList() {
            return mediaList;
        }

        public void setMediaList(List<MediaItem> mediaList) {
            this.mediaList = mediaList;
        }

    }
}
