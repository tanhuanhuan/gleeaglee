package com.tcl.huantan.hhpod.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

import com.tcl.huantan.hhpod.R;
import com.tcl.huantan.hhpod.constant.MusicPlayerConstant;
import com.tcl.huantan.hhpod.model.MusicInfo;
import com.tcl.huantan.hhpod.model.User;
import com.tcl.huantan.hhpod.util.MusicUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huantan on 8/16/16.
 * to manage the user's database
 */
public class MusicDatabaseManager {
    private static final String TAG = "MusicDatabaseManager";

    private static SQLiteDatabase mSqLiteDatabase;
    private static Context context;
    public static ListAdapter adapter;

    public MusicDatabaseManager(Context context) {
        Log.i(TAG, "MusicDatabaseManager --> Constructor");
        MusicDatabaseManager.context = context;
        MusicDatabaseManager.adapter = null;
        MusicDatabaseHelper mMusicDatabaseHelperHelper = new MusicDatabaseHelper(context);
        mSqLiteDatabase = mMusicDatabaseHelperHelper.getWritableDatabase();
    }

    /**
     * add users for one time
     *
     * @param users users
     */
    public void addUsers(List<User> users) {
        Log.i(TAG, "MusicDatabaseManager --> add");

        // Adopts the transaction processing, to ensure data integrity
        mSqLiteDatabase.beginTransaction();
        try {
            for (User user : users) {
                mSqLiteDatabase.execSQL("INSERT INTO " + MusicPlayerConstant.DatabaseMsg.TABLE_NAME_USER
                        + " VALUES(null, ?, ?, ?, ?)", new Object[]{
                        user.getName(),
                        user.getPassword(), user.getEmail(), user.getTel()
                });
            }
            // Set up the transaction completed successfully
            mSqLiteDatabase.setTransactionSuccessful();
        } catch (SQLException | IllegalStateException se) {
            // handle the SQLException
            se.printStackTrace();
        } finally {
            mSqLiteDatabase.endTransaction();
        }
    }

    /**
     * add one user for one time
     *
     * @param user user
     */
    public void addUser(User user) {
        Log.i(TAG, "MusicDatabaseManager --> add");

        // Adopts the transaction processing, to ensure data integrity
        mSqLiteDatabase.beginTransaction();
        try {
            mSqLiteDatabase.execSQL("INSERT INTO " +MusicPlayerConstant.DatabaseMsg.TABLE_NAME_USER
                    + " VALUES(null, ?, ?, ?, ?)", new String[]{
                    user.getName(),
                    user.getPassword(), user.getEmail(), user.getTel()
            });

            // Set up the transaction completed successfully
            mSqLiteDatabase.setTransactionSuccessful();
        } catch (SQLException | IllegalStateException se) {
            // handle the SQLException
            se.printStackTrace();
        } finally {
            mSqLiteDatabase.endTransaction();
        }
    }

    /**
     * query user by name
     * @return User
     */
    public Boolean queryByName(String[] name) {
        Log.i(TAG, "MusicDatabaseManager --> queryUserByName");
        Cursor c = mSqLiteDatabase.rawQuery("SELECT * FROM " + MusicPlayerConstant.DatabaseMsg.TABLE_NAME_USER
                        + " where " + MusicPlayerConstant.DatabaseMsg.TABLE_USER_NAME + " = ?",
                name);
        Boolean result = c.moveToFirst();
        c.close();
        return result;
    }

    /**
     * query user by name and password
     * @return Boolean If the query result is null, return false; If the query result is not null, return true
     */
    public Boolean queryByNameAndPassword(String[] queryReuest) {
        Log.i(TAG, "MusicDatabaseManager --> queryUserByName");
        Cursor c = mSqLiteDatabase.rawQuery("SELECT * FROM " +MusicPlayerConstant.DatabaseMsg.TABLE_NAME_USER
                        + " where " + MusicPlayerConstant.DatabaseMsg.TABLE_USER_NAME + " = ? and "+ MusicPlayerConstant.DatabaseMsg.TABLE_PASSWORD + " = ?",
                queryReuest);
        Boolean result = c.moveToFirst();
        c.close();
        return result;
    }

    /**
     * query user by email and password
     * @return Boolean If the query result is null, return false; If the query result is not null, return true
     */
    public Boolean queryByEmailAndPassword(String[] queryReuest) {
        Log.i(TAG, "MusicDatabaseManager --> queryUserByName");
        Cursor c = mSqLiteDatabase.rawQuery("SELECT * FROM " + MusicPlayerConstant.DatabaseMsg.TABLE_NAME_USER
                        + " where " + MusicPlayerConstant.DatabaseMsg.TABLE_EMAIL + " = ? and " + MusicPlayerConstant.DatabaseMsg.TABLE_PASSWORD + " = ?",
                queryReuest);
        Boolean result = c.moveToFirst();
        c.close();
        return result;
    }

