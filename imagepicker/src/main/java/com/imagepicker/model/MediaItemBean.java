package com.imagepicker.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

/**
 * auther Anuj Sharma on 9/18/2017.
 */

public class MediaItemBean implements Parcelable {

    private SparseArray<MediaItemBean> seelctedItemMap;

    private boolean isSelected;
    private String id;
    private String mediaName;
    private String mediaExtenstion;
    private long mediaSize;
    private String mediaPath;
    private String croppedPath;
    private String mimeType;
    private String dateAdded;
    private String mediaType;
    private String title;
    private int width;
    private int height;

    public SparseArray<MediaItemBean> getSeelctedItemMap() {
        return seelctedItemMap;
    }

    public void setSeelctedItemMap(SparseArray<MediaItemBean> seelctedItemMap) {
        this.seelctedItemMap = seelctedItemMap;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getMediaName() {
        return mediaName;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public String getMediaExtenstion() {
        return mediaExtenstion;
    }

    public void setMediaExtenstion(String mediaExtenstion) {
        this.mediaExtenstion = mediaExtenstion;
    }

    public long getMediaSize() {
        return mediaSize;
    }

    public void setMediaSize(long mediaSize) {
        this.mediaSize = mediaSize;
    }

    public String getMediaPath() {
        return mediaPath;
    }

    public void setMediaPath(String mediaPath) {
        this.mediaPath = mediaPath;
    }

    public String getCroppedPath() {
        return croppedPath;
    }

    public void setCroppedPath(String croppedPath) {
        this.croppedPath = croppedPath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public MediaItemBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSparseArray((SparseArray) this.seelctedItemMap);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
        dest.writeString(this.id);
        dest.writeString(this.mediaName);
        dest.writeString(this.mediaExtenstion);
        dest.writeLong(this.mediaSize);
        dest.writeString(this.mediaPath);
        dest.writeString(this.croppedPath);
        dest.writeString(this.mimeType);
        dest.writeString(this.dateAdded);
        dest.writeString(this.mediaType);
        dest.writeString(this.title);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
    }

    protected MediaItemBean(Parcel in) {
        this.seelctedItemMap = in.readSparseArray(MediaItemBean.class.getClassLoader());
        this.isSelected = in.readByte() != 0;
        this.id = in.readString();
        this.mediaName = in.readString();
        this.mediaExtenstion = in.readString();
        this.mediaSize = in.readLong();
        this.mediaPath = in.readString();
        this.croppedPath = in.readString();
        this.mimeType = in.readString();
        this.dateAdded = in.readString();
        this.mediaType = in.readString();
        this.title = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
    }

    public static final Creator<MediaItemBean> CREATOR = new Creator<MediaItemBean>() {
        @Override
        public MediaItemBean createFromParcel(Parcel source) {
            return new MediaItemBean(source);
        }

        @Override
        public MediaItemBean[] newArray(int size) {
            return new MediaItemBean[size];
        }
    };
}
