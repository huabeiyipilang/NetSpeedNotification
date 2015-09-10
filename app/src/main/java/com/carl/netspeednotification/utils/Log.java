package com.carl.netspeednotification.utils;

/**
 * Created by carl on 9/10/15.
 */
public class Log {

    private String mTag;

    public Log(String tag){
        mTag = tag;
    }

    public void info(String msg){
        i(mTag, msg);
    }

    public static void i(String tag, String msg){
        android.util.Log.i(tag, msg);
    }
}
