package com.carl.netspeednotification.features;

import android.content.Context;

import com.carl.netspeednotification.App;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by carl on 8/24/15.
 */
public class FeatureManager {

    public static final int FEATURE_ORDER_APP_SPEED = 1;
    public static final int FEATURE_SHOW_TOP2_IN_NOTIFICATION = 2;

    private static FeatureManager sInstance;

    private Context mContext;
    private List<FeatureInfo> mFeatureList = new LinkedList<FeatureInfo>();

    private FeatureManager(Context context){
        mContext = context;
        initFeatureList();
    }

    private void initFeatureList(){
        FeatureInfo info = new FeatureInfo();
        info.fid = FEATURE_ORDER_APP_SPEED;
        info.fkey = "feature_order_app_speed";
        info.name = "网络监控排序功能";
        info.summary = "可以按照网速、流量等排序";
        info.prize = 10;
        mFeatureList.add(info);

        info = new FeatureInfo();
        info.fid = FEATURE_SHOW_TOP2_IN_NOTIFICATION;
        info.fkey = "feature_show_top2_in_notification";
        info.name = "在通知中显示网速最高的2款应用";
        info.summary = "在通知中显示网速最高的2款应用";
        info.prize = 10;
        mFeatureList.add(info);
    }

    public static FeatureManager getsInstance(){
        if (sInstance == null){
            sInstance = new FeatureManager(App.getContext());
        }
        return sInstance;
    }

    public FeatureInfo getFeatureInfo(int featureId){
        for (FeatureInfo info : mFeatureList) {
            if (info.fid == featureId){
                return info;
            }
        }
        return null;
    }

    public List<FeatureInfo> getFeatureList(){
        return mFeatureList;
    }
}
