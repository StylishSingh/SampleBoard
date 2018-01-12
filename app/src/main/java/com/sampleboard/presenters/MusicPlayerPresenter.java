package com.sampleboard.presenters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.SeekBar;

import com.sampleboard.R;
import com.sampleboard.bean.MusicBean;
import com.sampleboard.databinding.ActivityMusicplayerBinding;
import com.sampleboard.enums.CurrentScreen;
import com.sampleboard.services.MusicService;
import com.sampleboard.services.NotificationForegroundService;
import com.sampleboard.utils.Constants;
import com.sampleboard.utils.Utils;
import com.sampleboard.view.musicModule.MusicPlayerActivity;
import com.sampleboard.view.fragment.dashboard.HomeFragment;
import com.sampleboard.view.musicModule.MusicListFragment;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import okhttp3.OkHttpClient;

/**
 * Created by Mobilyte India Pvt Ltd on 3/2/2017.
 */

public class MusicPlayerPresenter implements View.OnClickListener {
    private ActivityMusicplayerBinding binder;
    private MusicPlayerActivity activity;

    //get bottom sheet behavior from bottom sheet view
    BottomSheetBehavior bottomSheetBehavior;
    MediaPlayer mediaPlayer;
    Picasso picasso;
    private boolean isSongBuffered;
    private int currentSongPosition = 0;

    //media player and seekbar related variables
    private int current = 0;
    private boolean running = true;
    private int duration = 0;

    private boolean isUpdateFromUI = false;

    public BottomSheetBehavior getBottomSheetBehavior() {
        return (bottomSheetBehavior == null) ? null : bottomSheetBehavior;
    }

    public MusicPlayerPresenter(ActivityMusicplayerBinding binder, MusicPlayerActivity dashBoardActivity) {
        this.binder = binder;
        this.activity = dashBoardActivity;
        init();
    }

