package com.carl.netspeednotification.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.carl.netspeednotification.LaunchActivity;
import com.carl.netspeednotification.R;
import com.carl.netspeednotification.manager.AppInfo;
import com.carl.netspeednotification.manager.NetworkManager;

import java.util.List;

import cn.kli.utils.Conversion;

public class NotificationService extends Service implements NetworkManager.AppDataChangeListener, NetworkManager.DataChangeListener {

    private final static boolean DEBUG = true;

    private NetworkManager mNetworkManager;
    private NetworkNotifManager mNotifManager;
    private Notification mNotification = new Notification();
    private int[] mNotifAppIds = {R.id.iv_app_1, R.id.iv_app_2, R.id.iv_app_3, R.id.iv_app_4};
    private BroadcastReceiver mScreenOnOffReceiver = new BroadcastReceiver() {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
                mNetworkManager.start();
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
                mNetworkManager.stop();
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNetworkManager = NetworkManager.getInstance(this);
        mNotifManager = NetworkNotifManager.getInstance();
        mNotifManager.bindService(this);
        initNotification();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mScreenOnOffReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void initNotification() {
        mNotification = new Notification();
        mNotification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        mNotification.contentIntent = PendingIntent.getActivity(this, 1, new Intent(this, LaunchActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
        showNotification(mNotifManager.getNotifType());
    }

    public void showNotification(int type) {
        log("Show notification type:" + type);
        switch (type) {
            case NetworkNotifManager.NOTIF_TYPE_DEFAULT:
                mNetworkManager.removeAppListener(this);
                mNetworkManager.addListener(this);
                break;
            case NetworkNotifManager.NOTIF_TYPE_APPS:
                mNetworkManager.removeListener(this);
                mNetworkManager.addAppListener(this);
                break;
        }
    }

    private void updateDefaultNotification() {
        log("updateDefaultNotification");
        mNotification.icon = mNetworkManager.getSpeedIcon();
        if (Build.VERSION.SDK_INT >= 11) {
            mNotification.largeIcon = Conversion.drawable2Bitmap(getResources().getDrawable(R.drawable.ic_launcher));
        }
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_main);
        contentView.setTextViewText(R.id.tv_speed, NetworkManager.formatSpeed(mNetworkManager.getSpeed()));
        contentView.setTextViewText(R.id.tv_blow, NetworkManager.formatBlow(mNetworkManager.getBlow()));

        mNotification.contentView = contentView;

        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        manager.notify(1001, mNotification);
    }

    private void updateDetailNotification(List<AppInfo> appInfos) {
        log("updateDetailNotification");
        mNotification.icon = mNetworkManager.getSpeedIcon();
        if (Build.VERSION.SDK_INT >= 11) {
            mNotification.largeIcon = Conversion.drawable2Bitmap(getResources().getDrawable(R.drawable.ic_launcher));
        }
        log("icon id:" + mNotification.icon);
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_with_apps);
        contentView.setTextViewText(R.id.tv_speed, NetworkManager.formatSpeed(mNetworkManager.getSpeed()));
        contentView.setTextViewText(R.id.tv_blow, NetworkManager.formatBlow(mNetworkManager.getBlow()));

        int appSize = mNotifAppIds.length;
        int appCount = 0;
        for (int i = 0; i < appSize; i++) {
            AppInfo info = appInfos.get(i);
            if (info.getSpeed() > 0) {
                appCount++;
                contentView.setViewVisibility(mNotifAppIds[i], View.VISIBLE);
                contentView.setImageViewBitmap(mNotifAppIds[i], info.getIcon());
            } else {
                contentView.setViewVisibility(mNotifAppIds[i], View.GONE);
            }
        }
        contentView.setViewVisibility(R.id.tv_no_app, appCount == 0 ? View.VISIBLE : View.GONE);
        mNotification.contentView = contentView;

        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        manager.notify(1001, mNotification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        mNetworkManager.removeAppListener(this);
        mNetworkManager.removeListener(this);
        unregisterReceiver(mScreenOnOffReceiver);
    }

    @Override
    public void onAppDataChanged(List<AppInfo> appInfos) {
        try {
            updateDetailNotification(appInfos);
        } catch (Exception ignore) {

        }
    }

    @Override
    public void onDataChanged(float speed, float rxSpeed, float txSpeed) {
        try {
            updateDefaultNotification();
        } catch (Exception ignore) {

        }
    }

    private void log(String log) {
        if (DEBUG) {
            Log.i(getClass().getSimpleName(), log);
        }
    }
}
