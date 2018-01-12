package com.sampleboard.bean;

/**
 * Created by AnujSharma on 12/27/2017.
 */

public class CommentItemBean {
    /**
     * commentId : 1
     * type : text
     * isOwner : true
     * text : text message
     * time : 2 days before
     * media : https://static.pexels.com/photos/34950/pexels-photo.jpg
     * thumbnail :
     * isLiked : true
     * like_count : 123
     * user_name : Anuj Sharma
     * user_profile_pic :
     */

    private int commentId;
    private String type;
    private boolean isOwner;
    private String text;
    private String time;
    private String media;
    private String thumbnail;
    private boolean isLiked;
    private int like_count;
    private String user_name;
    private String user_profile_pic;

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isIsOwner() {
        return isOwner;
    }

    public void setIsOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean isIsLiked() {
        return isLiked;
    }

    public void setIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_profile_pic() {
        return user_profile_pic;
    }

    public void setUser_profile_pic(String user_profile_pic) {
        this.user_profile_pic = user_profile_pic;
    }
}
