package com.carl.netspeednotification;

import android.content.Context;
import android.content.SharedPreferences;

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

    public static PreferenceUtils getInstance(Context context){
        if (sInstance == null){
            sInstance = new PreferenceUtils(context);
        }
        return sInstance;
    }

    public SharedPreferences getDefault(){
        return mPref;
    }
}
