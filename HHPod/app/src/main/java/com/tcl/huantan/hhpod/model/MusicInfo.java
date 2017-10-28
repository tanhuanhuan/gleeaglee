package com.tcl.huantan.hhpod.model;

/**
 * Created by huantan on 8/16/16.
 * get and set for music
 */
public class MusicInfo {
    private int mId;           // the music's id
    private String mTitle;     // the music's title
    private String mArtist;    // the music's artist
    private long mDuration;    // the music's duration
    private String mUrl;       // the music's url
    private boolean isFavorite = false;

    public void setId(int id){
        this.mId = id;
    }

    public long getId(){return this.mId;}

    public void setTitle(String title){
        this.mTitle = title;
    }

    public String getTitle(){return this.mTitle;}

    public void setArtist(String artist){
        this.mArtist = artist;
    }

    public String getArtist(){return this.mArtist;}

    public void setDuration(long duration){this.mDuration = duration;}

    public long getDuration(){return this.mDuration;}

    public void setUrl(String url){this.mUrl = url;}

    public String getUrl(){return this.mUrl;}

    public void setFavorite(boolean favorite){this.isFavorite =favorite;}

    public boolean getFavorite(){return isFavorite;}

}
