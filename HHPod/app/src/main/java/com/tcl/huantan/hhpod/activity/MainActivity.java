package com.tcl.huantan.hhpod.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tcl.huantan.hhpod.R;
import com.tcl.huantan.hhpod.constant.MusicPlayerConstant;
import com.tcl.huantan.hhpod.database.MusicDatabaseManager;
import com.tcl.huantan.hhpod.fragment.MainFragment;
import com.tcl.huantan.hhpod.fragment.PlayFragment;
import com.tcl.huantan.hhpod.menu.SlidingMenu;
import com.tcl.huantan.hhpod.model.MusicInfo;
import com.tcl.huantan.hhpod.service.MusicPlayerService;
import com.tcl.huantan.hhpod.util.FindSongs;
import com.tcl.huantan.hhpod.util.MusicLog;
import com.tcl.huantan.hhpod.util.PermissionUtils;

import java.util.List;

public class MainActivity extends Activity implements MusicPlayerConstant, MediaPlayer.OnCompletionListener, Runnable {

    private static final String TAG = "MainActivity";
    private static final int PERMISSIONS_REQUEST_STORAGE = 1;
    private static final String PACKAGE_URI_PREFIX = "package:";

    public static List<MusicInfo> mMusicList;
    public static MainActivity mainActivity;
    public static MusicPlayerService.MusicBinder mMusicBinder;

    public int mCurrentMusicPlayId;

    private RemoteViews mRemoteViews;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;
    private NotificationReceiver mNotificationReceiver;
    private MusicPlayerService mNewPlayerService;
    private SharedPreferences.Editor mEditor;
    private MusicDatabaseManager mMusicDatabaseManager;

    private long mExitTime;
    private int mPlayModeFlag;
    private boolean isRun = true;

    private SeekBar mSeekBar;
    private ImageButton mPlayAndPauseButton;
    private ImageButton mNextSongButton;
    private ImageButton mPreviousSongButton;
    private ImageButton mPlayModeButton;
    private Button mExitApplicationButton;
    private Button mExitLoginButton;
    private TextView mMusicTitleTextView;
    private TextView mMusicSingerTextView;

    public Thread mSeekBarThread;

    /**
     * when a song is playing and add the song to the recent db table
     */
    public void addDataToRecent() {
        MusicInfo mMusicInfo = mMusicList.get(mCurrentMusicPlayId);
        long time = System.currentTimeMillis();

        if (MusicDatabaseManager.IsExistData(MusicPlayerConstant.DatabaseMsg.TABLE_NAME_RECENTLY, (int) mMusicInfo.getId())) {
            MusicDatabaseManager.updateRecentlyMusicListData(MusicPlayerConstant.DatabaseMsg.TABLE_NAME_RECENTLY, (int) mMusicInfo.getId(), time);
        } else {
            MusicDatabaseManager.addDataToRecentDB(
                    MusicPlayerConstant.DatabaseMsg.TABLE_NAME_RECENTLY,
                    (int) mMusicInfo.getId(),
                    mMusicInfo.getTitle(),
                    mMusicInfo.getArtist(),
                    mMusicInfo.getDuration(),
                    mMusicInfo.getUrl(),
                    time
            );
        }
    }

    /**
     * to ban the back key
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, R.string.back_key, Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                Intent i = new Intent(Intent.ACTION_MAIN);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * @return current music id.
     */
    public int getCurrentMusicId() {
        return mCurrentMusicPlayId;
    }

    /**
     * @return the instance of service
     */
    public MusicPlayerService getService() {
        return mNewPlayerService;
    }

