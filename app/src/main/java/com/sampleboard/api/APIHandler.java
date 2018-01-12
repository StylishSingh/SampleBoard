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

import com.sampleboard.enums.ApiName;
import com.sampleboard.utils.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Anuj Sharma on 2/17/2016.
 */
public class APIHandler {
    private static APIHandler instance;
    private OkHttpClient client;
    private HttpLoggingInterceptor interceptor;
    private Retrofit retrofit;
    private Call call = null;

    private APICallMethods handler; // Interface where all API methods are geting called

    /*
    Private Constructor in case of Single Instance Class
     */
    private APIHandler(String baseUrl) {
        interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient.Builder().addInterceptor(interceptor)
                .readTimeout(100, TimeUnit.SECONDS)
                .connectTimeout(25, TimeUnit.SECONDS)
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        handler = retrofit.create(APICallMethods.class);
    }

    /*public static APIHandler getInstance(String baseURL) {
        instance = new APIHandler(baseURL);
        return instance;
    }*/

    public static APIHandler getInstance() {
        if (instance == null) {
            synchronized (APIHandler.class) {
                if (instance == null) {
                    instance = new APIHandler(Constants.API_BASE_URL);
                }
            }
        }
        return instance;
    }

    public APICallMethods getHandler() {
        return handler;
    }

    public void getMusicList(final APIResponseInterface listener, final ApiName api_name) {
        call = handler.getMusicList();
        call.enqueue(new retrofit2.Callback() {
            @Override
            public void onResponse(Call call, retrofit2.Response response) {
                listener.onSuccess(response, retrofit, api_name);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                listener.onFailure(t, api_name);
            }
        });
    }


}
