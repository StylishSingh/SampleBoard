package com.sampleboard.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Anuj Sharma on 4/10/2017.
 */

public class PostDetailBean implements Parcelable {
    public String photoUrl;
    public String photoName;
    public String ownerName;
    public String ownerPicUrl;
    public int likeCount;
    public int commentCount;
    public boolean isLiked;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.photoUrl);
        dest.writeString(this.photoName);
        dest.writeString(this.ownerName);
        dest.writeString(this.ownerPicUrl);
        dest.writeInt(this.likeCount);
        dest.writeInt(this.commentCount);
        dest.writeByte(this.isLiked ? (byte) 1 : (byte) 0);
    }

    public PostDetailBean() {
    }

    protected PostDetailBean(Parcel in) {
        this.photoUrl = in.readString();
        this.photoName = in.readString();
        this.ownerName = in.readString();
        this.ownerPicUrl = in.readString();
        this.likeCount = in.readInt();
        this.commentCount = in.readInt();
        this.isLiked = in.readByte() != 0;
    }

    public static final Parcelable.Creator<PostDetailBean> CREATOR = new Parcelable.Creator<PostDetailBean>() {
        @Override
        public PostDetailBean createFromParcel(Parcel source) {
            return new PostDetailBean(source);
        }

        @Override
        public PostDetailBean[] newArray(int size) {
            return new PostDetailBean[size];
        }
    };
}
