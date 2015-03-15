package com.carl.netspeednotification;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.umeng.analytics.MobclickAgent;

import java.util.List;


public class MainActivity extends ActionBarActivity implements CompoundButton.OnCheckedChangeListener {

    private CheckBox mSwitchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initRate();
        mSwitchView = (CheckBox)findViewById(R.id.cb_switch);
        mSwitchView.setOnCheckedChangeListener(this);
        mSwitchView.setChecked(isServiceRunning(this, MainService.class.getName()));
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.cb_switch){
            if (isChecked) {
                startService(new Intent(this, MainService.class));
            } else {
                stopService(new Intent(this, MainService.class));
            }
            buttonView.setText(isChecked ? "关闭" : "开启");
        }else {
            if (isChecked){
                Integer rate = Integer.parseInt((String)buttonView.getTag());
                SharedPreferences prefs = PreferenceUtils.getInstance(this).getDefault();
                prefs.edit().putInt("fresh_rate", rate).commit();
            }
        }
    }

    public static boolean isServiceRunning(Context mContext,String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(30);
        if (!(serviceList.size()>0)) {
            return false;
        }
        for (int i=0; i<serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    private void initRate(){
        RadioGroup rateGroup = (RadioGroup)findViewById(R.id.rg_rate_list);
        int rate = PreferenceUtils.getInstance(this).getDefault().getInt("fresh_rate", 3000);
        for (int i = 0; i < rateGroup.getChildCount(); i++){
            RadioButton rb = (RadioButton) rateGroup.getChildAt(i);
            if (Integer.parseInt((String) rb.getTag()) == rate){
                rb.setChecked(true);
            }
            rb.setOnCheckedChangeListener(this);
        }
    }
}
