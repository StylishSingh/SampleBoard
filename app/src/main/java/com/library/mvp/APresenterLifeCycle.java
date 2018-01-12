package com.library.mvp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * @author Sachin Narang
 */

interface APresenterLifeCycle {

    void onCreate(Bundle savedInstanceState);

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();

    boolean onCreateOptionsMenu(Menu menu);

    boolean onPrepareOptionsMenu(Menu menu);

    boolean onOptionsItemSelected(MenuItem item);
}
