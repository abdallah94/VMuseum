package com.exalt.vmuseum.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

import com.exalt.vmuseum.R;
import com.exalt.vmuseum.ui.activities.DisplayActivity;
import com.exalt.vmuseum.utilities.interfaces.BeaconDetectionCallback;
import com.gimbal.android.BeaconEventListener;
import com.gimbal.android.BeaconManager;
import com.gimbal.android.BeaconSighting;
import com.gimbal.android.CommunicationManager;

import java.io.IOException;

public class AudioService extends Service implements MediaPlayer.OnPreparedListener,
        AudioManager.OnAudioFocusChangeListener {
    private static MediaPlayer mMediaPlayer = null;
    private final IBinder mBinder = new LocalBinder();
    private String url;
    private WifiManager.WifiLock mWifiLock;


    public AudioService() {
    }

    public static void pauseAudio() {
        mMediaPlayer.pause();
    }

    public static void continueAudio() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }

    }

    public static void stopAudio() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public static void setBeaconListeners(final BeaconDetectionCallback callback) {
        BeaconEventListener beaconEventListener = new BeaconEventListener() {
            @Override
            public void onBeaconSighting(BeaconSighting beaconSighting) {
                super.onBeaconSighting(beaconSighting);
                sendBeaconDetails(beaconSighting, callback);
            }
        };
        BeaconManager beaconManager = new BeaconManager();
        beaconManager.addListener(beaconEventListener);
        beaconManager.startListening();
        CommunicationManager.getInstance().startReceivingCommunications();

    }

    private static void sendBeaconDetails(BeaconSighting beaconSighting, BeaconDetectionCallback callback) {


        String BeaconName = beaconSighting.getBeacon().getIdentifier();
        callback.beaconFound(BeaconName);

    }

    @Override
    public void onAudioFocusChange(int i) {
        switch (i) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (mMediaPlayer == null) {
                    prepareMediaPlayer();
                } else if (!mMediaPlayer.isPlaying()) {
                    mMediaPlayer.start();
                }
                mMediaPlayer.setVolume(1.0f, 1.0f);
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
                mMediaPlayer.release();
                mMediaPlayer = null;
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.setVolume(0.1f, 0.1f);
                }
                break;
        }

    }

    public void startAudio(String url) {
        this.url = url;
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        try {
            prepareMediaPlayer();
            mMediaPlayer.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.prepareAsync(); // prepare async to not block main thread
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        getAudioFocus();//request audio focus
        return mBinder;
    }

    private void prepareMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopAudio();
            }
        });
        prepareLocks();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mWifiLock.release();
        stopForeground(true);
        mMediaPlayer.release();
        mMediaPlayer = null;
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        startNotification();
        mMediaPlayer.start();

    }

    //aquire the locks to prevent the system from turning off wifi and cpu
    private void prepareLocks() {
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mWifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))//prevent the system from turning off wifi
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");

        mWifiLock.acquire();
    }

    private void getAudioFocus() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // could not get audio focus.
        }
    }

    //used to display the notification
    private void startNotification() {

        //intent for opening displayActivity when pressing the notification
        Intent showTaskIntent = new Intent(getApplicationContext(), DisplayActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, showTaskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        //this is the intent that is supposed to pause the music player the button is clicked
        Intent pauseIntent = new Intent(getApplicationContext(), PauseButtonListener.class);
        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 0,
                pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Action pauseAction = getAction(pendingSwitchIntent, "pause", R.drawable.pause);//build the action for the pause button

        //this is the intent for the play button
        Intent playIntent = new Intent(getApplicationContext(), PlayButtonListener.class);
        PendingIntent pendingPlayIntent = PendingIntent.getBroadcast(this, 0,
                playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Action playAction = getAction(pendingPlayIntent, "play", R.drawable.play);//build the action for the pause button

        //this is the intent for stoping the audio when closing the notification
        Intent cancelIntent = new Intent(this, CancelNotoficationListener.class);
        PendingIntent pendingCancelIntent = PendingIntent.getBroadcast(this, 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //build the notification
        buildNotification(pauseAction, playAction, pendingCancelIntent, contentIntent);
    }

    private void buildNotification(Notification.Action pauseAction, Notification.Action playAction, PendingIntent pendingCancelIntent, PendingIntent contentIntent) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(getApplicationContext()).setContentTitle(getString(R.string.app_name))
                .setContentText("playing Audio").setSmallIcon(R.drawable.background).setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent).addAction(pauseAction).addAction(playAction).setDeleteIntent(pendingCancelIntent).build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        startForeground(1, notification);
        notificationManager.notify(1, notification);
    }

    private Notification.Action getAction(PendingIntent pendingSwitchIntent, String text, int icon) {
        Notification.Action pauseAction;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            pauseAction = new Notification.Action.Builder(
                    Icon.createWithResource(this, icon), text, pendingSwitchIntent).build();
        } else {
            pauseAction = new Notification.Action.Builder(icon, text, pendingSwitchIntent).build();
        }
        return pauseAction;
    }

    public static class CancelNotoficationListener extends BroadcastReceiver {

        public CancelNotoficationListener() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {

            //stop music here
            stopAudio();
        }
    }

    public static class PauseButtonListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //pause or stop audio here
            pauseAudio();
        }
    }

    public static class PlayButtonListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //continue audio here
            continueAudio();
        }
    }

    public class LocalBinder extends Binder {
        public AudioService getService() {
            // Return this instance of LocalService so clients can call public methods
            return AudioService.this;
        }
    }


}
