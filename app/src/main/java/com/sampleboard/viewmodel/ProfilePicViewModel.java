package com.sampleboard.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.sampleboard.api.APIHandler;
import com.sampleboard.bean.api_response.BaseResponse;

import java.io.File;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.HttpException;

/**
 * @author AnujSharma on 1/10/2018.
 */

public class ProfilePicViewModel extends ViewModel {
    MutableLiveData<String> messageObserver;
    MutableLiveData<Boolean> isProfileUpdated;

    public MutableLiveData<String> getMessageObserver() {
        if (messageObserver == null) messageObserver = new MutableLiveData<>();
        return messageObserver;
    }

    public MutableLiveData<Boolean> getIsProfileUpdated() {
        if (isProfileUpdated == null) isProfileUpdated = new MutableLiveData<>();
        return isProfileUpdated;
    }

    public void uploadProfilePic(String imagePath, int userId) {
        File file = new File(imagePath);

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("profile_pic", file.getName(), reqFile);

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(userId));

        APIHandler.getInstance().getHandler().uploadProfilePic(requestBody, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse baseResponse) {
                        if (baseResponse != null) {
                            if (baseResponse.getCode() == 1) {
                                getIsProfileUpdated().postValue(true);
                            } else {
                                getIsProfileUpdated().postValue(false);
                            }
                            getMessageObserver().postValue(baseResponse.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException) {
                            getMessageObserver().postValue(e.getMessage());
                        }
                    }
                });
    }
}
