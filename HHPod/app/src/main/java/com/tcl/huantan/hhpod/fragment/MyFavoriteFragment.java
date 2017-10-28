package com.tcl.huantan.hhpod.fragment;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tcl.huantan.hhpod.R;
import com.tcl.huantan.hhpod.activity.MainActivity;
import com.tcl.huantan.hhpod.adapter.MusicListAdapter;
import com.tcl.huantan.hhpod.database.MusicDatabaseManager;
import com.tcl.huantan.hhpod.model.MusicInfo;
import com.tcl.huantan.hhpod.service.MusicPlayerService;

import java.util.List;

/**
 * Created by huantan on 8/17/16.
 * MyFavoriteFragment which display the favorite songs
 */
public class MyFavoriteFragment extends Fragment {
    private MainActivity mainActivity;
    private List<MusicInfo> musicInfos;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public MyFavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mFavoriteFragmentView = inflater.inflate(R.layout.fragment_my_favorite, container, false);
        musicInfos = MusicDatabaseManager.updateFavoriteListAdapter();
        mainActivity = (MainActivity) getActivity();
        initialize(mFavoriteFragmentView);

        return mFavoriteFragmentView;
    }

    /**
     * init the favorite fragment view
     * @param mFavoriteFragmentView mFavoriteFragmentView
     */

    private void initialize(View mFavoriteFragmentView) {
        // reuse the musicListAdapter and set the new music info to it
        MusicListAdapter mFavoriteMusicListAdapter = new MusicListAdapter(mainActivity.getApplicationContext(), musicInfos);
        ListView mFavoriteListView = (ListView) mFavoriteFragmentView.findViewById(R.id.favorite_music_list);
        mFavoriteListView.setAdapter(mFavoriteMusicListAdapter);

        // image view to goto MainFragment
        mFavoriteFragmentView.findViewById(R.id.favorite_fragment_to_main_fragment)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mainActivity.gotoMainFragment();
                    }
                });

        // set click listener to play music list item
        ((ListView) mFavoriteFragmentView.findViewById(R.id.favorite_music_list)).setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        MusicPlayerService fragmentService = mainActivity.getService();
                        MusicInfo music_Info = musicInfos.get(position);
                        fragmentService.playMusic(music_Info.getUrl());
                        mainActivity.addDataToRecent();
                        // update UI
                        mainActivity.updateUIForList(musicInfos,position);
                        mainActivity.updatePlayButtonForBoth();
                        // start the seekBarThread to update seekBar UI
                        mainActivity.mSeekBarThread = new Thread(mainActivity);
                        mainActivity.mSeekBarThread.start();
                    }
                }
        );
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



}
