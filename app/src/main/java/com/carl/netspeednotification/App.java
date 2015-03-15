package com.carl.netspeednotification;

import android.app.Application;

import com.umeng.analytics.MobclickAgent;

public class App extends Application {
    
    private static App sApp;

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
        MobclickAgent.updateOnlineConfig(this);
    }
    
    public static App getContext(){
        return sApp;
    }
}
