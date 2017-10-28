package com.tcl.huantan.hhpod.lyric;

import com.tcl.huantan.hhpod.R;
import com.tcl.huantan.hhpod.model.LyricContent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huantan on 8/17/16.
 * create to implement the lyric progress
 */
public class LyricProgress {
    private List<LyricContent> lyricList;
    private LyricContent myLyricContent;

    public LyricProgress(){
        myLyricContent = new LyricContent();
        lyricList = new ArrayList<>();
    }

    public String readLyric(String path){
        StringBuilder stringBuilder = new StringBuilder();
        path = path.replace("song","lyric");
        File f = new File(path.replace(".mp3",".trc"));

        try{
            FileInputStream fis = new FileInputStream(f);
            InputStreamReader isr = new InputStreamReader(fis,"utf-8");
            BufferedReader br = new BufferedReader(isr);
            String s;

            //separator of every lyric sentence
            while((s = br.readLine()) != null){
                s = s.replace("[","");
                s = s.replace("]","@");

                //remove every word's time tag
                s = s.replaceAll("<[0-9]{3,5}>","");


                String spiltLrcData[] = s.split("@");

                if(spiltLrcData.length > 1){

                    //create the instance of every sentence,assignment time to the corresponding lyric
                    myLyricContent.setLyricString(spiltLrcData[1]);
                    int lycTime = time2Str(spiltLrcData[0]);
                    myLyricContent.setLyricTime(lycTime);
                    lyricList.add(myLyricContent);

                    myLyricContent = new LyricContent();
                }
            }
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
            stringBuilder.append(R.string.no_lyric);
        }
        catch(IOException e){
            e.printStackTrace();
            stringBuilder.append(R.string.lyric_exception);
        }
        return stringBuilder.toString();
    }

    public int time2Str(String timeStr){
        timeStr = timeStr.replace(":",".");
        timeStr = timeStr.replace(".","@");

        String timeData[] = timeStr.split("@");

        int min = Integer.parseInt(timeData[0]);
        int sec = Integer.parseInt(timeData[1]);
        int millSec = Integer.parseInt(timeData[2]);

        return (min * 60 + sec) * 1000 + millSec * 10;
    }

    public List<LyricContent> getLyricList(){
        return this.lyricList;
    }
}
