package com.carl.netspeednotification;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.carl.netspeednotification.base.BaseFragment;
import com.carl.netspeednotification.base.BlankActivity;
import com.carl.netspeednotification.features.FeatureFragment;
import com.carl.netspeednotification.monitor.SpeedMonitorFragment;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;


public class MainFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener{

    private CheckBox mSwitchView;
    private NetworkManager mNetworkManager;

    private NetworkManager.DataChangeListener mSpeedChangeListener = new NetworkManager.DataChangeListener() {
        @Override
        public void onDataChanged(float speed, float rxSpeed, float txSpeed) {
        }
    };

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_main;
    }

    public void initViews(View root){
        mSwitchView = (CheckBox)findViewById(R.id.cb_switch);
        mSwitchView.setOnCheckedChangeListener(this);
        mSwitchView.setChecked(isServiceRunning(this.getActivity(), MainService.class.getName()));
        findViewById(R.id.bt_network_detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlankActivity.startFragmentActivity(getActivity(), SpeedMonitorFragment.class, null);
//                BlankActivity.startFragmentActivity(getActivity(), FeatureFragment.class, null);
            }
        });
    }

    public void initDatas(){
        mNetworkManager = NetworkManager.getInstance(getActivity().getApplicationContext());
        initRate();
    }

    @Override
    public void onResume() {
        super.onResume();
        mNetworkManager.addListener(mSpeedChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        mNetworkManager.removeListener(mSpeedChangeListener);
        MobclickAgent.onPause(this.getActivity());
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
                NetworkManager.getInstance(getActivity()).setFreshRate(rate);
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
        int rate = NetworkManager.getInstance(getActivity()).getFreshRate();
        for (int i = 0; i < rateGroup.getChildCount(); i++){
            RadioButton rb = (RadioButton) rateGroup.getChildAt(i);
            if (Integer.parseInt((String) rb.getTag()) == rate){
                rb.setChecked(true);
            }
            rb.setOnCheckedChangeListener(this);
        }
    }
}
