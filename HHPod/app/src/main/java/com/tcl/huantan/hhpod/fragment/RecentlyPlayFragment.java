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
import com.tcl.huantan.hhpod.util.MusicLog;

import java.util.List;

/**
 * Created by huantan on 8/16/16.
 * RecentlyPlayFragment which display recently songs
 */
public class RecentlyPlayFragment extends Fragment {

    private static final String TAG = "RecentlyPlayFragment";

    private MainActivity mainActivity;
    private List<MusicInfo> musicInfos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mRecentlyPlayFragment = inflater.inflate(R.layout.fragment_recently_play, container, false);
        mainActivity = (MainActivity) getActivity();
        musicInfos = MusicDatabaseManager.updateRecentlyListAdapter();
        initialize(mRecentlyPlayFragment);
        return mRecentlyPlayFragment;
    }

    // init the recently fragment
    private void initialize(View mRecentlyPlayFragment) {
        // reuse the musicListAdapter and set the new music info to it
        MusicListAdapter mRecentMusicListAdapter = new MusicListAdapter(mainActivity.getApplicationContext(), musicInfos);
        ListView mRecentListView = (ListView) mRecentlyPlayFragment.findViewById(R.id.recently_play_music_list);
        mRecentListView.setAdapter(mRecentMusicListAdapter);
        // image view which to goto main fragment
        mRecentlyPlayFragment.findViewById(R.id.recent_fragment_to_main_fragment).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mainActivity.gotoMainFragment();
                    }
                });
        // set onclick listener to listview item
        mRecentListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        MusicPlayerService fragmentService = mainActivity.getService();
                        MusicInfo music_Info = musicInfos.get(position);
                        MusicLog.e(TAG, "current title is: " + music_Info.getTitle());
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
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
