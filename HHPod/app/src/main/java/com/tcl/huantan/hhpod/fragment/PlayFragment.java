package com.tcl.huantan.hhpod.fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tcl.huantan.hhpod.R;
import com.tcl.huantan.hhpod.activity.MainActivity;
import com.tcl.huantan.hhpod.constant.MusicPlayerConstant;
import com.tcl.huantan.hhpod.database.MusicDatabaseManager;
import com.tcl.huantan.hhpod.model.LyricContent;
import com.tcl.huantan.hhpod.lyric.LyricProgress;
import com.tcl.huantan.hhpod.lyric.LyricView;
import com.tcl.huantan.hhpod.model.MusicInfo;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by huantan on 8/17/16.
 * PlayFragment which when the music is playing
 */
public class PlayFragment extends Fragment{
    private int duration;
    private int index = 0;
    private int currentTime;
    private boolean isFavorite;

    private static String music_url = "";

    private LyricView lyricView;
    private List<LyricContent> lyricContents = new ArrayList<>();
    private MainActivity mainActivity;
    private MainFragment mainFragment;

    // set a runnable thread to run lyric
    Runnable myRunnable = new Runnable(){
        @Override
        public void run() {
            lyricView.setIndex(lyricIndex());
            lyricView.invalidate();
            myHandler.postDelayed(myRunnable, 100);
        }
    };

    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    // receive the current music url from main activity
    public static PlayFragment newInstance(String url) {
        PlayFragment fragment = new PlayFragment();
        Bundle args = new Bundle();
        music_url = url;
        args.putString("url",url);
        fragment.setArguments(args);
        return fragment;
    }

    public PlayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        View rootView = inflater.inflate(R.layout.fragment_play, container, false);
        initialize(rootView);
        return rootView;
    }

    // init the play fragment view
    private void initialize(View v){

        final MusicInfo music_Info = MainActivity.mMusicList.get(mainActivity.getCurrentMusicId());
        music_url = music_Info.getUrl();
        isFavorite = music_Info.getFavorite();
        mainFragment = new MainFragment();
        lyricView = (LyricView)v.findViewById(R.id.lrcShowView);

        (v.findViewById(R.id.dismiss_lyric_button))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.animator.slide_in_up, R.animator.slide_out_down);
                        fragmentTransaction.replace(R.id.fragment_layout, mainFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });

        // judge whether the song is favorite and set the suitable UI
        if(isFavorite){
            (v.findViewById(R.id.my_favorite_button)).setBackgroundResource(R.drawable.img_favourite_selected);
        }
        else{
            (v.findViewById(R.id.my_favorite_button)).setBackgroundResource(R.drawable.img_favourite_normal);
        }
        (v.findViewById(R.id.my_favorite_button))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isFavorite){
                            Toast.makeText(getActivity().getApplicationContext(),R.string.remove_favorite,Toast.LENGTH_SHORT).show();
                            (v.findViewById(R.id.my_favorite_button)).setBackgroundResource(R.drawable.img_favourite_normal);
                            isFavorite = false;
                            MusicDatabaseManager.deleteDataFromFavorite(
                                    (int) music_Info.getId());
                        }
                        else{
                            Toast.makeText(getActivity().getApplicationContext(),R.string.add_favorite,Toast.LENGTH_SHORT).show();
                            (v.findViewById(R.id.my_favorite_button)).setBackgroundResource(R.drawable.img_favourite_selected);
                            isFavorite = true;
                            music_Info.setFavorite(true);

                            // when add a song to favorite and add the song to favorite db
                            MusicDatabaseManager.addDataToFavoriteDB(
                                    MusicPlayerConstant.DatabaseMsg.TABLE_NAME_FAVORITE,
                                    (int) music_Info.getId(),
                                    music_Info.getTitle(),
                                    music_Info.getArtist(),
                                    music_Info.getDuration(),
                                    music_Info.getUrl()
                            );
                        }
                    }
                });

        initLyric(music_url);
        myHandler.post(myRunnable);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    // init lyric
    public void initLyric(String url) {
        LyricProgress lyricProgress = new LyricProgress();
        lyricProgress.readLyric(url);
        lyricContents = lyricProgress.getLyricList();

        try{
            lyricView.setMyLyricList(lyricContents);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    // get the current lyric's index
    public int lyricIndex() {
        int size = lyricContents.size();
        if(MainActivity.mMusicBinder.getMediaPlayer() !=null && MainActivity.mMusicBinder.getMediaPlayer().isPlaying()) {
            currentTime = MainActivity.mMusicBinder.getMediaPlayer().getCurrentPosition();
            duration = MainActivity.mMusicBinder.getMediaPlayer().getDuration();
        }else {
            // nothing to do
        }
        if(currentTime < duration) {
            for (int i = 0; i < size; i++) {
                if (i < size - 1) {
                    if (currentTime < lyricContents.get(i).getLyricTime() && i==0) {
                        index = i;
                        break;
                    }
                    if (currentTime > lyricContents.get(i).getLyricTime()
                            && currentTime < lyricContents.get(i + 1).getLyricTime()) {
                        index = i;
                        break;
                    }
                }
                if (i == size - 1
                        && currentTime > lyricContents.get(i).getLyricTime()) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }
}
