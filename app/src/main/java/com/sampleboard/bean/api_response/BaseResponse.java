package com.sampleboard.bean.api_response;

/**
 * @author AnujSharma on 1/9/2018.
 */

public class BaseResponse {
    private String message;
    private int code;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