    /**
     * query user by phone number and password
     *
     * @return Boolean If the query result is null, return false; If the query result is not null, return true
     */
    public Boolean queryByTelAndPassword(String[] queryReuest) {
        Log.i(TAG, "MusicDatabaseManager --> queryUserByName");
        Cursor c = mSqLiteDatabase.rawQuery("SELECT * FROM " + MusicPlayerConstant.DatabaseMsg.TABLE_NAME_USER
                        + " where "+ MusicPlayerConstant.DatabaseMsg.TABLE_TEL +" = ? and " + MusicPlayerConstant.DatabaseMsg.TABLE_PASSWORD + " = ?",
                queryReuest);
        Boolean result = c.moveToFirst();
        c.close();
        return result;
    }

    /**
     * Judge whether the table is null
     * @return Boolean If the table is null, return false; else, return true
     */
    public Boolean JudgeTables() {
        Log.i(TAG, "MusicDatabaseManager --> JudgeDatabase");
        Cursor c = mSqLiteDatabase.rawQuery("SELECT * FROM " + MusicPlayerConstant.DatabaseMsg.TABLE_NAME_USER
                , null);
        Boolean result = c.moveToFirst();
        c.close();
        return result;
    }

    /**
     * add music data to recently music database
     */
    public static void addDataToRecentDB(String table_name, int music_id, String music_title,
                        String music_artist, long music_duration,
                        String music_url, long current_time) {
        ContentValues cv = new ContentValues();
        cv.put(MusicPlayerConstant.DatabaseMsg.TABLE_INFO_ID, music_id);
        cv.put(MusicPlayerConstant.DatabaseMsg.TABLE_MUSIC_TITLE, music_title);
        cv.put(MusicPlayerConstant.DatabaseMsg.TABLE_ARTIST, music_artist);
        cv.put(MusicPlayerConstant.DatabaseMsg.TABLE_DURATION, MusicUtil.formatTime(music_duration));
        cv.put(MusicPlayerConstant.DatabaseMsg.TABLE_MUSIC_URL, music_url);
        cv.put(MusicPlayerConstant.DatabaseMsg.TABLE_CURRENT_TIME, current_time);

        mSqLiteDatabase.insert(table_name, null, cv);
    }

    /**
     * add music data to favorite music database
     */
    public static void addDataToFavoriteDB(String table_name, int music_id, String music_title,
                               String music_artist, long music_duration,
                               String music_url) {
        ContentValues cv = new ContentValues();
        cv.put(MusicPlayerConstant.DatabaseMsg.TABLE_INFO_ID, music_id);
        cv.put(MusicPlayerConstant.DatabaseMsg.TABLE_MUSIC_TITLE, music_title);
        cv.put(MusicPlayerConstant.DatabaseMsg.TABLE_ARTIST, music_artist);
        cv.put(MusicPlayerConstant.DatabaseMsg.TABLE_DURATION, MusicUtil.formatTime(music_duration));
        cv.put(MusicPlayerConstant.DatabaseMsg.TABLE_MUSIC_URL, music_url);

        mSqLiteDatabase.insert(table_name, null, cv);
    }

    /**
     * update the recently database
     */
    public static void updateRecentlyMusicListData(String table_name, int music_id, long current_time) {

        String str = "UPDATE " + table_name + " SET " + MusicPlayerConstant.DatabaseMsg.TABLE_CURRENT_TIME + "=" + current_time + " WHERE "
                + MusicPlayerConstant.DatabaseMsg.TABLE_INFO_ID + "=" + music_id;

        mSqLiteDatabase.execSQL(str);
    }

    /**
     * judge whether the song is exit in the recently database
     */
    public static boolean IsExistData(String table_name, int music_id) {

        String str = "SELECT * FROM " + table_name + " WHERE " + MusicPlayerConstant.DatabaseMsg.TABLE_INFO_ID + "=" + music_id;

        return mSqLiteDatabase.rawQuery(str, null).getCount() != 0;
    }

