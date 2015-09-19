package com.carl.netspeednotification;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.carl.netspeednotification.base.BaseFragment;
import com.carl.netspeednotification.base.BlankActivity;
import com.carl.netspeednotification.manager.NetworkManager;
import com.carl.netspeednotification.monitor.NetworkMonitorFragment;
import com.carl.netspeednotification.notification.NetworkNotifManager;
import com.carl.netspeednotification.notification.NotificationService;
import com.umeng.analytics.MobclickAgent;

import java.util.List;


public class MainFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener{

    private CheckBox mSwitchView;
    private NetworkManager mNetworkManager;
    private NetworkNotifManager mNotifManager;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_main;
    }

    public void initViews(View root){
        mSwitchView = (CheckBox)findViewById(R.id.cb_switch);
        mSwitchView.setOnCheckedChangeListener(this);
        findViewById(R.id.bt_network_detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlankActivity.startFragmentActivity(getActivity(), NetworkMonitorFragment.class, null);
            }
        });
    }

    public void initDatas(){
        mNetworkManager = NetworkManager.getInstance(getActivity().getApplicationContext());
        mNotifManager = NetworkNotifManager.getInstance();
        mSwitchView.setChecked(mNotifManager.isServiceRunning());
        initRate();

        RadioButton enableButton = (RadioButton)findViewById(R.id.rb_app_show_enable);
        RadioButton disableButton = (RadioButton)findViewById(R.id.rb_app_show_disable);
        if (mNotifManager.getNotifType() == NetworkNotifManager.NOTIF_TYPE_DEFAULT){
            disableButton.setChecked(true);
        }else{
            enableButton.setChecked(true);
        }
        CompoundButton.OnCheckedChangeListener onCheckedListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked){
                    return;
                }
                switch (buttonView.getId()){
                    case R.id.rb_app_show_enable:
                        mNotifManager.setNotifType(NetworkNotifManager.NOTIF_TYPE_APPS);
                        break;
                    case R.id.rb_app_show_disable:
                        mNotifManager.setNotifType(NetworkNotifManager.NOTIF_TYPE_DEFAULT);
                        break;
                }
            }
        };
        enableButton.setOnCheckedChangeListener(onCheckedListener);
        disableButton.setOnCheckedChangeListener(onCheckedListener);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this.getActivity());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.cb_switch) {
            if (isChecked) {
                mNotifManager.showNotification();
            } else {
                mNotifManager.hideNotification();
            }
            buttonView.setText(isChecked ? "关闭" : "开启");
        }else {
            if (isChecked){
                Integer rate = Integer.parseInt((String)buttonView.getTag());
                NetworkManager.getInstance(getActivity()).setFreshRate(rate);
            }
        }
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
