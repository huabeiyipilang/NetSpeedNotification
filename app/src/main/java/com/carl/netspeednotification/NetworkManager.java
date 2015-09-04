package com.carl.netspeednotification;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class NetworkManager {

    public final static int SPEED_CACHE_MAX = 20;

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

    private List<AppInfo> mAppInfos;
    private List<DataChangeListener> mDataChangeListeners = new ArrayList<DataChangeListener>();
    private List<AppDataChangeListener> mAppDataChangeListeners = new ArrayList<AppDataChangeListener>();
    private Handler mMainThreadHandler = new Handler();
    private HandlerThread mThread = new HandlerThread("network_speed");
    private NetworkHandler mHandler;
    private LinkedList<Float> mSpeedCache = new LinkedList<Float>();

    private class NetworkHandler extends Handler{

        public NetworkHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE:
                    update();
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE, getFreshRate());
                    break;
            }
        }
    }

    private Comparator<AppInfo> mComparator = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo lhs, AppInfo rhs) {
            return (int)(rhs.getSpeed() - lhs.getSpeed());
        }
    };

    public interface DataChangeListener{
        void onDataChanged(float speed, float rxSpeed, float txSpeed);
    }

    public interface AppDataChangeListener{
        void onAppDataChanged(List<AppInfo> appInfos);
    }

    private NetworkManager(Context context){
        mContext = context;
        mAppInfos = initAppInfos();
        mPref = PreferenceUtils.getInstance().getDefault();
        mThread.start();
        mHandler = new NetworkHandler(mThread.getLooper());
    }

    public static NetworkManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new NetworkManager(context);
        }
        return sInstance;
    }

    public void addListener(DataChangeListener listener){
        mDataChangeListeners.add(listener);
        mHandler.removeMessages(MSG_UPDATE);
        mHandler.sendEmptyMessage(MSG_UPDATE);
    }

    public void removeListener(DataChangeListener listener){
        mDataChangeListeners.remove(listener);
        if (mDataChangeListeners.size() == 0 && mAppDataChangeListeners.size() == 0){
            mHandler.removeMessages(MSG_UPDATE);
        }
    }


    public void addAppListener(AppDataChangeListener listener){
        mAppDataChangeListeners.add(listener);
        mHandler.removeMessages(MSG_UPDATE);
        mHandler.sendEmptyMessage(MSG_UPDATE);
    }

    public void removeAppListener(AppDataChangeListener listener){
        mAppDataChangeListeners.remove(listener);
        if (mDataChangeListeners.size() == 0 && mAppDataChangeListeners.size() == 0){
            mHandler.removeMessages(MSG_UPDATE);
        }
    }

    public void setFreshRate(int rate){
        SharedPreferences prefs = PreferenceUtils.getInstance().getDefault();
        prefs.edit().putInt("fresh_rate", rate).commit();
    }

    public int getFreshRate(){
        return PreferenceUtils.getInstance().getDefault().getInt("fresh_rate", 3000);
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
        if (mDataChangeListeners.size() > 0){
            long newTxBytes = TrafficStats.getTotalTxBytes();
            long newRxBytes = TrafficStats.getTotalRxBytes();
            long newTime = System.currentTimeMillis();

            outputSpeed = 0;
            try {
                outputTxSpeed = (newTxBytes - oldTotalTxBytes)*1000/(newTime - oldTime);
                outputRxSpeed = (newRxBytes - oldTotalRxBytes)*1000/(newTime - oldTime);
                outputSpeed = outputRxSpeed + outputTxSpeed; // b/s

                addSpeedCache(outputSpeed);
            } catch (Exception e) {

            }

            oldTotalRxBytes = newRxBytes;
            oldTotalTxBytes = newTxBytes;
            oldTime = newTime;

            mMainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (DataChangeListener listener : mDataChangeListeners){
                        listener.onDataChanged(outputSpeed, outputRxSpeed, outputTxSpeed);
                    }
                }
            });
        }

        if (mAppDataChangeListeners.size() > 0){
            for (AppInfo info : mAppInfos){
                info.update();
            }

            Collections.sort(mAppInfos, mComparator);

            mMainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (AppDataChangeListener listener : mAppDataChangeListeners){
                        listener.onAppDataChanged(mAppInfos);
                    }
                }
            });
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
        res = formatNumber(speed) + res;
        return res;
    }

    public static String formatBlow(float blow){
        String res = "";
        if(blow < 1000){   // b/s
            res = "B";
        }else if(blow < 1000000){  // kb/s
            blow = blow/1000;
            res = "K";
        }else if(blow < 1000000000){   // mb/s
            blow = blow/1000000;
            res = "M";
        }
        res = formatNumber(blow) + res;
        return res;
    }

    private static String formatNumber(float num){
        DecimalFormat df = new DecimalFormat("0");
        return df.format(num);
    }

    public List<AppInfo> getAppInfos(){
        return mAppInfos;
    }

    public List<Float> getSpeedCache(){
        return mSpeedCache;
    }

    private void addSpeedCache(float speed){
        if (mSpeedCache.size() >= SPEED_CACHE_MAX){
            mSpeedCache.removeLast();
        }
        mSpeedCache.push(speed);
    }

    private List<AppInfo> initAppInfos() {
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
                        appInfo.originRxBlow = TrafficStats.getUidRxBytes(appInfo.uid);
                        appInfo.originTxBlow = TrafficStats.getUidTxBytes(appInfo.uid);
                        uidList.add(appInfo);
                    }
                }
            }
        }
        return uidList;
    }

    public static class AppInfo{
        private String appName;
        private int uid;
        private long oldTotalRxBytes = 0L;
        private long oldTotalTxBytes = 0L;
        private float outputRxSpeed = 0f;
        private float outputTxSpeed = 0f;
        private float originRxBlow = 0f;      //初始下行流量
        private float originTxBlow = 0f;      //初始上行流量
        private float outputRxBlow = 0f;
        private float outputTxBlow = 0f;
        private long oldTime;


        private void update(){
            long newTxBytes = TrafficStats.getUidTxBytes(uid);
            long newRxBytes = TrafficStats.getUidRxBytes(uid);
            long newTime = System.currentTimeMillis();
            try {
                outputTxSpeed = (newTxBytes - oldTotalTxBytes)*1000/(newTime - oldTime);
                outputRxSpeed = (newRxBytes - oldTotalRxBytes)*1000/(newTime - oldTime);
                outputTxBlow = newTxBytes - originTxBlow;
                outputRxBlow = newRxBytes - originRxBlow;
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
            return outputRxSpeed + outputTxSpeed;
        }

        public float getRxSpeed(){
            return outputRxSpeed;
        }

        public float getTxSpeed(){
            return outputTxSpeed;
        }

        public float getBlow(){
            return outputRxBlow + outputTxBlow;
        }

        public float getRxBlow(){
            return outputRxBlow;
        }

        public float getTxBlow(){
            return outputTxBlow;
        }
    }
}
