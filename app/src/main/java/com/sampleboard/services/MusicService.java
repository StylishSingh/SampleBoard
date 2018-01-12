package com.sampleboard.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.sampleboard.presenters.MusicPlayerPresenter;
import com.sampleboard.utils.Constants;
import com.sampleboard.view.musicModule.MusicPlayerActivity;

import java.io.IOException;

/**
 * Created by Anuj Sharma on 3/21/2017.
 */

public class MusicService extends Service implements MediaPlayer.OnPreparedListener,MediaPlayer.OnErrorListener,MediaPlayer.OnCompletionListener {


    MediaPlayer mMediaPlayer = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent==null)return 0;
//        return super.onStartCommand(intent, flags, startId);
        if (intent.getAction().equals(Constants.ACTION_PLAY)) {
            try {
                mMediaPlayer = new MediaPlayer();    // initialize it here
                mMediaPlayer.setDataSource(intent.getStringExtra("music_url").toString());
                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.prepareAsync(); // prepare async to not block main thread
                System.out.println("Music player service initialized");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        //set media player initialized object to bottomsheet

        MusicPlayerPresenter presenter = MusicPlayerActivity.getInstance().getDashboardPresenter();
        if(presenter!=null){
            presenter.setMediaPLayer(mp);
//            Intent intent = new Intent(Constants.BROADCAST_MUSIC_PLAY);
//            sendBroadcast(intent);
        }

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        MusicPlayerPresenter presenter = MusicPlayerActivity.getInstance().getDashboardPresenter();
        if(presenter!=null){
            presenter.onMediaComplete(mp);
//            Intent intent = new Intent(Constants.BROADCAST_MUSIC_PLAY);
//            sendBroadcast(intent);
        }
    }
}
