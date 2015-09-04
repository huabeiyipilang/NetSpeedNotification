package com.carl.netspeednotification.features;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.carl.netspeednotification.R;
import com.carl.netspeednotification.base.BaseFragment;
import com.carl.netspeednotification.base.BaseItemView;
import com.carl.netspeednotification.base.ItemAdapter;

import java.util.List;

/**
 * Created by carl on 8/24/15.
 */
public class FeatureFragment extends BaseFragment {

    private ListView mFeatureListView;
    private List<FeatureInfo> mFeatureList;
    private BaseAdapter mAdapter;
    private View.OnClickListener mOnFuctionClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v != null && v instanceof Button){
                FeatureInfo info = (FeatureInfo)v.getTag();
                if (info != null){
                    switch (info.getState()){
                        case FeatureInfo.STATE_NO_AUTHORITY:
                            Toast.makeText(getActivity(), "请使用积分解锁该功能", Toast.LENGTH_SHORT).show();
                            break;
                        case FeatureInfo.STATE_ENABLE:
                            break;
                        case FeatureInfo.STATE_DISABLE:
                            break;
                    }
                }
            }
        }
    };

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_feature;
    }

    @Override
    public void initViews(View root) {
        mFeatureListView = (ListView)findViewById(R.id.lv_features);
    }

    @Override
    public void initDatas() {
        mFeatureList = FeatureManager.getsInstance().getFeatureList();
        mAdapter = new FeatureItemAdapter(mFeatureList, FeatureItemView.class);
        mFeatureListView.setAdapter(mAdapter);
    }

    private class FeatureItemAdapter extends ItemAdapter{

        public FeatureItemAdapter(List<? extends Object> list, Class<? extends BaseItemView> viewClass){
            super(list, viewClass);
        }

        @Override
        public View getView(int arg0, View convertView, ViewGroup arg2) {
            FeatureItemView itemView = (FeatureItemView)super.getView(arg0, convertView, arg2);
            itemView.setOnFunctionViewClickListener(mOnFuctionClickListener);
            return itemView;
        }
    }
}
