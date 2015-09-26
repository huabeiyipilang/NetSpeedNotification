package com.carl.netspeednotification.monitor;

import android.database.DataSetObserver;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.carl.netspeednotification.manager.AppInfo;
import com.carl.netspeednotification.manager.NetworkManager;
import com.carl.netspeednotification.R;
import com.carl.netspeednotification.base.BaseFragment;
import com.carl.netspeednotification.base.ItemAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by carl on 9/5/15.
 */
public class NetworkMonitorFragment extends BaseFragment implements NetworkManager.AppDataChangeListener {

    private Spinner mTitleSpinner;
    private ListView mListView;
    private List<AppInfo> mAppInfos = new ArrayList<AppInfo>();
    private AppItemAdapter mAdapter;
    private Strategy mStrategy = new BlowStrategy();
    private List<Strategy> mStrategyList = new ArrayList<Strategy>();

    private Comparator<AppInfo> mComparator = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo lhs, AppInfo rhs) {
            return (int)(mStrategy.getValue(rhs) - mStrategy.getValue(lhs));
        }
    };

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_network_monitor;
    }

    @Override
    public void initViews(View root) {
        mListView = (ListView)findViewById(R.id.lv_listview);
        mAdapter = new AppItemAdapter();
        mAdapter.setData(mAppInfos);
        mAdapter.setView(AppItemView.class);
        mListView.setAdapter(mAdapter);

        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setCustomView(R.layout.actionbar_network_monitor_fragment);
        actionBar.setDisplayShowCustomEnabled(true);
        mTitleSpinner = (Spinner)getActivity().findViewById(R.id.sp_tabs);
        getActivity().findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    @Override
    public void initDatas() {
        mStrategyList.add(new BlowStrategy());
        mStrategyList.add(new BlowRxStrategy());
        mStrategyList.add(new BlowTxStrategy());
        mStrategyList.add(new SpeedStrategy());
        mStrategyList.add(new SpeedRxStrategy());
        mStrategyList.add(new SpeedTxStrategy());

        SpinnerAdapter strategyAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return mStrategyList.size();
            }

            @Override
            public Strategy getItem(int position) {
                return mStrategyList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null){
                    convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_view_monitor_strategy, null);
                    convertView.setTag(convertView.findViewById(R.id.tv_strategy));
                }
                TextView strategyView = (TextView)convertView.getTag();
                strategyView.setText(getItem(position).getTitle());

                return convertView;
            }
        };

        mTitleSpinner.setAdapter(strategyAdapter);
        mTitleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Strategy strategy = mStrategyList.get(position);
                mStrategy = strategy;
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        Collections.sort(mAppInfos, mComparator);
        if (mAppInfos != null && mAppInfos.size() > 0){
            mAdapter.setMaxValue(mStrategy.getValue(mAppInfos.get(0)));
        }
        mAdapter.notifyDataSetChanged();
    }

    private class AppItemAdapter extends ItemAdapter{
        private float maxValue;

        public void setMaxValue(float max){
            maxValue = max;
        }

        @Override
        public View getView(int arg0, View convertView, ViewGroup arg2) {
            AppItemView view = (AppItemView) super.getView(arg0, convertView, arg2);
            AppInfo info = (AppInfo) getItem(arg0);
            view.setProgress(mStrategy.getValue(info), maxValue);
            view.setValueText(mStrategy.getValueText(info));
            return view;
        }
    }

    private abstract class Strategy{
        abstract String getTitle();
        abstract float getValue(AppInfo info);
        abstract String getValueText(AppInfo info);
    }

    private class BlowStrategy extends Strategy{

        @Override
        String getTitle() {
            return "总流量排行";
        }

        @Override
        float getValue(AppInfo info) {
            return info.getBlow();
        }

        @Override
        String getValueText(AppInfo info) {
            return NetworkManager.formatBlow(getValue(info));
        }
    }

    private class BlowTxStrategy extends Strategy{

        @Override
        String getTitle() {
            return "上传流量排行";
        }

        @Override
        float getValue(AppInfo info) {
            return info.getTxBlow();
        }

        @Override
        String getValueText(AppInfo info) {
            return NetworkManager.formatBlow(getValue(info));
        }
    }

    private class BlowRxStrategy extends Strategy{

        @Override
        String getTitle() {
            return "下载流量排行";
        }

        @Override
        float getValue(AppInfo info) {
            return info.getRxBlow();
        }

        @Override
        String getValueText(AppInfo info) {
            return NetworkManager.formatBlow(getValue(info));
        }
    }

    protected class SpeedStrategy extends Strategy{
        @Override
        String getTitle() {
            return "当前网速排行";
        }

        @Override
        float getValue(AppInfo info) {
            return info.getSpeed();
        }

        @Override
        String getValueText(AppInfo info) {
            return NetworkManager.formatSpeed(getValue(info));
        }
    }

    protected class SpeedTxStrategy extends Strategy{
        @Override
        String getTitle() {
            return "上传网速排行";
        }

        @Override
        float getValue(AppInfo info) {
            return info.getTxSpeed();
        }

        @Override
        String getValueText(AppInfo info) {
            return NetworkManager.formatSpeed(getValue(info));
        }
    }

    protected class SpeedRxStrategy extends Strategy{
        @Override
        String getTitle() {
            return "下载网速排行";
        }

        @Override
        float getValue(AppInfo info) {
            return info.getRxSpeed();
        }

        @Override
        String getValueText(AppInfo info) {
            return NetworkManager.formatSpeed(getValue(info));
        }
    }
}
