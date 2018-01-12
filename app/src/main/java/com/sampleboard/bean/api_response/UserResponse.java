package com.sampleboard.bean.api_response;

import android.databinding.ObservableField;

/**
 * @author AnujSharma on 1/9/2018.
 */

public class UserResponse {

    /**
     * uid : 12
     * first_name : sharma
     * last_name : kumar
     * email : sharma@yopmail.com
     * is_active : 1
     * profile_avatar :
     * gender : 1
     */

    private int uid;
    private String first_name;
    private String last_name;
    private String email;
    private int is_active;
    private String profile_avatar;
    private String gender;
    public ObservableField<String> dd=new ObservableField<>();

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getIs_active() {
        return is_active;
    }

    public void setIs_active(int is_active) {
        this.is_active = is_active;
    }

    public String getProfile_avatar() {
        return profile_avatar;
    }

    public void setProfile_avatar(String profile_avatar) {
        this.profile_avatar = profile_avatar;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
