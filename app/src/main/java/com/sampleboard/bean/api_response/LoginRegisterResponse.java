package com.sampleboard.bean.api_response;

/**
 * @author AnujSharma on 1/9/2018.
 */

public class LoginRegisterResponse extends BaseResponse{


    /**
     * data : {"uid":11,"first_name":"Anuj","last_name":"sharma","email":"anuj@yopmail.com","is_active":1,"profile_avatar":"/candy_profile/2018.01.08.\\beautiful-evening-wallpaper-1.jpg","gender":"1"}
     */

    private UserResponse data;

    public UserResponse getData() {
        return data;
    }

    public void setData(UserResponse data) {
        this.data = data;
    }

}
