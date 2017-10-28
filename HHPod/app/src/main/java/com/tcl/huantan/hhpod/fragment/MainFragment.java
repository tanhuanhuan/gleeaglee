package com.tcl.huantan.hhpod.fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tcl.huantan.hhpod.R;
import com.tcl.huantan.hhpod.activity.MainActivity;
import com.tcl.huantan.hhpod.constant.MusicPlayerConstant;
import com.tcl.huantan.hhpod.menu.SlidingMenu;
import com.tcl.huantan.hhpod.model.MusicInfo;
import com.tcl.huantan.hhpod.service.MusicPlayerService;
import com.tcl.huantan.hhpod.util.FindSongs;

import java.util.List;

/**
 * Created by huantan on 8/16/16.
 * MainFragment contains link to other fragments
 */
public class MainFragment extends Fragment {
    private MainActivity mainActivity;
    public static List<MusicInfo> musicInfos;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public MainFragment() {
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
        FindSongs finder = new FindSongs();
        musicInfos = finder.getMusicInfos(getContext().getContentResolver());
        final SlidingMenu mMenu = (SlidingMenu) mainActivity.findViewById(R.id.id_menu);
        View rootView = inflater.inflate(R
                .layout.fragment_main, container, false);

        // imageButton which to open menu
        rootView.findViewById(R.id.user_image).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMenu.toggle();
                    }
                });

        // button which to MyMusicFragment
        rootView.findViewById(R.id.myMusicButton).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyMusicFragment myMusicFragment = new MyMusicFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
                        fragmentTransaction.replace(R.id.fragment_layout, myMusicFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });

        // button which play random music
        rootView.findViewById(R.id.randomMusic).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(musicInfos != null){
                        mainActivity.changeMusic(MusicPlayerConstant.PlayerMsg.RANDOM_MODE, MusicPlayerConstant.PlayerMsg.NEXT_MUSIC, musicInfos);
                        MusicPlayerService fragmentService = mainActivity.getService();
                        MusicInfo mMusicInfo = musicInfos.get(mainActivity.mCurrentMusicPlayId);
                        fragmentService.playMusic(mMusicInfo.getUrl());
                        mainActivity.addDataToRecent();
                        // update UI
                        mainActivity.updateUIForChangeMethod();
                        mainActivity.updatePlayButtonForBoth();
                        // start the seekBarThread to update seekBar UI
                        mainActivity.mSeekBarThread = new Thread(mainActivity);
                        mainActivity.mSeekBarThread.start();
                        Toast.makeText(mainActivity, R.string.random_toast, Toast.LENGTH_SHORT).show();
                    }else {
                            Toast.makeText(mainActivity, R.string.random_no_music, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // button which to RecentlyPlayFragment
        rootView.findViewById(R.id.latestPlayListButton).
                setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        RecentlyPlayFragment recentlyPlayFragment = new RecentlyPlayFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
                        fragmentTransaction.replace(R.id.fragment_layout, recentlyPlayFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });

        // button which to MyFavoriteFragment
        rootView.findViewById(R.id.myFavoriteButton).
                setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        MyFavoriteFragment myFavoriteFragment = new MyFavoriteFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
                        fragmentTransaction.replace(R.id.fragment_layout, myFavoriteFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });

        // button which to baidu net to play music
        rootView.findViewById(R.id.netPlayButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://music.baidu.com"));
                startActivity(intent);

            }
        });

        // get the user's info and display
        Intent mIntent = mainActivity.getIntent();
        String mUserInformation = mIntent.getStringExtra(MusicPlayerConstant.PlayerMsg.ACCOUNT);
        TextView mUserTextView = (TextView) rootView.findViewById(R.id.music_user);
        mUserTextView.setText(mUserInformation);

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
