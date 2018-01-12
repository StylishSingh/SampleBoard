package com.library.mvp;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * @author Sachin Narang
 */

abstract public class ActivityPresenter<V extends BaseView, T extends IBaseInterActor> extends BasePresenter<V, T> implements APresenterLifeCycle {

    public ActivityPresenter(V v, Context context) {
        attachView(v, context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {
        detachView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }
}
