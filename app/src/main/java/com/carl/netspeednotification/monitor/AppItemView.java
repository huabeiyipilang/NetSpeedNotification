package com.carl.netspeednotification.monitor;

import android.content.Context;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carl.netspeednotification.manager.AppInfo;
import com.carl.netspeednotification.manager.NetworkManager;
import com.carl.netspeednotification.R;
import com.carl.netspeednotification.base.BaseItemView;

/**
 * Created by carl on 3/17/15.
 */
public class AppItemView extends BaseItemView{
    private ImageView mIconView;
    private TextView mTitleView;
    private TextView mValueView;
    private ProgressBar mProgressView;

    public AppItemView(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.item_view_network_monitor;
    }

    @Override
    protected void initViews() {
        mIconView = (ImageView)findViewById(R.id.iv_app_icon);
        mTitleView = (TextView)findViewById(R.id.tv_app_name);
        mValueView = (TextView)findViewById(R.id.tv_app_value);
        mProgressView = (ProgressBar)findViewById(R.id.pb_progress);
    }

    public void setProgress(float progress, float max){
        mProgressView.setMax((int) max);
        mProgressView.setProgress((int) progress);
    }

    public void setValueText(String value){
        mValueView.setText(value);
    }

    @Override
    public void bindData(Object data) {
        AppInfo info = (AppInfo) data;
        mTitleView.setText(info.getAppName());
        info.loadIcon(mIconView);
    }
}
