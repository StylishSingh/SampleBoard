/*
 *
 *
 *  * Copyright © 2016, Mobilyte Inc. and/or its affiliates. All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  *
 *  * - Redistributions of source code must retain the above copyright
 *  *    notice, this list of conditions and the following disclaimer.
 *  *
 *  * - Redistributions in binary form must reproduce the above copyright
 *  * notice, this list of conditions and the following disclaimer in the
 *  * documentation and/or other materials provided with the distribution.
 *
 * /
 */
package com.sampleboard.api;


import com.sampleboard.bean.MusicBean;
import com.sampleboard.bean.api_response.BaseResponse;
import com.sampleboard.bean.api_response.GetProfileResponse;
import com.sampleboard.bean.api_response.LoginRegisterResponse;
import com.sampleboard.utils.Constants;

import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by Mobilyte on 2/17/2016.
 */
public interface APICallMethods {

    @GET(Constants.MUSIC_API)
    Call<List<MusicBean>> getMusicList();

    @FormUrlEncoded
    @POST(Constants.LOGIN_API)
    Single<LoginRegisterResponse> login(@FieldMap Map<String, String> param);

    @FormUrlEncoded
    @POST(Constants.REGISTER_API)
    Single<LoginRegisterResponse> register(@FieldMap Map<String, String> param);

    @GET(Constants.GET_PROFILE_API)
    Single<GetProfileResponse> getProfile(@Query("user_id") int userId);

    @FormUrlEncoded
    @POST(Constants.UPDATE_PROFILE_API)
    Single<BaseResponse> updateProfile(@FieldMap Map<String, String> param);

    @Multipart
    @POST(Constants.UPLOAD_PROFILE_PIC_API)
    Single<BaseResponse> uploadProfilePic(@Part("user_id") RequestBody userId, @Part MultipartBody.Part profile_pic);

    /*
    @FormUrlEncoded
    @POST(ConstantFile.CHECK_IS_DRIVER)
    Call<CheckIsDriverResponse> checkIsDriver(@FieldMap Map<String, String> options);

    @Multipart
    @POST(ConstantFile.BECOME_A_DRIVER)
    Call<BasicResponse>becomeDriver(@PartMap Map<String, RequestBody> options, @Part MultipartBody.Part profile_pic
            , @Part MultipartBody.Part license_pic, @Part MultipartBody.Part insurance_pic);*/

}
