package com.carl.netspeednotification;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.IBinder;
import android.view.View;
import android.widget.RemoteViews;

import com.carl.netspeednotification.manager.AppInfo;
import com.carl.netspeednotification.manager.NetworkManager;

import java.util.List;

import cn.kli.utils.Conversion;

public class MainService extends Service implements NetworkManager.AppDataChangeListener, NetworkManager.DataChangeListener{

    private NetworkManager mNetworkManager;
    private Notification mNotification = new Notification();
    private int[] mNotifAppIds = {R.id.iv_app_1, R.id.iv_app_2, R.id.iv_app_3, R.id.iv_app_4};

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mNetworkManager = NetworkManager.getInstance(this);
        initNotification();
        mNetworkManager.addListener(this);
        mNetworkManager.addAppListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void initNotification(){
        mNotification = new Notification();
        mNotification.flags = Notification.FLAG_NO_CLEAR|Notification.FLAG_ONGOING_EVENT;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, new Intent(this, LaunchActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
        mNotification.contentIntent = pendingIntent;
    }

    private void updateDefaultNotification(List<AppInfo> appInfos){
        mNotification.icon = mNetworkManager.getSpeedIcon();
        if(Build.VERSION.SDK_INT >= 11){
            mNotification.largeIcon = Conversion.drawable2Bitmap(getResources().getDrawable(R.drawable.ic_launcher));
        }
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_main);

        mNotification.contentView = contentView;
        startForeground(1001, mNotification);

    }

    private void updateDetailNotification(List<AppInfo> appInfos){
        mNotification.icon = mNetworkManager.getSpeedIcon();
        if(Build.VERSION.SDK_INT >= 11){
            mNotification.largeIcon = Conversion.drawable2Bitmap(getResources().getDrawable(R.drawable.ic_launcher));
        }
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_with_apps);
        contentView.setTextViewText(R.id.tv_speed, NetworkManager.formatSpeed(mNetworkManager.getSpeed()));
        contentView.setTextViewText(R.id.tv_blow, NetworkManager.formatBlow(mNetworkManager.getBlow()));

        int appSize = mNotifAppIds.length;
        int appCount = 0;
        for (int i = 0; i < appSize; i++){
            AppInfo info = appInfos.get(i);
            if (info.getSpeed() > 0){
                appCount++;
                contentView.setViewVisibility(mNotifAppIds[i], View.VISIBLE);
                contentView.setImageViewBitmap(mNotifAppIds[i], info.getIcon());
            }else{
                contentView.setViewVisibility(mNotifAppIds[i], View.GONE);
            }
        }
        contentView.setViewVisibility(R.id.tv_no_app, appCount == 0 ? View.VISIBLE : View.GONE);
        mNotification.contentView = contentView;
        startForeground(1001, mNotification);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        mNetworkManager.removeAppListener(this);
        mNetworkManager.removeListener(this);
    }

    @Override
    public void onAppDataChanged(List<AppInfo> appInfos) {
        updateDetailNotification(appInfos);
    }

    @Override
    public void onDataChanged(float speed, float rxSpeed, float txSpeed) {

    }
}
