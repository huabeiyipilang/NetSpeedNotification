package com.carl.netspeednotification;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.umeng.analytics.MobclickAgent;

public class Umeng {
    
    public static Context sContext = App.getContext();
    
    static{
        MobclickAgent.setSessionContinueMillis(5);
    }
    
    public static void init(){
        MobclickAgent.updateOnlineConfig(sContext);
    }
    
    public static void onActivityResume(Activity activity){
        MobclickAgent.onResume(activity);
    }
    
    public static void onActivityPause(Activity activity){
        MobclickAgent.onPause(activity);
    }
    
    public static void onFragmentResume(Fragment fragment){
        MobclickAgent.onPageStart(fragment.getClass().getSimpleName());
    }
    
    public static void onFragmentPause(Fragment fragment){
        MobclickAgent.onPageEnd(fragment.getClass().getSimpleName());
    }


    public static final String ADS_ACTION_PRESENT = "ads_present";
    public static final String ADS_ACTION_DISMISS = "ads_dismiss";
    public static final String ADS_ACTION_FAIL = "ads_fail";
    public static final String ADS_ACTION_NO_AD = "ads_no_ad";
    public static final String ADS_ACTION_CLICK = "ads_click";

    public static void adsActions(String action){
        MobclickAgent.onEvent(sContext, "ads_action", action);
    }

    public static boolean isAdsShouldShow(){
        String show_ad = MobclickAgent.getConfigParams(App.getContext(), "show_ad");
        boolean showAd = "true".equals(show_ad) ? true : false;

        String channels = MobclickAgent.getConfigParams(App.getContext(), "show_ad_channel_list");
        if(TextUtils.isEmpty(channels)){
            showAd = false;
        }else{
            String[] showAdChannelArray = channels.split(",");
            String appChannel = Umeng.getChannel();
            if(showAdChannelArray != null && !TextUtils.isEmpty(appChannel)){
                boolean contains = false;
                for(String channel : showAdChannelArray){
                    contains = appChannel.contains(channel);
                    if(contains){
                        break;
                    }
                }
                showAd = showAd && contains;
            }
        }
        return showAd;
    }
    
    public static String getChannel(){
		try {
			ApplicationInfo appInfo = App.getContext().getPackageManager().getApplicationInfo(App.getContext().getPackageName(),
			PackageManager.GET_META_DATA);
	    	return appInfo.metaData.getString("UMENG_CHANNEL");
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
    }
}
