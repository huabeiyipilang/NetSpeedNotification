package com.carl.netspeednotification;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NetworkManager {

    private static NetworkManager sInstance;
    private Context mContext;
    private SharedPreferences mPref;

    private long oldTotalRxBytes = 0L;
    private long oldTotalTxBytes = 0L;
    private long oldTime;

    private float outputSpeed = 0f;
    private float outputRxSpeed = 0f;
    private float outputTxSpeed = 0f;

    private static final int MSG_UPDATE = 1;

    private int mUpdateDuring = 1000;

    private List<AppInfo> mAppInfos;
    private List<DataChangeListener> mDataChangeListeners = new ArrayList<DataChangeListener>();
    private Handler mmHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE:
                    update();
                    mmHandler.sendEmptyMessageDelayed(MSG_UPDATE, mUpdateDuring);
                    break;
            }
        }
    };

    private Comparator<AppInfo> mComparator = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo lhs, AppInfo rhs) {
            return (int)(rhs.getSpeed() - lhs.getSpeed());
        }
    };

    public interface DataChangeListener{
        void onDataChanged(float speed, float rxSpeed, float txSpeed, List<AppInfo> appInfos);
    }

    private NetworkManager(Context context){
        mContext = context;
        mAppInfos = getAppInfos();
        mPref = PreferenceUtils.getInstance(mContext).getDefault();
        mUpdateDuring = getUpdateDuring();
    }

    public static NetworkManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new NetworkManager(context);
        }
        return sInstance;
    }

    public void addListener(DataChangeListener listener){
        boolean start = false;
        if (mDataChangeListeners.size() == 0){
            start = true;
        }
        mDataChangeListeners.add(listener);
        if (start){
            mmHandler.sendEmptyMessage(MSG_UPDATE);
        }
    }

    public void removeListener(DataChangeListener listener){
        mDataChangeListeners.remove(listener);
        if (mDataChangeListeners.size() == 0){
            mmHandler.removeMessages(MSG_UPDATE);
        }
    }

    public void setUpdateDuring(int during){
        mUpdateDuring = during;
    }

    public int getSpeedIcon(){
        float speed = getSpeed();
        int resId = R.drawable.wkb000;
        if(speed < 1000){   // b/s
        }else if(speed < 1000000){  // kb/s
            resId += (int)(speed/1000);
        }else if(speed < 1000000000){   // mb/s
            resId = R.drawable.wmb010 + (int)(speed/100000) - 10;
        }
        return resId;
    }

    private void update(){
        long newTxBytes = TrafficStats.getTotalTxBytes();
        long newRxBytes = TrafficStats.getTotalRxBytes();
        long newTime = System.currentTimeMillis();

        outputSpeed = 0;
        try {
            outputTxSpeed = (newTxBytes - oldTotalTxBytes)*1000/(newTime - oldTime);
            outputRxSpeed = (newRxBytes - oldTotalRxBytes)*1000/(newTime - oldTime);
            outputSpeed = outputRxSpeed + outputTxSpeed; // b/s
        } catch (Exception e) {

        }

        oldTotalRxBytes = newRxBytes;
        oldTotalTxBytes = newTxBytes;
        oldTime = newTime;

        for (AppInfo info : mAppInfos){
            info.update();
        }

        Collections.sort(mAppInfos, mComparator);

        for (DataChangeListener listener : mDataChangeListeners){
            listener.onDataChanged(outputSpeed, outputRxSpeed, outputTxSpeed, mAppInfos);
        }
    }

    public float getSpeed(){
        return outputSpeed;
    }

    public float getRxSpeed(){
        return outputRxSpeed;
    }

    public float getTxSpeed(){
        return outputTxSpeed;
    }

    public static String formatSpeed(float speed){
        String res = "";
        if(speed < 1000){   // b/s
            res = "B/s";
        }else if(speed < 1000000){  // kb/s
            speed = speed/1000;
            res = "K/s";
        }else if(speed < 1000000000){   // mb/s
            speed = speed/1000000;
            res = "M/s";
        }
        DecimalFormat df = new DecimalFormat("0");
        res = df.format(speed) + res;
        return res;
    }

    private List<AppInfo> getAppInfos() {
        List<AppInfo> uidList = new ArrayList<AppInfo>();
        PackageManager pm = mContext.getPackageManager();
        List<PackageInfo> packageinfos = pm
                .getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES
                        | PackageManager.GET_PERMISSIONS);
        for (PackageInfo info : packageinfos) {
            String[] permissions = info.requestedPermissions;
            if (permissions != null && permissions.length > 0) {
                for (String permission : permissions) {
                    if ("android.permission.INTERNET".equals(permission)) {
                        AppInfo appInfo = new AppInfo();
                        appInfo.uid = info.applicationInfo.uid;
                        appInfo.appName = pm.getApplicationLabel(info.applicationInfo).toString();
                        uidList.add(appInfo);
                    }
                }
            }
        }
        return uidList;
    }

    private int getUpdateDuring(){
        int rate = mPref.getInt("fresh_rate", 3000);
        return rate;
    }

    public static class AppInfo{
        private String appName;
        private int uid;
        private long oldTotalRxBytes = 0L;
        private long oldTotalTxBytes = 0L;
        private float outputSpeed = 0f;
        private float outputRxSpeed = 0f;
        private float outputTxSpeed = 0f;
        private long oldTime;


        private void update(){
            long newTxBytes = TrafficStats.getUidTxBytes(uid);
            long newRxBytes = TrafficStats.getUidRxBytes(uid);
            long newTime = System.currentTimeMillis();

            outputSpeed = 0;
            try {
                outputTxSpeed = (newTxBytes - oldTotalTxBytes)*1000/(newTime - oldTime);
                outputRxSpeed = (newRxBytes - oldTotalRxBytes)*1000/(newTime - oldTime);
                outputSpeed = outputRxSpeed + outputTxSpeed; // b/s
            } catch (Exception e) {

            }

            oldTotalRxBytes = newRxBytes;
            oldTotalTxBytes = newTxBytes;
            oldTime = newTime;
        }

        public String getAppName(){
            return appName;
        }

        public float getSpeed(){
            return outputSpeed;
        }

        public float getRxSpeed(){
            return outputRxSpeed;
        }

        public float getTxSpeed(){
            return outputTxSpeed;
        }
    }
}
