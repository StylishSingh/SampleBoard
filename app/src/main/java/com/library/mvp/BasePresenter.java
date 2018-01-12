package com.library.mvp;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.util.ArrayMap;

import com.library.basecontroller.AppBaseCompatActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

/**
 * @author Sachin Narang
 */

abstract class BasePresenter<V extends BaseView, T extends IBaseInterActor> implements IPresenter<V> {

    private WeakReference<V> viewRef;
    private WeakReference<AppBaseCompatActivity> activityRef;
    private WeakReference<Context> contextRef;
    private T interActor;
    /**
     * holds the executing or executed service call instances
     */
    private Map<String, Call> mServiceCallsMap;

    @UiThread
    @Override
    public void attachView(V view, Context context) {
        activityRef = new WeakReference<>((AppBaseCompatActivity) context);
        contextRef = new WeakReference<>(context);
        viewRef = new WeakReference<>(view);
        interActor = createInterActor();
        mServiceCallsMap = new ArrayMap<>();
    }

    /**
     * Get the attached view. You should always call {@link #isViewAttached()} to check if the view
     * is
     * attached to avoid NullPointerExceptions.
     *
     * @return <code>null</code>, if view is not attached, otherwise the concrete view instance
     */
    @UiThread
    public V getView() {
        return viewRef == null ? null : viewRef.get();
    }

    @Override
    public void detachView() {

        if (activityRef != null) {
            activityRef.clear();
            activityRef = null;
        }

        if (viewRef != null) {
            viewRef.clear();
            viewRef = null;
        }

        if (contextRef != null) {
            contextRef.clear();
            contextRef = null;
        }

        if (interActor != null)
            interActor = null;

        if (mServiceCallsMap != null) {
            cancelAllServiceCalls(new ArrayList<>(mServiceCallsMap.values()));
            mServiceCallsMap = null;
        }
    }

    /**
     * Checks if a view is attached to this presenter. You should always call this method before
     * calling {@link #getView()} to get the view instance.
     */
    @UiThread
    protected boolean isViewAttached() {
        return viewRef != null && viewRef.get() != null;
    }

    @Nullable
    public Context getContext() {
        if (contextRef != null)
            return contextRef.get();
        return null;
    }

    protected AppBaseCompatActivity getActivity() {
        if (activityRef != null)
            return activityRef.get();
        return null;
    }

    protected abstract T createInterActor();

    protected T getInterActor() {
        return interActor;
    }

    /**
     * this function will cancel all the service which can have an asynchronous response from server
     */
    private void cancelAllServiceCalls(List<Call> serviceCallList) {
        for (Call call : serviceCallList)
            try {
                call.cancel();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
    }

    /**
     * returns the service call object from service map, you can not override this method.
     *
     * @param key key value of the service call (Basically the url)
     * @return Returns the Generic type if exists otherwise null
     */
    final public Call<V> getServiceCallIfExist(String key) {
        if (mServiceCallsMap != null && mServiceCallsMap.containsKey(key))
            return mServiceCallsMap.get(key).clone();
        else
            return null;
    }

    /**
     * create Call Service and put it in Service Map, you can not override this method.
     *
     * @param call Call Service object
     * @param key  key value of Call Service (Basically URL)
     */
    final public void putServiceCallInServiceMap(Call<V> call, String key) {
        mServiceCallsMap.put(key, call);
    }

    /**
     * checks whether call service exists in service map or not, you can not override this method.
     *
     * @param key key of call service (Basically URL)
     * @return true or false
     */
    final public boolean isServiceCallExist(String key) {
        return mServiceCallsMap.containsKey(key);
    }
}