    /**
     * update the recently music list's adapter and set the info to the adapter
     * @return recently db's music info
     */
    public static List<MusicInfo> updateRecentlyListAdapter() {
        List<MusicInfo> musicInfos = new ArrayList<>();
        try {
            try (Cursor cur = mSqLiteDatabase.
                    query(MusicPlayerConstant.DatabaseMsg.TABLE_NAME_RECENTLY, new String[]{
                                    MusicPlayerConstant.DatabaseMsg.TABLE_INFO_ID,
                                    MusicPlayerConstant.DatabaseMsg.TABLE_MUSIC_TITLE,
                                    MusicPlayerConstant.DatabaseMsg.TABLE_ARTIST,
                                    MusicPlayerConstant.DatabaseMsg.TABLE_DURATION,
                                    MusicPlayerConstant.DatabaseMsg.TABLE_MUSIC_URL,
                            }
                            , null, null, null, null, null)) {

                if (cur != null && cur.getCount() >= 0) {
                    int flags = 0;
                    adapter = new SimpleCursorAdapter(
                            context,
                            R.layout.music_item_layout,
                            cur,
                            new String[]{
                                    MusicPlayerConstant.DatabaseMsg.TABLE_MUSIC_TITLE,
                                    MusicPlayerConstant.DatabaseMsg.TABLE_ARTIST,
                                    MusicPlayerConstant.DatabaseMsg.TABLE_DURATION},
                            new int[]{R.id.recently_play_music_title,
                                    R.id.recently_play_music_Artist,
                                    R.id.recently_play_music_duration},
                            flags
                    );
                }
                if (cur != null) {
                    while (cur.moveToNext()) {
                        MusicInfo musicInfo = new MusicInfo();
                        musicInfo.setUrl(cur.getString(cur.getColumnIndex(MusicPlayerConstant.DatabaseMsg.TABLE_MUSIC_URL)));
                        musicInfo.setArtist(cur.getString(cur.getColumnIndex(MusicPlayerConstant.DatabaseMsg.TABLE_ARTIST)));
                        String time = cur.getString(cur.getColumnIndex(MusicPlayerConstant.DatabaseMsg.TABLE_DURATION));
                        long Time = changeTime(time);
                        musicInfo.setDuration(Time);
                        musicInfo.setId(cur.getInt(cur.getColumnIndex(MusicPlayerConstant.DatabaseMsg.TABLE_INFO_ID)));
                        musicInfo.setTitle(cur.getString(cur.getColumnIndex(MusicPlayerConstant.DatabaseMsg.TABLE_MUSIC_TITLE)));
                        musicInfos.add(musicInfo);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return musicInfos;
    }

    /**
     *delete the song from the favorite table
     * @param music_id the delete music id
     */
    public static void deleteDataFromFavorite(int music_id) {

        String str = "DELETE FROM " + MusicPlayerConstant.DatabaseMsg.TABLE_NAME_FAVORITE +
                " WHERE " + MusicPlayerConstant.DatabaseMsg.TABLE_INFO_ID + "=" + music_id;
        mSqLiteDatabase.execSQL(str);
        Log.d(TAG, "delete from favoritedatabase successfully!");
    }

    /**
     * update the favorite music list's adapter and set the info to the adapter
     * @return favorite db's music info
     */
    public static List<MusicInfo> updateFavoriteListAdapter() {
        List<MusicInfo> musicInfos = new ArrayList<>();
        try {
            try (Cursor cur = mSqLiteDatabase.
                    query(MusicPlayerConstant.DatabaseMsg.TABLE_NAME_FAVORITE, new String[]{
                                    MusicPlayerConstant.DatabaseMsg.TABLE_INFO_ID,
                                    MusicPlayerConstant.DatabaseMsg.TABLE_MUSIC_TITLE,
                                    MusicPlayerConstant.DatabaseMsg.TABLE_ARTIST,
                                    MusicPlayerConstant.DatabaseMsg.TABLE_DURATION,
                                    MusicPlayerConstant.DatabaseMsg.TABLE_MUSIC_URL,
                            }
                            , null, null, null, null, null)) {

                if (cur != null && cur.getCount() >= 0) {
                    int flags = 0;
                    adapter = new SimpleCursorAdapter(
                            context,
                            R.layout.music_item_layout,
                            cur,
                            new String[]{
                                    MusicPlayerConstant.DatabaseMsg.TABLE_MUSIC_TITLE,
                                    MusicPlayerConstant.DatabaseMsg.TABLE_ARTIST,
                                    MusicPlayerConstant.DatabaseMsg.TABLE_DURATION},
                            new int[]{R.id.recently_play_music_title,
                                    R.id.recently_play_music_Artist,
                                    R.id.recently_play_music_duration},
                            flags
                    );
                }
                if (cur != null) {
                    while (cur.moveToNext()) {
                        MusicInfo mp3Info = new MusicInfo();
                        mp3Info.setUrl(cur.getString(cur.getColumnIndex(MusicPlayerConstant.DatabaseMsg.TABLE_MUSIC_URL)));
                        mp3Info.setArtist(cur.getString(cur.getColumnIndex(MusicPlayerConstant.DatabaseMsg.TABLE_ARTIST)));
                        String time = cur.getString(cur.getColumnIndex(MusicPlayerConstant.DatabaseMsg.TABLE_DURATION));
                        long Time = changeTime(time);
                        mp3Info.setDuration(Time);
                        mp3Info.setId(cur.getInt(cur.getColumnIndex(MusicPlayerConstant.DatabaseMsg.TABLE_INFO_ID)));
                        mp3Info.setTitle(cur.getString(cur.getColumnIndex(MusicPlayerConstant.DatabaseMsg.TABLE_MUSIC_TITLE)));
                        musicInfos.add(mp3Info);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return musicInfos;
    }

    /**
     * formate music duration
     */
    private static long changeTime(String time) {
        long Time;
        String[] str = time.split(":");
        Time = (Integer.valueOf(str[0]) * 60 + Integer.valueOf(str[1])) * 1000;
        return Time;
    }

    /**
     * close database
     */
    public void closeDB() {
        Log.i(TAG, "MusicDatabaseManager --> closeDB");
        mSqLiteDatabase.close();
    }
}
