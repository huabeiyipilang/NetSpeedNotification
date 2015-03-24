package com.carl.netspeednotification;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;

import java.util.List;

import cn.kli.utils.Conversion;

public class MainService extends Service implements NetworkManager.AppDataChangeListener, NetworkManager.DataChangeListener{

    private NetworkManager mNetworkManager;
    private Notification mNotification = new Notification();

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

    private void updateNotification(List<NetworkManager.AppInfo> appInfos){
        mNotification.icon = mNetworkManager.getSpeedIcon();
        if(Build.VERSION.SDK_INT >= 11){
            mNotification.largeIcon = Conversion.drawable2Bitmap(getResources().getDrawable(R.drawable.ic_launcher));
        }
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_main);
        contentView.setTextViewText(R.id.tv_tx_speed, "上行："+NetworkManager.formatSpeed(mNetworkManager.getTxSpeed()));
        contentView.setTextViewText(R.id.tv_rx_speed, "下行："+NetworkManager.formatSpeed(mNetworkManager.getRxSpeed()));

        if (appInfos != null && appInfos.size() > 1){
            NetworkManager.AppInfo info = appInfos.get(0);
            contentView.setTextViewText(R.id.tv_app1, info.getAppName());
            contentView.setTextViewText(R.id.tv_speed1, NetworkManager.formatSpeed(info.getSpeed()));
            info = appInfos.get(1);
            contentView.setTextViewText(R.id.tv_app2, info.getAppName());
            contentView.setTextViewText(R.id.tv_speed2, NetworkManager.formatSpeed(info.getSpeed()));
        }else{
            contentView.setTextViewText(R.id.tv_app1, "");
            contentView.setTextViewText(R.id.tv_speed1, "");
            contentView.setTextViewText(R.id.tv_app2, "");
            contentView.setTextViewText(R.id.tv_speed2, "");
        }
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
    public void onAppDataChanged(List<NetworkManager.AppInfo> appInfos) {
        updateNotification(appInfos);
    }

    @Override
    public void onDataChanged(float speed, float rxSpeed, float txSpeed) {

    }
}
