package com.sampleboard.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.sampleboard.R;
import com.sampleboard.utils.Constants;
import com.sampleboard.view.musicModule.MusicPlayerActivity;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import okhttp3.OkHttpClient;

/**
 * Created by Anuj Sharma on 3/27/2017.
 */

public class NotificationForegroundService extends Service {
    private static final String LOG_TAG = "ForegroundService";
    public static boolean IS_SERVICE_RUNNING = false;
    public static boolean IS_SONG_RUNNING = false;

    private Context context;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = this;
        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Start Foreground Intent ");
            showCustomNotification(intent.getStringExtra("title"), intent.getStringExtra("desc"), intent.getStringExtra("image"));

        } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
            Log.i(LOG_TAG, "Clicked Previous");
            Intent prevIntent = new Intent(Constants.MusicAction.PLAY_PREVIOUS);
            sendBroadcast(prevIntent);
        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
            Log.i(LOG_TAG, "Clicked Play");
            if (IS_SONG_RUNNING) {
                //show pause icon
                customNotificationView.setImageViewResource(R.id.notification_playpause, R.drawable.ic_pause);
                IS_SONG_RUNNING = false;
            } else {
                //show play icon
                customNotificationView.setImageViewResource(R.id.notification_playpause, R.drawable.ic_play);
                IS_SONG_RUNNING = true;
            }
            if (mNotificationManager != null) {
                mNotificationManager.notify(
                        Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                        notification);
            }

            Intent prevIntent = new Intent(Constants.MusicAction.TOGGLE);
            sendBroadcast(prevIntent);
        } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
            Log.i(LOG_TAG, "Clicked Next");
            Intent prevIntent = new Intent(Constants.MusicAction.PLAY_NEXT);
            sendBroadcast(prevIntent);
        } else if (intent.getAction().equals(Constants.MusicAction.PLAY_UPDATE_ACTION)) {
            //update
            Toast.makeText(context, "update from ui", Toast.LENGTH_SHORT).show();
            boolean isPlay = intent.getBooleanExtra("isPlay", false);
            if (isPlay) {
                customNotificationView.setImageViewResource(R.id.notification_playpause, R.drawable.ic_pause);
            } else {
                customNotificationView.setImageViewResource(R.id.notification_playpause, R.drawable.ic_play);
            }
            if (mNotificationManager != null) {
                mNotificationManager.notify(
                        Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                        notification);
            }
        } else if (intent.getAction().equals(
                Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Stop Foreground Intent");
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    RemoteViews customNotificationView;
    Notification notification;
    Picasso picasso;
    NotificationManager mNotificationManager;

    private void showCustomNotification(String title, String desc, final String imagePath) {
//set values
        customNotificationView = new RemoteViews(getPackageName(),
                R.layout.view_notification);
        customNotificationView.setTextViewText(R.id.notification_title, title);
        customNotificationView.setTextViewText(R.id.notification_desc, desc);
//        if(picasso==null){
//        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    /*picasso.with(context).load("https://fb-s-a-a.akamaihd.net/h-ak-xtf1/v/t1.0-1/p320x320/14364683_10209036612425074_3269215244539804881_n.jpg?oh=28ce2120dc3fcf8d4ccf6ab7de9c02c3&oe=596C57A3&__gda__=1499803270_fa8842c2a869887e82768dddf45f415a" +
                            "").resize(100,100).centerCrop().into(customNotificationView, R.id.notification_icon,
                            Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);*/

                    /*OkHttpClient client = new OkHttpClient();
                    Picasso picasso = new Picasso.Builder(context)
                            .downloader(new OkHttp3Downloader(client))
                            .build();
                    picasso.with(context.getApplicationContext()).load(imagePath).resize(100, 100).centerCrop().into(customNotificationView, R.id.notification_icon,
                            Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
                    StatsSnapshot picassoStats = Picasso.with(context).getSnapshot();
                    Log.d("Picasso Stats", picassoStats.toString());*/

                    OkHttpClient client = new OkHttpClient();
                    Picasso.Builder builder = new Picasso.Builder(context.getApplicationContext());
                    builder.listener(new Picasso.Listener() {
                        @Override
                        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                            exception.printStackTrace();
                        }
                    });
                    builder.downloader(new OkHttp3Downloader(client));
                    builder.build().load(imagePath).into(customNotificationView, R.id.notification_icon,
                            Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //set click events
        Intent notificationIntent = new Intent(this, MusicPlayerActivity.class);
        /*notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);*/
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent previousIntent = new Intent(this, NotificationForegroundService.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        Intent playIntent = new Intent(this, NotificationForegroundService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent nextIntent = new Intent(this, NotificationForegroundService.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_cloud_download);

        customNotificationView.setOnClickPendingIntent(R.id.notification_playpause, pplayIntent);
        customNotificationView.setOnClickPendingIntent(R.id.notification_previous, ppreviousIntent);
        customNotificationView.setOnClickPendingIntent(R.id.notification_next, pnextIntent);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notification = new NotificationCompat.Builder(this)
                .setContentTitle("Music Player")
                .setTicker("Music Player")
                .setContentText("My song")
                .setCustomContentView(customNotificationView)
                .setCustomBigContentView(customNotificationView)
                .setSmallIcon(getNotificationIcon())
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
//                .addAction(R.drawable.ic_previous, null,
//                        ppreviousIntent)
//                .addAction(R.drawable.ic_pause, null,
//                        pplayIntent)
//                .addAction(R.drawable.ic_next, null,
//                        pnextIntent).build();
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                notification);


    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_cloud_download : R.mipmap.ic_launcher;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "In onDestroy");
        Toast.makeText(this, "Service Detroyed!", Toast.LENGTH_SHORT).show();
    }
}
