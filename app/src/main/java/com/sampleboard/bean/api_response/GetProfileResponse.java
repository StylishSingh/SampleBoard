package com.sampleboard.bean.api_response;

/**
 * @author AnujSharma on 1/9/2018.
 */

public class GetProfileResponse extends BaseResponse {
    private UserResponse data;

    public UserResponse getData() {
        return data;
    }

    public void setData(UserResponse data) {
        this.data = data;
    }
}
