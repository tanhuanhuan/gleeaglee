package com.tcl.huantan.hhpod.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

/**
 * Created by huantan on 9/8/16.
 * create to connect to service to play music
 */
public class MusicPlayerService extends Service implements MediaPlayer.OnCompletionListener {
    private static final String TAG = "MusicPlayerService";

    private MediaPlayer mMediaPlayer;
    private MusicBinder mMusicBinder = new MusicBinder();

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return mMusicBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind");
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
        } else {
            mMediaPlayer = new MediaPlayer();
        }
        mMediaPlayer.setOnCompletionListener(this);
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.i(TAG, "onCompletion");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    /**
     * to play music
     * @param mPath the current playing song's url
     */
    public void playMusic(String mPath) {
        if (null == mPath || null == mMediaPlayer) {
            return;
        }
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(mPath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException | IllegalStateException | SecurityException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }


    /**
     * when click the pause button then call the method
     */
    public void pauseMusic() {
        if (mMediaPlayer == null)
            return;
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        } else {
            mMediaPlayer.start();
        }
    }

    /**
     * the inner class to support the method in the Binder
     */
    public class MusicBinder extends Binder {

        public MusicPlayerService getService() {
            return MusicPlayerService.this;
        }

        public MediaPlayer getMediaPlayer() {
            return mMediaPlayer;
        }


    }
}

