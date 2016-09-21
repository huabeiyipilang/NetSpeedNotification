package com.carl.netspeednotification;

import android.app.Application;

import com.umeng.analytics.MobclickAgent;

public class App extends Application {
    
    private static App sApp;

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
    }
    
    public static App getContext(){
        return sApp;
    }
}
