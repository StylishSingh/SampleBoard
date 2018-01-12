package com.sampleboard.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.text.TextUtils;

import com.sampleboard.api.APIHandler;
import com.sampleboard.bean.api_response.BaseResponse;
import com.sampleboard.bean.api_response.GetProfileResponse;
import com.sampleboard.bean.api_response.UserResponse;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

/**
 * @author AnujSharma on 1/9/2018.
 */

public class EditProfileViewModel extends ViewModel {

    private MutableLiveData<String> messageObserver;
    private MutableLiveData<UserResponse> userResponseObserver;
    private MutableLiveData<Boolean> updateProfileObserver;

    public MutableLiveData<String> getMessageObserver() {
        if (messageObserver == null) messageObserver = new MutableLiveData<>();
        return messageObserver;
    }

    public MutableLiveData<UserResponse> getUserResponseObserver() {
        if (userResponseObserver == null) userResponseObserver = new MutableLiveData<>();
        return userResponseObserver;
    }

    public MutableLiveData<Boolean> getUpdateProfileObserver() {
        if (updateProfileObserver == null) updateProfileObserver = new MutableLiveData<>();
        return updateProfileObserver;
    }

    /**
     * Hit API to get Profile Information
     * @param userId user Id
     */
    public void getProfileInfo(int userId) {
        APIHandler.getInstance().getHandler().getProfile(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<GetProfileResponse>() {
                    @Override
                    public void onSuccess(GetProfileResponse getProfileResponse) {
                        if (getProfileResponse != null) {
                            if (getProfileResponse.getCode() == 1) {
                                getUserResponseObserver().postValue(getProfileResponse.getData());
                            } else {
                                getMessageObserver().postValue(getProfileResponse.getMessage());
                            }
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

    /**
     * Update Profile API Hit
     * @param param contains information to update on server
     */
    public void updateProfile(Map<String, String> param) {
        APIHandler.getInstance().getHandler().updateProfile(param)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse baseResponse) {
                        if (baseResponse != null && baseResponse.getCode() == 1) {
                            getUpdateProfileObserver().postValue(true);
                        } else {
                            getUpdateProfileObserver().postValue(false);
                        }
                        getMessageObserver().postValue(baseResponse.getMessage());
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException) {
                            getMessageObserver().postValue(e.getMessage());
                        }
                    }
                });
    }

    public void validateProfile(UserResponse obj, int userId) {
        if (obj != null) {
            if (obj.getFirst_name().length() == 0) {
                getMessageObserver().postValue("Please enter first name.");
            } else if (obj.getLast_name().length() == 0) {
                getMessageObserver().postValue("Please enter last name.");
            } else {
                Map<String, String> param = new HashMap<>();
                param.put("user_id", String.valueOf(userId));
                param.put("first_name", obj.getFirst_name());
                param.put("last_name", obj.getLast_name());
                param.put("gender", obj.getGender());
                updateProfile(param);
            }
        } else {
            getMessageObserver().postValue("Please enter required fields.");
        }
    }
}
