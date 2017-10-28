package com.tcl.huantan.hhpod.constant;

/**
 * Created by huantan on 8/16/16.
 * the Application's static constant
 */
public interface MusicPlayerConstant {
     class PlayerMsg{
         public static final int LOOP_MODE = 0;
         public static final int RANDOM_MODE = 1;
         public static final int PREVIOUS_MUSIC = 3;
         public static final int NEXT_MUSIC = 4;
         public static final String MUSIC_PLAY_MODE_TAG = "music_play_mode";
         public static final String ACCOUNT = "login_account";
    }

     class NotificationMsg{
        public static final String NOTIFICATION_PREVIOUS_MUSIC = "notification_previous";
        public static final String NOTIFICATION_NEXT_MUSIC = "notification_next";
        public static final String NOTIFICATION_PAUSE_MUSIC = "notification_pause";
        public static final String NOTIFICATION_EXIT = "notification_exit";
    }

    class DatabaseMsg{
        public static final String TABLE_NAME_USER = "user_information";
        public final static String TABLE_NAME_RECENTLY = "recently_music_information";
        public final static String TABLE_NAME_FAVORITE = "favorite_music_information";
        public static final String TABLE_USER_NAME = "name";
        public static final String TABLE_PASSWORD = "password";
        public static final String TABLE_EMAIL = "email";
        public static final String TABLE_TEL = "tel";
        public final static String TABLE_INFO_ID = "_id";
        public final static String TABLE_MUSIC_TITLE = "music_title";
        public final static String TABLE_ARTIST = "artist";
        public final static String TABLE_DURATION = "duration";
        public final static String TABLE_MUSIC_URL = "music_url";
        public final static String TABLE_CURRENT_TIME = "current_time";
    }
}
