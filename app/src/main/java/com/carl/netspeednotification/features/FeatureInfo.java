package com.carl.netspeednotification.features;

import android.content.SharedPreferences;

import com.carl.netspeednotification.PreferenceUtils;

/**
 * Created by carl on 8/24/15.
 */
public class FeatureInfo {

    public static final int STATE_NO_AUTHORITY = 0;
    public static final int STATE_ENABLE = 1;
    public static final int STATE_DISABLE = 2;

    int fid;
    String fkey;
    String name;
    String summary;
    int prize;

    public int getState(){
        int state = PreferenceUtils.getInstance().getDefault().getInt(fkey, STATE_NO_AUTHORITY);
        return state;
    }

    public void setState(int state){
        SharedPreferences.Editor editor = PreferenceUtils.getInstance().getDefault().edit();
        editor.putInt(fkey, state).apply();
    }

    public String getName(){
        return name;
    }

    public String getSummary(){
        return summary;
    }

    public int getPrize(){
        return prize;
    }

}
