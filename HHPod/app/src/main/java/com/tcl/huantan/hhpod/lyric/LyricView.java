package com.tcl.huantan.hhpod.lyric;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.tcl.huantan.hhpod.R;
import com.tcl.huantan.hhpod.model.LyricContent;

import java.util.List;


/**
 * Created by huantan on 8/17/16.
 * create to display the lyric
 */
public class LyricView extends TextView{
    private float width;
    private float height;
    private int index = 0;

    private Paint currentPaint;
    private Paint notCurrentPaint;

    private List<LyricContent> myLyricList = null;

    public void setIndex(int index){
        this.index = index;
    }

    public void setMyLyricList(List<LyricContent> lyricList){
        this.myLyricList = lyricList;
    }

    public LyricView(Context context){
        super(context);
        init();
    }

    public LyricView(Context context,AttributeSet attributeSet){
        super(context,attributeSet);
        init();
    }

    public LyricView(Context context,AttributeSet attributeSet,int defSytle){
        super(context,attributeSet,defSytle);
        init();
    }

    private void init(){                            //init drawer
        setFocusable(true);

        currentPaint = new Paint();
        currentPaint.setAntiAlias(true);
        currentPaint.setTextAlign(Paint.Align.CENTER);

        notCurrentPaint = new Paint();
        notCurrentPaint.setAntiAlias(true);
        notCurrentPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(canvas == null){
            return ;
        }

        currentPaint.setColor(getResources().getColor(R.color.black));
        notCurrentPaint.setColor(getResources().getColor(R.color.ivory));

        currentPaint.setTextSize(60);
        currentPaint.setTypeface(Typeface.DEFAULT_BOLD);

        float textSize = 50;
        notCurrentPaint.setTextSize(textSize);
        notCurrentPaint.setTypeface(Typeface.DEFAULT);

        try{
            setText("");

            float tempY = height / 2;
            //draw the lyric before
            float textHeight = 50;
            for(int i = index - 1; i >= 0; i --){
                tempY -= textHeight;
                canvas.drawText(myLyricList.get(i).getLyricString(),width/2,tempY,notCurrentPaint);
            }
            //draw the current lyric
            canvas.drawText(myLyricList.get(index).getLyricString(),width/2,height/2,currentPaint);

            //draw the lyric later
            tempY = height / 2;
            for(int i =index + 1;i<myLyricList.size(); i ++){
                tempY += textHeight;
                canvas.drawText(myLyricList.get(i).getLyricString(),width/2,tempY,notCurrentPaint);
            }

        }
        catch(Exception e){
            setText(R.string.no_lyric);
            setTextSize(18);
            setTextColor(Color.parseColor("#FFFFF0"));
        }
    }

    @Override
    protected void onSizeChanged(int w,int h,int oldW,int oldH){
        super.onSizeChanged(w,h,oldW,oldH);
        this.width = w;
        this.height = h;
    }
}
