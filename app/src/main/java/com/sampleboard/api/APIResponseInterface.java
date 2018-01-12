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

import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Mobilyte on 2/17/2016.
 */
public interface APIResponseInterface {
    public void onSuccess(Response response, Retrofit retrofit, ApiName api);
    public void onFailure(Throwable t, ApiName api);
}
