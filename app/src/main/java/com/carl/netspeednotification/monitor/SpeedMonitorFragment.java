package com.carl.netspeednotification.monitor;

import android.view.View;
import android.widget.ListView;

import com.carl.netspeednotification.NetworkManager;
import com.carl.netspeednotification.R;
import com.carl.netspeednotification.base.BaseFragment;
import com.carl.netspeednotification.base.ItemAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carl on 3/17/15.
 */
public class SpeedMonitorFragment extends BaseFragment implements NetworkManager.AppDataChangeListener {

    private ListView mSpeedListView;
    private List<NetworkManager.AppInfo> mAppInfos = new ArrayList<NetworkManager.AppInfo>();
    private ItemAdapter mAdapter;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_speed_monitor;
    }

    @Override
    public void initViews(View root) {
        mSpeedListView = (ListView)findViewById(R.id.lv_speed);
    }

    @Override
    public void initDatas() {
        getActivity().setTitle("网络监控");
        mAdapter = new ItemAdapter(mAppInfos, AppSpeedItemView.class);
        mSpeedListView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        NetworkManager.getInstance(getActivity()).addAppListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        NetworkManager.getInstance(getActivity()).removeAppListener(this);
    }

    @Override
    public void onAppDataChanged(List<NetworkManager.AppInfo> appInfos) {
        mAppInfos.clear();
        mAppInfos.addAll(appInfos);
        mAdapter.notifyDataSetChanged();
    }
}
