package com.sampleboard.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * author by Anuj Sharma on 12/14/2017.
 */

public class MediaItem implements Parcelable {
    private String mediaId;
    private String type;
    private String title;
    private String desc;
    private String media;
    private String media_fullsize;
    private String thumbnail;
    private boolean isLiked;
    private boolean isShared;
    private String like_count;

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getMedia_fullsize() {
        return media_fullsize;
    }

    public void setMedia_fullsize(String media_fullsize) {
        this.media_fullsize = media_fullsize;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public boolean isShared() {
        return isShared;
    }

    public void setShared(boolean shared) {
        isShared = shared;
    }

    public String getLike_count() {
        return like_count;
    }

    public void setLike_count(String like_count) {
        this.like_count = like_count;
    }

    public MediaItem() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mediaId);
        dest.writeString(this.type);
        dest.writeString(this.title);
        dest.writeString(this.desc);
        dest.writeString(this.media);
        dest.writeString(this.media_fullsize);
        dest.writeString(this.thumbnail);
        dest.writeByte(this.isLiked ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isShared ? (byte) 1 : (byte) 0);
        dest.writeString(this.like_count);
    }

    protected MediaItem(Parcel in) {
        this.mediaId = in.readString();
        this.type = in.readString();
        this.title = in.readString();
        this.desc = in.readString();
        this.media = in.readString();
        this.media_fullsize = in.readString();
        this.thumbnail = in.readString();
        this.isLiked = in.readByte() != 0;
        this.isShared = in.readByte() != 0;
        this.like_count = in.readString();
    }

    public static final Creator<MediaItem> CREATOR = new Creator<MediaItem>() {
        @Override
        public MediaItem createFromParcel(Parcel source) {
            return new MediaItem(source);
        }

        @Override
        public MediaItem[] newArray(int size) {
            return new MediaItem[size];
        }
    };
}
