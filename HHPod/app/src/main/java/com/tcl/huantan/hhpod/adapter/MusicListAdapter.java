package com.tcl.huantan.hhpod.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tcl.huantan.hhpod.R;
import com.tcl.huantan.hhpod.model.MusicInfo;
import com.tcl.huantan.hhpod.util.MusicUtil;

import java.util.List;

/**
 * Created by huantan on 8/17/16.
 * MusicListView's item adapter
 */

public class MusicListAdapter extends BaseAdapter {
    private Context context;
    private List<MusicInfo> mMusicInfo;

    /**
     * constructor of the MusicListAdapter
     *
     * @param context    context
     * @param musicInfos the music info
     */
    public MusicListAdapter(Context context, List<MusicInfo> musicInfos) {
        this.context = context;
        this.mMusicInfo = musicInfos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewContainer vc;
        if (convertView == null) {
            vc = new ViewContainer();
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.music_list_item_layout, null);
            vc.mMusicTitle = (TextView) convertView.findViewById(R.id.music_title);
            vc.mMusicArtist = (TextView) convertView.findViewById(R.id.music_Artist);
            vc.mMusicDuration = (TextView) convertView.findViewById(R.id.music_duration);
            convertView.setTag(vc);
        } else {
            vc = (ViewContainer) convertView.getTag();
        }
        MusicInfo musicInfo = mMusicInfo.get(position);
        vc.mMusicTitle.setText(musicInfo.getTitle());
        vc.mMusicArtist.setText(musicInfo.getArtist());
        vc.mMusicDuration.setText(String.valueOf(MusicUtil.formatTime(musicInfo.getDuration())));
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mMusicInfo.size();
    }
}

class ViewContainer {
    public TextView mMusicTitle;
    public TextView mMusicArtist;
    public TextView mMusicDuration;
}
