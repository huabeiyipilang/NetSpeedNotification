package com.carl.netspeednotification;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mobads.InterstitialAd;
import com.baidu.mobads.InterstitialAdListener;
import com.baidu.mobads.SplashAd;
import com.baidu.mobads.SplashAdListener;
import com.carl.netspeednotification.manager.NetworkManager;
import com.carl.netspeednotification.utils.PreferenceUtils;

import java.lang.ref.WeakReference;


public class LaunchActivity extends ActionBarActivity {

    private final static int AD_TIME = 1;
    private final static int MSG_TIME = 1;
    private final static int MSG_ENTER_APP = 2;
    private ViewGroup mAdsGroup;
    private boolean mNeedShowAd = true;
    private TextView mTimeView;

    private LaunchHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        mAdsGroup = (ViewGroup)findViewById(R.id.ads_container);
        mTimeView = (TextView)findViewById(R.id.tv_time);
        mHandler = new LaunchHandler(this);
        findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.sendEmptyMessage(MSG_ENTER_APP);
            }
        });
        initPreference();
        initAd();
    }

    @Override
    public void onResume() {
        super.onResume();
        Umeng.onActivityResume(this);
        mHandler.sendEmptyMessage(MSG_TIME);
        NetworkManager.getInstance(getApplicationContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeMessages(MSG_ENTER_APP);
        Umeng.onActivityPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void enterApp(){
        mNeedShowAd = false;
        mHandler.removeMessages(MSG_TIME);
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    private void initPreference(){
        SharedPreferences prefs = PreferenceUtils.getInstance().getDefault();
        if (prefs.getInt("fresh_rate", 0) == 0){
            prefs.edit().putInt("fresh_rate", 3000).commit();
        }
    }

    private void initAd(){
//        if (Umeng.isAdsShouldShow()){
//            String type = Umeng.launcherAdType();
//            if ("chaping".equals(type)){
//                initInterstitialAd();
//            }else{
//                initAds();
//            }
//        }
    }

    private void initInterstitialAd(){
        final InterstitialAd interAd=new InterstitialAd(this);
        interAd.setListener(new InterstitialAdListener() {

            @Override
            public void onAdClick(InterstitialAd arg0) {
                Umeng.adsActions(Umeng.ADS_ACTION_CLICK);
            }

            @Override
            public void onAdDismissed() {
                mHandler.sendEmptyMessage(MSG_ENTER_APP);
                Umeng.adsActions(Umeng.ADS_ACTION_DISMISS);
            }

            @Override
            public void onAdFailed(String arg0) {
                if ("no ad".equals(arg0)) {
                    Umeng.adsActions(Umeng.ADS_ACTION_NO_AD);
                } else {
                    Umeng.adsActions(Umeng.ADS_ACTION_FAIL);
                }
            }

            @Override
            public void onAdPresent() {
                Umeng.adsActions(Umeng.ADS_ACTION_PRESENT);
            }

            @Override
            public void onAdReady() {
                if (mNeedShowAd) {
                    mHandler.removeMessages(MSG_ENTER_APP);
                    interAd.showAd(LaunchActivity.this);
                }
            }

        });
        interAd.loadAd();
    }

    private void initAds(){

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

    private void updateTimeLeft(int time){
        mTimeView.setText(time +"");
    }

    private static class LaunchHandler extends Handler{

        private final WeakReference<LaunchActivity> mLaunchActivityRef;
        private LaunchActivity mActivity;
        private int mTimeLeft = AD_TIME;

        LaunchHandler(LaunchActivity activity){
            mLaunchActivityRef = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mActivity = mLaunchActivityRef.get();
            if (mActivity == null){
                return;
            }

            switch (msg.what){
                case MSG_TIME:
                    if (mTimeLeft == 0)
                    {
                        sendEmptyMessage(MSG_ENTER_APP);
                    }else{
                        mActivity.updateTimeLeft(mTimeLeft);
                        removeMessages(MSG_TIME);
                        sendEmptyMessageDelayed(MSG_TIME, 1000);
                    }
                    mTimeLeft--;
                    break;
                case MSG_ENTER_APP:
                    mActivity.enterApp();
                    break;
            }
        }
    }
}
