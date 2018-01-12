package com.sampleboard.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mobilyte India Pvt Ltd on 3/1/2017.
 */

public class PhotosBean implements Parcelable {
    public String title;
    public String photoUrl;
    public String price;
    public String photoName;
    public int availableCount;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.photoUrl);
        dest.writeString(this.price);
        dest.writeString(this.photoName);
        dest.writeInt(this.availableCount);
    }

    public PhotosBean() {
    }

    protected PhotosBean(Parcel in) {
        this.title = in.readString();
        this.photoUrl = in.readString();
        this.price = in.readString();
        this.photoName = in.readString();
        this.availableCount = in.readInt();
    }

    public static final Parcelable.Creator<PhotosBean> CREATOR = new Parcelable.Creator<PhotosBean>() {
        @Override
        public PhotosBean createFromParcel(Parcel source) {
            return new PhotosBean(source);
        }

        @Override
        public PhotosBean[] newArray(int size) {
            return new PhotosBean[size];
        }
    };
}
