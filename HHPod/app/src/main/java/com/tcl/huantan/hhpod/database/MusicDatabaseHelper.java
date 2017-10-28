package com.tcl.huantan.hhpod.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tcl.huantan.hhpod.constant.MusicPlayerConstant;

/**
 * Created by huantan on 8/16/16.
 * databaseHelper to help to create the database to restore the user's Info
 */
public class MusicDatabaseHelper extends SQLiteOpenHelper {
    // database version
    private static final int DATABASE_VERSION = 1;

    // database name
    private static final String DATABASE_NAME = "musicplayer.db";

    // the static SQL statement to create three tables
    private final static String CREATE_RECENTLY_TABLE = "CREATE TABLE IF NOT EXISTS " +
            MusicPlayerConstant.DatabaseMsg.TABLE_NAME_RECENTLY + " (" +  MusicPlayerConstant.DatabaseMsg.TABLE_INFO_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
            + MusicPlayerConstant.DatabaseMsg.TABLE_MUSIC_TITLE + " STRING," + MusicPlayerConstant.DatabaseMsg.TABLE_ARTIST + " STRING,"
            + MusicPlayerConstant.DatabaseMsg.TABLE_DURATION + " LONG," + MusicPlayerConstant.DatabaseMsg.TABLE_MUSIC_URL + " STRING,"
            + MusicPlayerConstant.DatabaseMsg.TABLE_CURRENT_TIME + " LONG)";

    private final static String CREATE_FAVORITE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            MusicPlayerConstant.DatabaseMsg.TABLE_NAME_FAVORITE + " (" + MusicPlayerConstant.DatabaseMsg.TABLE_INFO_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
            + MusicPlayerConstant.DatabaseMsg.TABLE_MUSIC_TITLE + " STRING," + MusicPlayerConstant.DatabaseMsg.TABLE_ARTIST + " STRING,"
            + MusicPlayerConstant.DatabaseMsg.TABLE_DURATION + " LONG," + MusicPlayerConstant.DatabaseMsg.TABLE_MUSIC_URL + " STRING)";

    private final static String CREATE_USER_TABLE = " CREATE TABLE IF NOT EXISTS " +
            MusicPlayerConstant.DatabaseMsg. TABLE_NAME_USER + " (" + MusicPlayerConstant.DatabaseMsg.TABLE_INFO_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
            + MusicPlayerConstant.DatabaseMsg.TABLE_USER_NAME + " STRING," + MusicPlayerConstant.DatabaseMsg.TABLE_PASSWORD + " STRING,"
            + MusicPlayerConstant.DatabaseMsg.TABLE_EMAIL + " STRING," + MusicPlayerConstant.DatabaseMsg.TABLE_TEL + " STRING)";

    private static final String TAG = "MusicDatabaseHelper";

    /**
     * constructor of the MusicDatabaseHelper
     * @param context context
     */
    public MusicDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.i(TAG, "MusicDatabaseHelper Constructor");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.i(TAG, "MusicDatabaseHelper onCreate");
        // Execute the SQL sentence for creating tables
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_RECENTLY_TABLE);
        db.execSQL(CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "MusicDatabaseHelper onUpgrade");

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        Log.i(TAG, "MusicDatabaseHelper onOpen");
    }

}