    /**
     * the public method to replace fragments to mainFragment
     */
    public void gotoMainFragment() {
        MainFragment mainFragment = new MainFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
        fragmentTransaction.replace(R.id.fragment_layout, mainFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * MainActivity's bottom click method to playFragment
     *
     * @param v view
     */
    public void main_activity_bottom_layout_listener(View v) {

        String current_music_url = mMusicList.get(mCurrentMusicPlayId).getUrl();
        PlayFragment playFragment = PlayFragment.newInstance(current_music_url);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.slide_in_up, R.animator.slide_out_down);
        fragmentTransaction.replace(R.id.fragment_layout, playFragment);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate executed");
        mainActivity = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        checkPermission();
    }

    private void initViewAndListener(){
        initView();
        initData();
        initClickEvent();
        connectService();
        initMyNotification();
    }

    /**
     * add permission to android M for storage
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {
        // Add M permission start
        Log.d(TAG, "permission: ");
        if (!PermissionUtils.hasStoragePermission(this)) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_STORAGE);
            Log.d(TAG, "checkPermission: " + shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE));
        } else {
            initViewAndListener();
        }
    }

    /**
     * permission deal
     *
     * @param requestCode  requestCode
     * @param permissions  permission
     * @param grantResults grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: ..............");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initViewAndListener();
                } else {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Log.d(TAG, "checkPermission");
                        showMessageOKCancel("You need to allow permission",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == DialogInterface.BUTTON_POSITIVE) {
                                            Log.d(TAG, "checkPermissiononClick: " + which);
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse(PACKAGE_URI_PREFIX + getPackageName()));
                                            startActivity(intent);
                                            if (!PermissionUtils.hasStoragePermission(MainActivity.this)) {
                                                //Log.e(TAG, "should not finish!!!!");
                                                finish();
                                            }
                                        } else {
                                            finish();
                                        }
                                    }
                                });
                    } else {
                        finish();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * show dialog when need permission
     *
     * @param message  message
     * @param listener listener
     */
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", listener)
                .setNegativeButton("Cancel", listener)
                .create()
                .show();
    }

    /**
     * init the view
     */
    public void initView() {
        MainFragment mainFragment = new MainFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_layout, mainFragment).commit();

        mPlayAndPauseButton = (ImageButton) findViewById(R.id.play_button);
        mSeekBar = (SeekBar) findViewById(R.id.process_bar);
        mPlayModeButton = (ImageButton) findViewById(R.id.play_mode_button);
        mMusicTitleTextView = (TextView) findViewById(R.id.music_info_textView);
        mMusicSingerTextView = (TextView) findViewById(R.id.singer_info_textView);
        mPlayAndPauseButton.setImageResource(R.drawable.play_photo);
        mNextSongButton = (ImageButton) findViewById(R.id.next_song_button);
        mPreviousSongButton = (ImageButton) findViewById(R.id.previous_song_button);
        SlidingMenu mMenu = (SlidingMenu) findViewById(R.id.id_menu);
        mExitApplicationButton = (Button) mMenu.findViewById(R.id.exit_application);
        mExitLoginButton = (Button) mMenu.findViewById(R.id.exit_login);
        TextView mMenuUserName = (TextView) mMenu.findViewById(R.id.menu_user_name);

        Intent mIntent = mainActivity.getIntent();
        String mUserInformation = mIntent.getStringExtra(MusicPlayerConstant.PlayerMsg.ACCOUNT);
        mMenuUserName.setText("Hi " + mUserInformation);

