package com.imagepicker.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * author by Anuj Sharma on 10/12/2017.
 */

public class FolderBean implements Parcelable {
    private String id;
    String parent_id;
    String coverPicName;
    String coverPicPath;
    String folderName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getCoverPicName() {
        return coverPicName;
    }

    public void setCoverPicName(String coverPicName) {
        this.coverPicName = coverPicName;
    }

    public String getCoverPicPath() {
        return coverPicPath;
    }

    public void setCoverPicPath(String coverPicPath) {
        this.coverPicPath = coverPicPath;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.parent_id);
        dest.writeString(this.coverPicName);
        dest.writeString(this.coverPicPath);
        dest.writeString(this.folderName);
    }

    public FolderBean() {
    }

    protected FolderBean(Parcel in) {
        this.id = in.readString();
        this.parent_id = in.readString();
        this.coverPicName = in.readString();
        this.coverPicPath = in.readString();
        this.folderName = in.readString();
    }

    public static final Parcelable.Creator<FolderBean> CREATOR = new Parcelable.Creator<FolderBean>() {
        @Override
        public FolderBean createFromParcel(Parcel source) {
            return new FolderBean(source);
        }

        @Override
        public FolderBean[] newArray(int size) {
            return new FolderBean[size];
        }
    };
}
