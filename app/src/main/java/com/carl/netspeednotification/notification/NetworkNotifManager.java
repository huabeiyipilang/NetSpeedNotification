package com.carl.netspeednotification.notification;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.carl.netspeednotification.App;
import com.carl.netspeednotification.utils.PreferenceUtils;

import java.util.List;

/**
 * Created by carl on 9/19/15.
 */
public class NetworkNotifManager {

    public final static int NOTIF_TYPE_DEFAULT = 1;
    public final static int NOTIF_TYPE_APPS = 2;

    private static NetworkNotifManager ourInstance = new NetworkNotifManager();

    private NotificationService mNotifService;
    private int mNotifType = -1;

    public static NetworkNotifManager getInstance() {
        return ourInstance;
    }

    void bindService(NotificationService service){
        mNotifService = service;
    }

    private NetworkNotifManager() {
    }

    public void showNotification(){
        App.getContext().startService(new Intent(App.getContext(), NotificationService.class));
    }

    public void hideNotification(){
        App.getContext().stopService(new Intent(App.getContext(), NotificationService.class));
    }

    public boolean isServiceRunning(){
        boolean isRunning = false;
        String className = NotificationService.class.getName();
        ActivityManager activityManager = (ActivityManager)
                App.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(30);
        if (!(serviceList.size()>0)) {
            return false;
        }
        for (int i=0; i<serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    public int getNotifType(){
        if (mNotifType == -1){
            mNotifType = PreferenceUtils.getInstance().getDefault().getInt("notification_type", NOTIF_TYPE_DEFAULT);
        }
        return mNotifType;
    }

    public void setNotifType(int type){
        SharedPreferences prefs = PreferenceUtils.getInstance().getDefault();
        mNotifType = type;
        prefs.edit().putInt("notification_type", mNotifType).commit();
        if (mNotifService != null){
            mNotifService.showNotification(mNotifType);
        }
    }
}
