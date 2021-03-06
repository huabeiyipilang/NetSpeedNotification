package com.carl.netspeednotification.monitor;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.carl.netspeednotification.manager.AppInfo;
import com.carl.netspeednotification.manager.NetworkManager;
import com.carl.netspeednotification.R;
import com.carl.netspeednotification.base.BaseFragment;
import com.carl.netspeednotification.base.ItemAdapter;
import com.carl.netspeednotification.features.FeatureInfo;
import com.carl.netspeednotification.features.FeatureManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by carl on 3/17/15.
 */
public class SpeedMonitorFragment extends BaseFragment implements NetworkManager.AppDataChangeListener {

    private final static int SORT_APP_NAME = 1;
    private final static int SORT_SPEED = 2;
    private final static int SORT_FLOW = 3;

    private int mSortMode = SORT_SPEED;

    private Comparator<AppInfo> mComparator = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo lhs, AppInfo rhs) {
            switch (mSortMode){
                case SORT_APP_NAME:
                    return lhs.getAppName().compareTo(rhs.getAppName());
                case SORT_SPEED:
                    return (int)(rhs.getSpeed() - lhs.getSpeed());
                case SORT_FLOW:
                    return (int)(rhs.getBlow() - lhs.getBlow());
            }
            return 0;
        }
    };

    private ListView mSpeedListView;
    private List<AppInfo> mAppInfos = new ArrayList<AppInfo>();
    private ItemAdapter mAdapter;
    private View mArrowName;
    private View mArrowSpeed;
    private View mArrowFlow;
    private FeatureInfo mOrderFeatureInfo;

    private OnClickListener mSortClickListener = new OnClickListener(){

        @Override
        public void onClick(View v) {
            if (mOrderFeatureInfo.getState() != FeatureInfo.STATE_ENABLE){
                return;
            }
            switch (v.getId()){
                case R.id.tab_app_name:
                    mSortMode = SORT_APP_NAME;
                    break;
                case R.id.tab_speed:
                    mSortMode = SORT_SPEED;
                    break;
                case R.id.tab_flow:
                    mSortMode = SORT_FLOW;
                    break;
            }
            updateSort();
        }
    };

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_speed_monitor;
    }

    @Override
    public void initViews(View root) {
        findViewById(R.id.tab_app_name).setOnClickListener(mSortClickListener);
        findViewById(R.id.tab_speed).setOnClickListener(mSortClickListener);
        findViewById(R.id.tab_flow).setOnClickListener(mSortClickListener);
        mArrowName = findViewById(R.id.iv_arrow_app);
        mArrowName.setTag(SORT_APP_NAME);
        mArrowSpeed = findViewById(R.id.iv_arrow_speed);
        mArrowSpeed.setTag(SORT_SPEED);
        mArrowFlow = findViewById(R.id.iv_arrow_flow);
        mArrowFlow.setTag(SORT_FLOW);
        mSpeedListView = (ListView)findViewById(R.id.lv_speed);
    }

    @Override
    public void initDatas() {
        getActivity().setTitle("网络监控");
        mOrderFeatureInfo = FeatureManager.getsInstance().getFeatureInfo(FeatureManager.FEATURE_ORDER_APP_SPEED);
        mAdapter = new ItemAdapter(mAppInfos, AppSpeedItemView.class);
        mSpeedListView.setAdapter(mAdapter);
        updateSort();
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
    public void onAppDataChanged(List<AppInfo> appInfos) {
        mAppInfos.clear();
        mAppInfos.addAll(appInfos);
        if (mSortMode != SORT_SPEED){
            Collections.sort(mAppInfos, mComparator);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void updateSort(){
        if (mOrderFeatureInfo.getState() != FeatureInfo.STATE_ENABLE){
            return;
        }
        mArrowName.setVisibility((int)mArrowName.getTag() == mSortMode ? View.VISIBLE : View.GONE);
        mArrowSpeed.setVisibility((int)mArrowSpeed.getTag() == mSortMode ? View.VISIBLE : View.GONE);
        mArrowFlow.setVisibility((int)mArrowFlow.getTag() == mSortMode ? View.VISIBLE : View.GONE);
        Collections.sort(mAppInfos, mComparator);
        mAdapter.notifyDataSetChanged();
    }
}
