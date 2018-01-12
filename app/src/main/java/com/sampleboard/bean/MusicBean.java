package com.sampleboard.bean;

import java.util.List;

/**
 * Created by Anuj Sharma on 3/20/2017.
 */

public class MusicBean {

    /**
     * song : Afreen Afreen
     * url : http://hck.re/Rh8KTk
     * artists : Rahat Fateh Ali Khan, Momina Mustehsan
     * cover_image : http://hck.re/kWWxUI
     */
    private List<MusicBean> music_list;
    private String song;
    private String url;
    private String artists;
    private String cover_image;

    public String getSong() {
        return song;
    }

    public List<MusicBean> getMusic_list() {
        return music_list;
    }

    public void setMusic_list(List<MusicBean> music_list) {
        this.music_list = music_list;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getArtists() {
        return artists;
    }

    public void setArtists(String artists) {
        this.artists = artists;
    }

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }
}