    private void init() {
        changeScreen(CurrentScreen.MUSIC_LIST_SCREEN, false, false, null);
//        changeScreen(CurrentScreen.ITEM_LIST_SCREEN,false,false,null);
        if (bottomSheetBehavior == null) {
            bottomSheetBehavior = BottomSheetBehavior.from(binder.playerBottomsheet);
            bottomSheetBehavior.setPeekHeight(0);
        }
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        //hide play pause button
                        TransitionManager.beginDelayedTransition(binder.bottomsheetLayout.sheetRoot, new ChangeBounds());
                        binder.bottomsheetLayout.btnPlayPause.setVisibility(View.INVISIBLE);
                        binder.bottomsheetLayout.btnSeekbar.setVisibility(View.GONE);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        //show play pause button
                        TransitionManager.beginDelayedTransition(binder.bottomsheetLayout.sheetRoot, new ChangeBounds());
                        binder.bottomsheetLayout.btnSeekbar.setVisibility(View.VISIBLE);
                        manageProgress();
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        //set seekbar listener
        binder.bottomsheetLayout.playerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null) {
                    //convert progress according to song length
                    if (fromUser) {
                        mediaPlayer.seekTo(progress);
                    }

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    /**
     * Show or hide Progressbar
     */
    public void showProgress(boolean isShow) {
        if (isShow) binder.layoutProgress.progressParent.setVisibility(View.VISIBLE);
        else binder.layoutProgress.progressParent.setVisibility(View.GONE);
    }

    public static HomeFragment itemsListFragment;

    public HomeFragment initializeItemFragment() {
        if (itemsListFragment == null) itemsListFragment = new HomeFragment();
        return itemsListFragment;
    }

    private static MusicListFragment musicListFragment;

    private MusicListFragment initializeMusicListFragment() {
        if (musicListFragment == null) musicListFragment = new MusicListFragment();
        return musicListFragment;
    }

    public void expandBottomSheet() {
        //to expand the bottom sheet
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public void collapseBottomSheet() {
        //to collapse the bottom sheet
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public void changeScreen(CurrentScreen currentScreen, boolean isAddFragment, boolean isBackStack, Bundle bundle) {
        Fragment currentFragment = null;
        switch (currentScreen) {
            case MUSIC_LIST_SCREEN:
//                currentFragment = initializeMusicListFragment();
                currentFragment = new MusicListFragment();
                break;
            case ITEM_LIST_SCREEN:
                currentFragment = initializeItemFragment();
                break;
        }
        if (currentFragment == null) {
            return;
        }
        //hide Keyboard
        Utils.getInstance().hideSoftKeyboard(activity, binder.getRoot());
        if (currentScreen == CurrentScreen.MUSIC_DETAIL_BOTTOMSHEET) {
            ((BottomSheetDialogFragment) currentFragment).show(activity.getSupportFragmentManager(), "player");
        } else {
            if (isAddFragment) {
                //Add Fragment
                if (isBackStack)
                    activity.addFragmentWithBundle(R.id.musicplayer_contaner, currentFragment, true, bundle);
                else
                    activity.addFragmentWithBundle(R.id.musicplayer_contaner, currentFragment, false, bundle);
            } else {
                //Replace Fragment
                if (isBackStack)
                    activity.navigateToWithBundle(R.id.musicplayer_contaner, currentFragment, true, bundle);
                else
                    activity.navigateToWithBundle(R.id.musicplayer_contaner, currentFragment, false, bundle);
            }
        }

    }

    public void playMusic(MusicBean musicBean, int adapterPosition) {
        //run notification service in foreground
        Intent service = new Intent(activity, NotificationForegroundService.class);
        service.putExtra("title", musicBean.getSong());
        service.putExtra("desc", musicBean.getArtists());
        service.putExtra("image", musicBean.getCover_image());
        service.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        NotificationForegroundService.IS_SERVICE_RUNNING = true;
        activity.startService(service);

        currentSongPosition = adapterPosition;

        if (currentSongPosition == -1) return;

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;

            binder.bottomsheetLayout.btnSeekbar.setProgress(0);
            binder.bottomsheetLayout.playerSeekbar.setProgress(0);
            //remove seekbar progress handler
            binder.bottomsheetLayout.playerSeekbar.removeCallbacks(onEverySecond);
        }

        isSongBuffered = false;     //By default is buffered false
        manageProgress();

        //set related data
        binder.bottomsheetLayout.musicTitle.setText(musicBean.getSong());
        binder.bottomsheetLayout.musicArtist.setText(musicBean.getArtists());
        if (picasso == null) {
            OkHttpClient client = new OkHttpClient();
            picasso = new Picasso.Builder(activity)
                    .downloader(new OkHttp3Downloader(client))
                    .build();
        }
        picasso.load(musicBean.getCover_image()).resize(100, 100).into(binder.bottomsheetLayout.musicIcon);
        picasso.load(musicBean.getCover_image()).into(binder.bottomsheetLayout.coverImage);
        bottomSheetBehavior.setPeekHeight((int) activity.getResources().getDimension(R.dimen.peek_height));

        //start music service
        Intent musicIntent = new Intent(activity.getApplicationContext(), MusicService.class);
        musicIntent.setAction(Constants.ACTION_PLAY);
        musicIntent.putExtra("music_url", musicBean.getUrl());
        activity.startService(musicIntent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.player_top:
                collapseBottomSheet();
                break;
            case R.id.btn_playPause:
            case R.id.player_playPause:
                // set ui toggle
                isUpdateFromUI = true;
                togglePlayPause();
                break;
            case R.id.player_next:
                playnextSong();
                break;
            case R.id.player_previous:
                playPreviousSong();
                break;
        }
    }

    /**
     * @param mp Media player object
     */
    public void setMediaPLayer(MediaPlayer mp) {
        this.mediaPlayer = mp;
        isSongBuffered = true;
        manageProgress();
        mediaPlayer.start();

        //set seekbar info
        binder.bottomsheetLayout.playerSeekbar.setMax(mediaPlayer.getDuration());
        binder.bottomsheetLayout.btnSeekbar.setMax(mediaPlayer.getDuration());
        binder.bottomsheetLayout.playerSeekbar.postDelayed(onEverySecond, 1000);
        //set value to local variables
        duration = mediaPlayer.getDuration();
    }

    /**
     * @param mp Media player object called when media completed
     */
    public void onMediaComplete(MediaPlayer mp) {
        //set all play/pause button to default condition
        if (binder != null) {
            mediaPlayer.pause();
            binder.bottomsheetLayout.playerPlayPause.setImageResource(R.drawable.ic_play);
            binder.bottomsheetLayout.btnPlayPause.setImageResource(R.drawable.ic_play);

            //set seekbar info
            binder.bottomsheetLayout.playerSeekbar.setMax(mediaPlayer.getDuration());
            binder.bottomsheetLayout.btnSeekbar.setMax(mediaPlayer.getDuration());
            //set value to local variables
            duration = mediaPlayer.getDuration();
        }
    }

    /**
     * Toggle music player play pause button
     */
    public void togglePlayPause() {
        if (mediaPlayer != null) {
            Intent service = new Intent(activity, NotificationForegroundService.class);
            if (mediaPlayer.isPlaying()) {
                //pause music
                mediaPlayer.pause();
                binder.bottomsheetLayout.btnPlayPause.setImageResource(R.drawable.ic_play);
                binder.bottomsheetLayout.playerPlayPause.setImageResource(R.drawable.ic_play);
                //if controlled from UI
                service.putExtra("isPlay", false);
            } else {
                //play music
                mediaPlayer.start();
                binder.bottomsheetLayout.btnPlayPause.setImageResource(R.drawable.ic_pause);
                binder.bottomsheetLayout.playerPlayPause.setImageResource(R.drawable.ic_pause);

                if (binder.bottomsheetLayout.playerSeekbar.getProgress() == 0) {
                    binder.bottomsheetLayout.playerSeekbar.postDelayed(onEverySecond, 1000);
                }
                //if controlled from UI
                service.putExtra("isPlay", true);
            }

            if (isUpdateFromUI) {
                //run service to notify play pause
                service.setAction(Constants.MusicAction.PLAY_UPDATE_ACTION);
                activity.startService(service);
                isUpdateFromUI = false;
            }
        }
    }

    /**
     * Play Next song from music list
     */
    public void playnextSong() {
        MusicListPresenter presenter = MusicListFragment.getInstance().getMusicListPresenter();
        if (presenter != null) {
            if (currentSongPosition < presenter.getMusicList().size() - 1) {
                MusicBean obj = presenter.getMusicList().get(currentSongPosition + 1);
                //play next song
                playMusic(obj, currentSongPosition + 1);
            }
        }
    }

    /**
     * Play Previous song from music list
     */
    public void playPreviousSong() {
        MusicListPresenter presenter = MusicListFragment.getInstance().getMusicListPresenter();
        if (presenter != null) {
            if (currentSongPosition != 0) {
                MusicBean obj = presenter.getMusicList().get(currentSongPosition - 1);
                //play next song
                playMusic(obj, currentSongPosition - 1);
            }
        }
    }

    private Runnable onEverySecond = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null) {
                if (true == running) {
                    if (binder.bottomsheetLayout.playerSeekbar != null) {
                        binder.bottomsheetLayout.playerSeekbar.setProgress(mediaPlayer.getCurrentPosition());
                        binder.bottomsheetLayout.btnSeekbar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                    if (mediaPlayer.isPlaying()) {
                        binder.bottomsheetLayout.playerSeekbar.postDelayed(onEverySecond, 1000);
                        updateTime();
                    }
                }
            }
        }
    };

