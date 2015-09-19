package com.carl.netspeednotification.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.carl.netspeednotification.App;

/**
 * Created by carl on 1/19/15.
 */
public class PreferenceUtils {
    private static PreferenceUtils sInstance;

    private Context mContext;
    private SharedPreferences mPref;

    private PreferenceUtils(Context context){
        mContext = context;
        mPref = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
    }

    public static PreferenceUtils getInstance(){
        if (sInstance == null){
            sInstance = new PreferenceUtils(App.getContext());
        }
        return sInstance;
    }

    public SharedPreferences getDefault(){
        return mPref;
    }
}
