package com.tcl.huantan.hhpod.model;

/**
 * Created by huantan on 8/17/16.
 * get and set for lyric
 */
public class LyricContent {

    private String mLyric;            // the content of the lyric
    private int mLyricCurrentTime;    // the current time of the lyric

    public String getLyricString(){
        return this.mLyric;
    }

    public void setLyricString(String lyricString){
        this.mLyric = lyricString;
    }

    public int getLyricTime(){
        return this.mLyricCurrentTime;
    }

    public void setLyricTime(int lyricTime){
        this.mLyricCurrentTime = lyricTime;
    }
}
