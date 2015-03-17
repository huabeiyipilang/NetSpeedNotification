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

public class MainService extends Service implements NetworkManager.DataChangeListener{

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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateNotification();
        return START_STICKY;
    }

    private void initNotification(){
        mNotification = new Notification();
        mNotification.flags = Notification.FLAG_NO_CLEAR|Notification.FLAG_ONGOING_EVENT;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, new Intent(this, LaunchActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
        mNotification.contentIntent = pendingIntent;
    }


    @Override
    public void onDataChanged(float speed, float rxSpeed, float txSpeed) {
        updateNotification();
    }

    private void updateNotification(){
        mNotification.icon = mNetworkManager.getSpeedIcon();
        if(Build.VERSION.SDK_INT >= 11){
            mNotification.largeIcon = Conversion.drawable2Bitmap(getResources().getDrawable(R.drawable.ic_launcher));
        }
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_main);
        contentView.setTextViewText(R.id.tv_title, getString(R.string.app_name));
        contentView.setTextViewText(R.id.tv_content, "上行："+NetworkManager.formatSpeed(mNetworkManager.getTxSpeed())
                +"， 下行："+NetworkManager.formatSpeed(mNetworkManager.getRxSpeed()));
        mNotification.contentView = contentView;
        startForeground(1001, mNotification);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        mNetworkManager.removeListener(this);
    }
}
