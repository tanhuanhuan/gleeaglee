package com.tcl.huantan.hhpod.util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;

import com.tcl.huantan.hhpod.model.MusicInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huantan on 8/17/16.
 * find local songs by contentResolver
 */
public class FindSongs {
    public List<MusicInfo> getMusicInfos(ContentResolver contentResolver) {
        List<MusicInfo> mMusicInfos;
        try (Cursor cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER)) {
            mMusicInfos = new ArrayList<>();
            if (cursor != null) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    MusicInfo mMusicInfo = new MusicInfo();
                    cursor.moveToNext();
                    int id = cursor.getInt(cursor
                            .getColumnIndex(MediaStore.Audio.Media._ID));

                    String title = cursor.getString((cursor
                            .getColumnIndex(MediaStore.Audio.Media.TITLE)));

                    String artist = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.ARTIST));

                    long duration = cursor.getLong(cursor
                            .getColumnIndex(MediaStore.Audio.Media.DURATION));

                    String url = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.DATA));

                    int isMusic = cursor.getInt(cursor
                            .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));

                    if (isMusic != 0 && duration / (1000 * 60) >= 1) {
                        mMusicInfo.setId(id);
                        mMusicInfo.setTitle(title);
                        mMusicInfo.setArtist(artist);
                        mMusicInfo.setDuration(duration);
                        mMusicInfo.setUrl(url);
                        mMusicInfos.add(mMusicInfo);
                    }
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return mMusicInfos;
    }
}
