package com.library.mvp;

import retrofit2.Call;

/**
 * @author Sachin Narang
 */

public interface ApplicationInterActor extends IBaseInterActor {
    <T> void callable(OnCallableResponse onCallableResponse, Call<T> call, String serviceType);

    interface OnCallableResponse {
        void onError(String error);

        <T> void fromCallable(T response, String serviceType);
    }
}

