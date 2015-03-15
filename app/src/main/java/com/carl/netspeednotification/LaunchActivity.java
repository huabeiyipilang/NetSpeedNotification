package com.carl.netspeednotification;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ViewGroup;

import com.baidu.mobads.SplashAd;
import com.baidu.mobads.SplashAdListener;
import com.umeng.analytics.MobclickAgent;


public class LaunchActivity extends ActionBarActivity {

    private Handler mHandler = new Handler();
    private ViewGroup mAdsGroup;
    private Runnable mStartRunnable = new Runnable() {
        @Override
        public void run() {
            LaunchActivity.this.enterApp();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        mAdsGroup = (ViewGroup)findViewById(R.id.ads_container);
        initPreference();
        initAds();
        mHandler.postDelayed(mStartRunnable, BuildConfig.DEBUG ? 5000 : 5000);
    }

    @Override
    public void onResume() {
        super.onResume();
        Umeng.onActivityResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mStartRunnable);
        Umeng.onActivityPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void enterApp(){
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    private void initPreference(){
        SharedPreferences prefs = PreferenceUtils.getInstance(this).getDefault();
        if (prefs.getInt("fresh_rate", 0) == 0){
            prefs.edit().putInt("fresh_rate", 3000).commit();
        }
    }

    private void initAds(){
        if (!Umeng.isAdsShouldShow()){
            return;
        }

        SplashAdListener listener = new SplashAdListener() {
            @Override
            public void onAdPresent() {
                Umeng.adsActions(Umeng.ADS_ACTION_PRESENT);
            }

            @Override
            public void onAdDismissed() {
                Umeng.adsActions(Umeng.ADS_ACTION_DISMISS);
            }

            @Override
            public void onAdFailed(String s) {
                if ("no ad".equals(s)){
                    Umeng.adsActions(Umeng.ADS_ACTION_NO_AD);
                }else{
                    Umeng.adsActions(Umeng.ADS_ACTION_FAIL);
                }
            }

            @Override
            public void onAdClick() {
                Umeng.adsActions(Umeng.ADS_ACTION_CLICK);
            }
        };
        new SplashAd(this, mAdsGroup, listener, "", true, SplashAd.SplashType.CACHE);
    }

}