    private void updateTime() {
        current = mediaPlayer.getCurrentPosition();
        System.out.println("duration - " + duration + " current- "
                + current);
        int dSeconds = (int) (duration / 1000) % 60;
        int dMinutes = (int) ((duration / (1000 * 60)) % 60);
        int dHours = (int) ((duration / (1000 * 60 * 60)) % 24);

        binder.bottomsheetLayout.musicTotalDuration.setText(String.format("%02d:%02d", dMinutes, dSeconds));

        int cSeconds = (int) (current / 1000) % 60;
        int cMinutes = (int) ((current / (1000 * 60)) % 60);
        int cHours = (int) ((current / (1000 * 60 * 60)) % 24);

        if (dHours == 0) {
            binder.bottomsheetLayout.musicCurrentDuration.setText(String.format("%02d:%02d", cMinutes, cSeconds));
        } else {
//            binder.bottomsheetLayout.musicCurrentDuration.setText(String.format("%02d:%02d:%02d / %02d:%02d:%02d", cHours, cMinutes, cSeconds, dHours, dMinutes, dSeconds));
        }
    }

    /**
     * Manage progressbar if song buffering
     */
    private void manageProgress() {
        if (!isSongBuffered) {
            //show progressbar
            binder.bottomsheetLayout.playerProgress.setVisibility(View.VISIBLE);
            binder.bottomsheetLayout.btnProgressBar.setVisibility(View.VISIBLE);

            binder.bottomsheetLayout.playerPlayPause.setVisibility(View.INVISIBLE);
            binder.bottomsheetLayout.btnPlayPause.setVisibility(View.INVISIBLE);
        } else {
            //show progressbar
            binder.bottomsheetLayout.playerProgress.setVisibility(View.INVISIBLE);
            binder.bottomsheetLayout.btnProgressBar.setVisibility(View.INVISIBLE);

            binder.bottomsheetLayout.playerPlayPause.setVisibility(View.VISIBLE);
            binder.bottomsheetLayout.btnPlayPause.setVisibility(View.VISIBLE);

            //show play pause icon on basis of playing song
            if (mediaPlayer.isPlaying()) {
                binder.bottomsheetLayout.btnPlayPause.setImageResource(R.drawable.ic_pause);
                binder.bottomsheetLayout.playerPlayPause.setImageResource(R.drawable.ic_pause);
            } else {
                binder.bottomsheetLayout.btnPlayPause.setImageResource(R.drawable.ic_play);
                binder.bottomsheetLayout.playerPlayPause.setImageResource(R.drawable.ic_play);
            }
        }
    }


    /**
     * Music Play Broadcast Receiver
     */
    BroadcastReceiver musicPlayReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.MusicAction.PLAY_NEXT)) {
                playnextSong();
            } else if (intent.getAction().equals(Constants.MusicAction.PLAY_PREVIOUS)) {
                playPreviousSong();
            } else if (intent.getAction().equals(Constants.MusicAction.TOGGLE)) {
                togglePlayPause();
            }
        }
    };


    public void pause() {
        //activity pause
    }

    public void resume() {
        //activity resume
    }

    public void create() {
        //activity create
        //register music player receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.MusicAction.PLAY_NEXT);
        intentFilter.addAction(Constants.MusicAction.PLAY_PREVIOUS);
        intentFilter.addAction(Constants.MusicAction.TOGGLE);
        activity.registerReceiver(musicPlayReceiver, intentFilter);
    }

    public void destroy() {
        //activity destroy
        if (musicPlayReceiver != null)
            activity.unregisterReceiver(musicPlayReceiver);
    }
}
