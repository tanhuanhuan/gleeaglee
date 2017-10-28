package com.tcl.huantan.hhpod.fragment;

import android.view.View;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.AlertDialog.Builder;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.content.DialogInterface;

import com.tcl.huantan.hhpod.R;
import com.tcl.huantan.hhpod.activity.MainActivity;
import com.tcl.huantan.hhpod.adapter.MusicListAdapter;
import com.tcl.huantan.hhpod.model.MusicInfo;
import com.tcl.huantan.hhpod.service.MusicPlayerService;
import com.tcl.huantan.hhpod.util.FindSongs;

import java.util.List;

/**
 * Created by huantan on 8/17/16.
 * MyMusicFragment which display the local music songs
 */
public class MyMusicFragment extends Fragment{
    private MainActivity mainActivity;
    private List<MusicInfo> musicInfos;
    private ListView listView;

    private MusicListAdapter musicListAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public MyMusicFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_my_music, container, false);
        mainActivity = (MainActivity) getActivity();
        // from ContentResolver
        FindSongs finder = new FindSongs();
        musicInfos = finder.getMusicInfos(mainActivity.getContentResolver());
        initialize(rootView);
        return rootView;
    }

    // init the main fragment view
    private void initialize(final View rootView){
        // reUse the musicListAdapter and set the new music info to it
        musicListAdapter = new MusicListAdapter(mainActivity.getApplicationContext(),musicInfos);
        listView = (ListView)rootView.findViewById(R.id.music_list);
        listView.setAdapter(musicListAdapter);

        rootView.findViewById(R.id.top_layout_right_ImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.gotoMainFragment();
            }
        });

        ((ListView) rootView.findViewById(R.id.music_list)).setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (musicInfos != null) {
                            MusicPlayerService fragmentService = mainActivity.getService();
                            MusicInfo music_Info = MainActivity.mMusicList.get(position);
                            fragmentService.playMusic(music_Info.getUrl());
                            mainActivity.mCurrentMusicPlayId = position;
                            mainActivity.addDataToRecent();
                            // update UI
                            mainActivity.updateUIForChangeMethod();
                            mainActivity.updatePlayButtonForBoth();
                            // start the seekBarThread to update seekBar UI
                            mainActivity.mSeekBarThread = new Thread(mainActivity);
                            mainActivity.mSeekBarThread.start();
                        }
                    }
                }
        );
        // the long item click listener
        ((ListView) rootView.findViewById(R.id.music_list)).setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {
                String musicTitle = musicInfos.get(position). getTitle();
                AlertDialog.Builder builder = new Builder(getActivity());
                builder.setTitle(R.string.delete_title);
                builder.setMessage(getResources().getString(
                        R.string.confirm_delete)
                        + musicTitle + "?");
                builder.setPositiveButton(
                        R.string.comfirm,
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                removeItem(position);
                            }
                        });
                builder.setNegativeButton(
                        R.string.cancel,
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();

                return false;
            }

        });
    }

    // remove the music from my music listView item
    public void removeItem(int position) {
        getActivity().getContentResolver().delete(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                MediaStore.Audio.Media.TITLE + "=?",
                new String[] {
                        musicInfos.get(position).getTitle()
                });
        musicInfos.remove(position);
        if (musicInfos.size() == 0) {
            Toast.makeText(mainActivity.getApplicationContext(), R.string.no_data,
                    Toast.LENGTH_SHORT).show();
        }
        musicListAdapter.notifyDataSetChanged();
        listView.invalidate();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
