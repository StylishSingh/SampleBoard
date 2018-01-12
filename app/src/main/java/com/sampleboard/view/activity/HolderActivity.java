package com.sampleboard.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sampleboard.R;
import com.sampleboard.enums.CurrentScreen;
import com.sampleboard.utils.Constants;
import com.sampleboard.view.BaseActivity;

/**
 * author by Anuj Sharma on 12/13/2017.
 */

public class HolderActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holder);

        if (getIntent() != null && getIntent().getStringExtra(Constants.DESTINATION) != null) {
            if (getIntent().getStringExtra(Constants.DESTINATION).equals(Constants.SEARCH_SCREEN)) {
                // Move to Post Detail screen
                changeScreen(R.id.container_holder, CurrentScreen.SEARCH_SCREEN, false, false, null);
            }
            /*if (getIntent().getStringExtra(Constants.DESTINATION).equals(Constants.DETAIL_SCREEN)) {
                // Move to Post Detail screen
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.OBJ_DETAIL, getIntent().getParcelableExtra(Constants.OBJ_DETAIL));
                changeScreen(R.id.container_holder, CurrentScreen.POST_DETAIL_SCREEN, false, false, bundle);
            }*/
        }
    }
    @Override
    public void onBackPressed() {
        oneStepBack();
    }
}