        SharedPreferences mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        mPlayModeFlag = mSharedPreferences.getInt(MusicPlayerConstant.PlayerMsg.MUSIC_PLAY_MODE_TAG, 0);
        if (mPlayModeFlag == PlayerMsg.RANDOM_MODE) {
            mPlayModeButton.setImageResource(R.drawable.random_play_mode);
        }
        if (mPlayModeFlag == PlayerMsg.LOOP_MODE) {
            mPlayModeButton.setImageResource(R.drawable.play_mode_photo);
        }
    }

    /**
     * init the data
     */
    public void initData() {
        mCurrentMusicPlayId = 0;
        mExitTime = 0;
        FindSongs mFindSongs = new FindSongs();
        mNotificationReceiver = new NotificationReceiver();
        mMusicList = mFindSongs.getMusicInfos(getContentResolver());
        SharedPreferences mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();
        // init the DB
        mMusicDatabaseManager = new MusicDatabaseManager(this);
    }

    /**
     * init the click event
     */
    public void initClickEvent() {
        mPlayAndPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNewPlayerService.pauseMusic();
                updatePlayButtonForBoth();
                if (mMusicBinder != null && mMusicBinder.getMediaPlayer().isPlaying()) {
                    mSeekBarThread = new Thread(MainActivity.this);
                    mSeekBarThread.start();
                }
            }
        });

        mNextSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMusic(mPlayModeFlag, MusicPlayerConstant.PlayerMsg.NEXT_MUSIC, mMusicList);
                MusicInfo mMusicInfo = mMusicList.get(mCurrentMusicPlayId);
                mNewPlayerService.playMusic(mMusicInfo.getUrl());
                updateUIForChangeMethod();
                updatePlayButtonForBoth();
                mSeekBarThread = new Thread(MainActivity.this);
                mSeekBarThread.start();
            }
        });

        mPreviousSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMusic(mPlayModeFlag, MusicPlayerConstant.PlayerMsg.PREVIOUS_MUSIC, mMusicList);
                MusicInfo mMusicInfo = mMusicList.get(mCurrentMusicPlayId);
                mNewPlayerService.playMusic(mMusicInfo.getUrl());
                updateUIForChangeMethod();
                updatePlayButtonForBoth();
                mSeekBarThread = new Thread(MainActivity.this);
                mSeekBarThread.start();
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBarChangeByUser());

        mPlayModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayModeFlag == PlayerMsg.LOOP_MODE) {
                    mPlayModeFlag = PlayerMsg.RANDOM_MODE;
                    mPlayModeButton.setImageResource(R.drawable.random_play_mode);
                    Toast.makeText(MainActivity.this, R.string.random_play_mode_toast,
                            Toast.LENGTH_SHORT).show();
                    mEditor.putInt(MusicPlayerConstant.PlayerMsg.MUSIC_PLAY_MODE_TAG, PlayerMsg.RANDOM_MODE);
                } else {
                    if (mPlayModeFlag == PlayerMsg.RANDOM_MODE) {
                        mPlayModeFlag = PlayerMsg.LOOP_MODE;
                        mPlayModeButton.setImageResource(R.drawable.play_mode_photo);
                        Toast.makeText(MainActivity.this, R.string.loop_play_mode_toast,
                                Toast.LENGTH_SHORT).show();
                        mEditor.putInt(MusicPlayerConstant.PlayerMsg.MUSIC_PLAY_MODE_TAG, PlayerMsg.LOOP_MODE);
                    }
                }
                mEditor.apply();
            }
        });

        mExitApplicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMusicDatabaseManager.closeDB();
                unregisterReceiver(mNotificationReceiver);
                unbindService(serviceConnection);
                Intent stopServiceIntent = new Intent(MainActivity.this, MusicPlayerService.class);
                stopService(stopServiceIntent);
                mNotificationManager.cancel(0);
                Intent closeIntent = new Intent(Intent.ACTION_MAIN);
                closeIntent.addCategory(Intent.CATEGORY_HOME);
                closeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(closeIntent);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });

        mExitLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.mainActivity);
                dialog.setMessage(R.string.log_out);
                dialog.setCancelable(false);
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        SharedPreferences.Editor editor = LoginActivity.mSharedPreferences.edit();
                        editor.putString(MusicPlayerConstant.PlayerMsg.ACCOUNT, "");
                        editor.putString(LoginActivity.PASSWORD, "");
                        editor.putBoolean(LoginActivity.AUTO_LOGIN, false);
                        editor.apply();

                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        if (mSeekBarThread != null && mSeekBarThread.isAlive()) {
                            mSeekBarThread.interrupt();
                        }
                        isRun = false;
                        mainActivity.finish();
                        Log.d(TAG, "logout");
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    protected void onStart() {
        Log.e(TAG, "onStart executed");
        super.onStart();
    }

    /**
     * when start a song or change a song to call the method
     *
     * @param mode       the music play mode
     * @param msg        the message
     * @param musicInfos musicList's info
     */
    public void changeMusic(int mode, int msg, List<MusicInfo> musicInfos) {
        switch (mode) {
            case MusicPlayerConstant.PlayerMsg.LOOP_MODE:
                switch (msg) {
                    case MusicPlayerConstant.PlayerMsg.NEXT_MUSIC:
                        if (mCurrentMusicPlayId < musicInfos.size() - 1) {
                            mCurrentMusicPlayId++;
                        } else {
                            mCurrentMusicPlayId = 0;
                        }
                        break;
                    case MusicPlayerConstant.PlayerMsg.PREVIOUS_MUSIC:
                        if (mCurrentMusicPlayId >= 1) {
                            mCurrentMusicPlayId--;
                        } else {
                            mCurrentMusicPlayId = musicInfos.size() - 1;
                        }
                        break;
                }
                break;
            case MusicPlayerConstant.PlayerMsg.RANDOM_MODE:
                mCurrentMusicPlayId = getRandom();
                break;
            default:
        }

        try {
            MusicInfo mMusicInfo = mMusicList.get(mCurrentMusicPlayId);
            MusicLog.d(TAG, "the current music is" + mMusicInfo.getTitle());
            mSeekBar.setMax((int) mMusicInfo.getDuration());
            addDataToRecent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * bindService and startService together
     */
    private void connectService() {
        Intent intentService = new Intent(MainActivity.this, MusicPlayerService.class);
        bindService(intentService, serviceConnection, BIND_AUTO_CREATE);
        startService(intentService);
    }

    /**
     * connect to service
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMusicBinder = (MusicPlayerService.MusicBinder) service;
            mNewPlayerService = mMusicBinder.getService();
            mMusicBinder.getMediaPlayer().setOnCompletionListener(MainActivity.this);
            mNotificationReceiver = new NotificationReceiver();
            IntentFilter notificationIntentFilter = new IntentFilter();
            notificationIntentFilter.addAction(NotificationMsg.NOTIFICATION_PREVIOUS_MUSIC);
            notificationIntentFilter.addAction(NotificationMsg.NOTIFICATION_NEXT_MUSIC);
            notificationIntentFilter.addAction(NotificationMsg.NOTIFICATION_PAUSE_MUSIC);
            notificationIntentFilter.addAction(NotificationMsg.NOTIFICATION_EXIT);
            registerReceiver(mNotificationReceiver, notificationIntentFilter);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "Service is disconnected!");
        }
    };

    /**
     * start a thread to update the seekBar
     */
    @Override
    public void run() {
        int currentSeekBarPosition = 0;
        int total;
        while (isRun && mMusicBinder != null && mMusicBinder.getMediaPlayer() != null && mMusicBinder.getMediaPlayer().isPlaying()) {
            total = mMusicBinder.getMediaPlayer().getDuration();
            if (currentSeekBarPosition <= total) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                currentSeekBarPosition = mMusicBinder.getMediaPlayer().getCurrentPosition();
                mSeekBar.setProgress(currentSeekBarPosition);
            }
        }
    }

    /**
     * override the MediaPlayer's OnCompletionListener to monitor when a song has played completely
     *
     * @param mp mediaplayer
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mPlayModeFlag == PlayerMsg.LOOP_MODE) {
            Log.i(TAG, "onCompletion");
            if (++mCurrentMusicPlayId >= mMusicList.size()) {
                mCurrentMusicPlayId = 0;
            }
            MusicInfo mMusicInfo = mMusicList.get(mCurrentMusicPlayId);
            mNewPlayerService.playMusic(mMusicInfo.getUrl());
        }
        if (mPlayModeFlag == PlayerMsg.RANDOM_MODE) {
            mCurrentMusicPlayId = getRandom();
            MusicInfo mMusicInfo = mMusicList.get(mCurrentMusicPlayId);
            mNewPlayerService.playMusic(mMusicInfo.getUrl());
        }
        updateUIForChangeMethod();
    }

    /**
     * the method to get the random music
     *
     * @return the musicList's index
     */
    public int getRandom() {
        int i;
        i = (int) (Math.random() * mMusicList.size());
        return i;
    }

    /**
     * to update UI when click the music from ListView
     *
     * @param infos    the playing music info
     * @param position the position from the List
     */
    public void updateUIForList(List<MusicInfo> infos, int position) {
        mMusicTitleTextView.setText(infos.get(position).getTitle());
        mMusicSingerTextView.setText(infos.get(position).getArtist());
        mSeekBar.setMax((int) infos.get(position).getDuration());
        int BEGIN_PLAY_PROGRESS = 0;
        mSeekBar.setProgress(BEGIN_PLAY_PROGRESS);
        mRemoteViews.setTextViewText(R.id.notification_music_title, infos.get(position).getTitle());
        mRemoteViews.setTextViewText(R.id.notification_music_Artist, infos.get(position).getArtist());
        mNotificationManager.notify(0, mBuilder.build());
    }

    /**
     * to update UI when call method changeMusic
     */
    public void updateUIForChangeMethod() {
        mMusicTitleTextView.setText(mMusicList.get(mCurrentMusicPlayId).getTitle());
        mMusicSingerTextView.setText(mMusicList.get(mCurrentMusicPlayId).getArtist());
        mSeekBar.setMax((int) mMusicList.get(mCurrentMusicPlayId).getDuration());
        int BEGIN_PLAY_PROGRESS = 0;
        mSeekBar.setProgress(BEGIN_PLAY_PROGRESS);
        mRemoteViews.setTextViewText(R.id.notification_music_title, mMusicList.get(mCurrentMusicPlayId).getTitle());
        mRemoteViews.setTextViewText(R.id.notification_music_Artist, mMusicList.get(mCurrentMusicPlayId).getArtist());
        mNotificationManager.notify(0, mBuilder.build());
    }

    /**
     * refresh the play button for activity's playbutton and notification's playbutton
     */
    public void updatePlayButtonForBoth() {
        if (mMusicBinder.getMediaPlayer().isPlaying()) {
            mPlayAndPauseButton.setImageResource(R.drawable.pause_photo);
            mRemoteViews.setImageViewResource(R.id.notification_play_button, R.drawable.play_pause_normal);
        } else {
            mPlayAndPauseButton.setImageResource(R.drawable.play_photo);
            mRemoteViews.setImageViewResource(R.id.notification_play_button, R.drawable.play_normal);
        }
        mNotificationManager.notify(0, mBuilder.build());
    }

    /**
     * init notification
     */
    private void initMyNotification() {
        if (null != mNotificationManager) {
            mNotificationManager.cancelAll();
        }

        mRemoteViews = new RemoteViews(getPackageName(),
                R.layout.notification_layout);
        mRemoteViews.setImageViewResource
                (R.id.notification_artist_image, R.drawable.notification_artist_default_image);

        Intent previousButtonIntent = new Intent(NotificationMsg.NOTIFICATION_PREVIOUS_MUSIC);
        PendingIntent pendPreviousButtonIntent = PendingIntent.getBroadcast(this, 0, previousButtonIntent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.notification_previous_song_button, pendPreviousButtonIntent);

        Intent nextButtonIntent = new Intent(NotificationMsg.NOTIFICATION_NEXT_MUSIC);
        PendingIntent pendNextButtonIntent = PendingIntent.getBroadcast(this, 0, nextButtonIntent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.notification_next_song_button, pendNextButtonIntent);

        Intent playButtonIntent = new Intent(NotificationMsg.NOTIFICATION_PAUSE_MUSIC);
        PendingIntent pendPlayButtonIntent = PendingIntent.getBroadcast(this, 0, playButtonIntent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.notification_play_button, pendPlayButtonIntent);

        Intent exitButton = new Intent(NotificationMsg.NOTIFICATION_EXIT);
        PendingIntent pendingExitButtonIntent = PendingIntent.getBroadcast(this, 0, exitButton, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.notification_exit_button, pendingExitButtonIntent);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(MainActivity.this);
        mBuilder.setContent(mRemoteViews);
        mBuilder.setSmallIcon(R.drawable.notification_artist_default_image);
        mBuilder.setTicker(getResources().getText(R.string.notification_content));
        mBuilder.setContentIntent(contentIntent);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


    }

    @Override
    protected void onDestroy() {
//        mMusicDatabaseManager.closeDB();
        if (null != mNotificationManager) {
            unregisterReceiver(mNotificationReceiver);
            mNotificationManager.cancel(0);
        }
        if (null != serviceConnection) {
            try {
                unbindService(serviceConnection);
            } catch (Exception e) {
                Log.e(TAG, "service is not bind, catch");
            } finally {
                Log.e(TAG, "service is not bind");
            }
        }
        Intent stopServiceIntent = new Intent(MainActivity.this, MusicPlayerService.class);
        stopService(stopServiceIntent);

        Log.d(TAG, "onDestroy executed");
        super.onDestroy();
    }

    /**
     * to implement the SeekBar's click event by user
     */
    public class SeekBarChangeByUser implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                if (mMusicBinder != null) {
                    mMusicBinder.getMediaPlayer().seekTo(progress);
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    /**
     * the notification's broadcast receiver to receive the click event
     */
    private class NotificationReceiver extends BroadcastReceiver {

        private MusicInfo mMusicInfo;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case NotificationMsg.NOTIFICATION_PREVIOUS_MUSIC:
                    changeMusic(mPlayModeFlag, PlayerMsg.PREVIOUS_MUSIC, mMusicList);
                    mMusicInfo = mMusicList.get(mCurrentMusicPlayId);
                    mNewPlayerService.playMusic(mMusicInfo.getUrl());
                    updateUIForChangeMethod();
                    updatePlayButtonForBoth();
                    mSeekBarThread = new Thread(MainActivity.this);
                    mSeekBarThread.start();
                    break;
                case NotificationMsg.NOTIFICATION_PAUSE_MUSIC:
                    mNewPlayerService.pauseMusic();
                    updatePlayButtonForBoth();
                    if (mMusicBinder != null && mMusicBinder.getMediaPlayer().isPlaying()) {
                        mSeekBarThread = new Thread(MainActivity.this);
                        mSeekBarThread.start();
                    }
                    break;
                case NotificationMsg.NOTIFICATION_NEXT_MUSIC:
                    changeMusic(mPlayModeFlag, PlayerMsg.NEXT_MUSIC, mMusicList);
                    mMusicInfo = mMusicList.get(mCurrentMusicPlayId);
                    mNewPlayerService.playMusic(mMusicInfo.getUrl());
                    updateUIForChangeMethod();
                    updatePlayButtonForBoth();
                    mSeekBarThread = new Thread(MainActivity.this);
                    mSeekBarThread.start();
                    break;
                case NotificationMsg.NOTIFICATION_EXIT:
                    mMusicDatabaseManager.closeDB();
                    unregisterReceiver(mNotificationReceiver);
                    unbindService(serviceConnection);
                    Intent stopServiceIntent = new Intent(MainActivity.this, MusicPlayerService.class);
                    stopService(stopServiceIntent);
                    mNotificationManager.cancel(0);
                    Intent closeIntent = new Intent(Intent.ACTION_MAIN);
                    closeIntent.addCategory(Intent.CATEGORY_HOME);
                    closeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(closeIntent);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    break;
                default:
            }
        }
    }
}
