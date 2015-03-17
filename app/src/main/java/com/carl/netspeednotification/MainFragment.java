package com.carl.netspeednotification;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.carl.netspeednotification.base.BaseFragment;
import com.umeng.analytics.MobclickAgent;

import java.util.List;


public class MainFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener,
    NetworkManager.DataChangeListener{

    private CheckBox mSwitchView;
    private TextView mTestView;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_main;
    }

    public void initViews(View root){
        mSwitchView = (CheckBox)findViewById(R.id.cb_switch);
        mSwitchView.setOnCheckedChangeListener(this);
        mSwitchView.setChecked(isServiceRunning(this.getActivity(), MainService.class.getName()));
        mTestView = (TextView)findViewById(R.id.tv_test);
    }

    public void initDatas(){
        initRate();
    }

    @Override
    public void onResume() {
        super.onResume();
        NetworkManager.getInstance(this.getActivity()).addListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this.getActivity());
        NetworkManager.getInstance(this.getActivity()).removeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.cb_switch){
            if (isChecked) {
                this.getActivity().startService(new Intent(this.getActivity(), MainService.class));
            } else {
                this.getActivity().stopService(new Intent(this.getActivity(), MainService.class));
            }
            buttonView.setText(isChecked ? "关闭" : "开启");
        }else {
            if (isChecked){
                Integer rate = Integer.parseInt((String)buttonView.getTag());
                SharedPreferences prefs = PreferenceUtils.getInstance(this.getActivity()).getDefault();
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
        int rate = PreferenceUtils.getInstance(this.getActivity()).getDefault().getInt("fresh_rate", 3000);
        for (int i = 0; i < rateGroup.getChildCount(); i++){
            RadioButton rb = (RadioButton) rateGroup.getChildAt(i);
            if (Integer.parseInt((String) rb.getTag()) == rate){
                rb.setChecked(true);
            }
            rb.setOnCheckedChangeListener(this);
        }
    }

    @Override
    public void onDataChanged(float speed, float rxSpeed, float txSpeed, List<NetworkManager.AppInfo> appInfos) {
        StringBuilder sb = new StringBuilder();
        for (NetworkManager.AppInfo info : appInfos){
            sb.append(info.getAppName()+"  ")
                    .append("speed:"+NetworkManager.formatSpeed(info.getSpeed())+", ")
                    .append("rxspeed:"+NetworkManager.formatSpeed(info.getRxSpeed())+", ")
                    .append("txspeed:"+NetworkManager.formatSpeed(info.getTxSpeed())+", ")
                    .append("\n");
        }
        mTestView.setText(sb.toString());
    }
}
