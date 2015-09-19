package com.carl.netspeednotification.manager;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.carl.netspeednotification.App;

/**
 * Created by carl on 9/6/15.
 */
public class AppInfo {
    String appName;
    String pkgName;
    int uid;
    long oldTotalRxBytes = 0L;
    long oldTotalTxBytes = 0L;
    float outputRxSpeed = 0f;
    float outputTxSpeed = 0f;
    float originRxBlow = 0f;      //初始下行流量
    float originTxBlow = 0f;      //初始上行流量
    float outputRxBlow = 0f;
    float outputTxBlow = 0f;
    long oldTime;


    void update() {
        long newTxBytes = TrafficStats.getUidTxBytes(uid);
        long newRxBytes = TrafficStats.getUidRxBytes(uid);
        long newTime = System.currentTimeMillis();
        try {
            outputTxSpeed = (newTxBytes - oldTotalTxBytes) * 1000 / (newTime - oldTime);
            outputRxSpeed = (newRxBytes - oldTotalRxBytes) * 1000 / (newTime - oldTime);
            outputTxBlow = newTxBytes - originTxBlow;
            outputRxBlow = newRxBytes - originRxBlow;
        } catch (Exception e) {
            e.printStackTrace();
        }

        oldTotalRxBytes = newRxBytes;
        oldTotalTxBytes = newTxBytes;
        oldTime = newTime;
    }

    public String getAppName() {
        return appName;
    }

    public float getSpeed() {
        return outputRxSpeed + outputTxSpeed;
    }

    public float getRxSpeed() {
        return outputRxSpeed;
    }

    public float getTxSpeed() {
        return outputTxSpeed;
    }

    public float getBlow() {
        return outputRxBlow + outputTxBlow;
    }

    public float getRxBlow() {
        return outputRxBlow;
    }

    public float getTxBlow() {
        return outputTxBlow;
    }

    public Bitmap getIcon(){
        return NetworkManager.getInstance(App.getContext()).getAppIcon(pkgName);
    }

    public void loadIcon(final ImageView imageView){
        if (imageView == null){
            return;
        }

        Bitmap bitmap = getIcon();
        if (bitmap == null){
            imageView.setImageResource(android.R.drawable.sym_def_app_icon);
        }else{
            imageView.setImageBitmap(bitmap);
        }

//        new AsyncTask(){
//
//            @Override
//            protected Object doInBackground(Object[] params) {
//                PackageManager pm = App.getContext().getPackageManager();
//                Drawable icon = null;
//                try {
//                    ApplicationInfo info = pm.getApplicationInfo(pkgName, 0);
//                    icon = info.loadIcon(pm);
//                } catch (PackageManager.NameNotFoundException e) {
//                    icon = App.getContext().getResources().getDrawable(android.R.drawable.sym_def_app_icon);
//                }
//                return icon;
//            }
//
//            @Override
//            protected void onPostExecute(Object o) {
//                super.onPostExecute(o);
//                Drawable icon = (Drawable)o;
//                imageView.setImageDrawable(icon);
//            }
//        }.execute("");
    }
}
