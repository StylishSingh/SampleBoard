package com.sampleboard.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by AnujSharma on 12/27/2017.
 */

public class CommentBean extends BaseModel {

    /**
     * status : 200
     * data : {"total_count":200,"comment_list":[{"commentId":1,"type":"text","isOwner":true,"text":"text message","time":"2 days before","media":"https://static.pexels.com/photos/34950/pexels-photo.jpg","thumbnail":"","isLiked":true,"like_count":123,"user_name":"Anuj Sharma","user_profile_pic":""},{"commentId":2,"type":"image","isOwner":false,"text":"text message","time":"2 days before","media":"https://static.pexels.com/photos/34950/pexels-photo.jpg","thumbnail":"","isLiked":false,"like_count":1,"user_name":"Pal Hardy","user_profile_pic":""}]}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * total_count : 200
         * comment_list : [{"commentId":1,"type":"text","isOwner":true,"text":"text message","time":"2 days before","media":"https://static.pexels.com/photos/34950/pexels-photo.jpg","thumbnail":"","isLiked":true,"like_count":123,"user_name":"Anuj Sharma","user_profile_pic":""},{"commentId":2,"type":"image","isOwner":false,"text":"text message","time":"2 days before","media":"https://static.pexels.com/photos/34950/pexels-photo.jpg","thumbnail":"","isLiked":false,"like_count":1,"user_name":"Pal Hardy","user_profile_pic":""}]
         */

        private int total_count;
        private List<CommentItemBean> comment_list;

        public int getTotal_count() {
            return total_count;
        }

        public void setTotal_count(int total_count) {
            this.total_count = total_count;
        }

        public List<CommentItemBean> getComment_list() {
            return comment_list;
        }

        public void setComment_list(List<CommentItemBean> comment_list) {
            this.comment_list = comment_list;
        }


    }
}
