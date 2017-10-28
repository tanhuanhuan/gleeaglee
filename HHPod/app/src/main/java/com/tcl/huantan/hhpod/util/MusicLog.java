package com.tcl.huantan.hhpod.util;

import android.util.Log;

/**
 * Created by huantan on 9/17/16.
 * create to resolve the problem which the log is too long
 */
public class MusicLog {
    public static int d(String tag, String msg) {
        return Log.d(tag, msg);
    }
    public static int d(String tag, String msg, Throwable tr) {
        return Log.d(tag, msg, tr);
    }

    public static int e(String tag, String msg) {
        return Log.e(tag, msg);
    }
    public static int e(String tag, String msg, Throwable tr) {
        return Log.e(tag, msg, tr);
    }
    public static int i(String tag, String msg) {
        return Log.e(tag, msg);
    }
    public static int i(String tag, String msg, Throwable tr) {
        return Log.i(tag, msg, tr);
    }


}
