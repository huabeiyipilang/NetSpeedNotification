package com.carl.netspeednotification.monitor;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.carl.netspeednotification.NetworkManager;
import com.carl.netspeednotification.R;
import com.carl.netspeednotification.base.BaseItemView;

import org.w3c.dom.Text;

/**
 * Created by carl on 3/17/15.
 */
public class AppSpeedItemView extends BaseItemView{

    private TextView mAppNameView;
    private TextView mSpeedView;
    private TextView mRxSpeedView;
    private TextView mTxSpeedView;
    private TextView mBlowView;

    public AppSpeedItemView(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.item_view_app_network;
    }

    @Override
    protected void initViews() {
        mAppNameView = (TextView)findViewById(R.id.tv_app_name);
        mSpeedView = (TextView)findViewById(R.id.tv_speed);
        mRxSpeedView = (TextView)findViewById(R.id.tv_rx_speed);
        mTxSpeedView = (TextView)findViewById(R.id.tv_tx_speed);
        mBlowView = (TextView)findViewById(R.id.tv_net_blow);
    }

    @Override
    public void bindData(Object data) {
        if (data == null){
            mAppNameView.setText("应用");
            mSpeedView.setText("网度");
            mRxSpeedView.setText("下载速度");
            mTxSpeedView.setText("上传速度");
        }else{
            NetworkManager.AppInfo info = (NetworkManager.AppInfo)data;
            mAppNameView.setText(info.getAppName());
            mSpeedView.setText(NetworkManager.formatSpeed(info.getSpeed()));
//            mRxSpeedView.setText(NetworkManager.formatSpeed(info.getRxSpeed()));
//            mTxSpeedView.setText(NetworkManager.formatSpeed(info.getTxSpeed()));
            mBlowView.setText(NetworkManager.formatSpeed(info.getBlow()));
        }
    }
}
