package com.sampleboard.view.musicModule;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.view.Menu;

import com.sampleboard.view.BaseActivity;
import com.sampleboard.R;
import com.sampleboard.databinding.ActivityMusicplayerBinding;
import com.sampleboard.presenters.MusicPlayerPresenter;

/**
 * Created by Anuj Sharma on 3/1/2017.
 */

public class MusicPlayerActivity extends BaseActivity {
    ActivityMusicplayerBinding binder;
    private MusicPlayerPresenter presenter;

    private static MusicPlayerActivity activity;

    public static MusicPlayerActivity getInstance(){
        return activity;
    }

    public MusicPlayerPresenter getDashboardPresenter(){
        return (presenter==null)?null:presenter;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binder = DataBindingUtil.setContentView(this,R.layout.activity_musicplayer);
        activity = MusicPlayerActivity.this;
        if(presenter==null){
            presenter = new MusicPlayerPresenter(binder,this);
            presenter.create();
        }

        //set click listeners for bottomsheet components
        binder.bottomsheetLayout.playerTop.setOnClickListener(presenter);
        binder.bottomsheetLayout.btnPlayPause.setOnClickListener(presenter);
        binder.bottomsheetLayout.playerPlayPause.setOnClickListener(presenter);

        binder.bottomsheetLayout.playerPrevious.setOnClickListener(presenter);
        binder.bottomsheetLayout.playerNext.setOnClickListener(presenter);
    }

    @Override
    public void onBackPressed() {
        if(presenter!=null && presenter.getBottomSheetBehavior()!=null &&
                presenter.getBottomSheetBehavior().getState() == BottomSheetBehavior.STATE_EXPANDED){
            presenter.getBottomSheetBehavior().setState(BottomSheetBehavior.STATE_COLLAPSED);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if(presenter!=null){
            presenter.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if(presenter!=null){
            presenter.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(presenter!=null){
            presenter.resume();
        }
    }
}
