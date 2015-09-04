package com.carl.netspeednotification.features;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.carl.netspeednotification.R;
import com.carl.netspeednotification.base.BaseItemView;

/**
 * Created by carl on 8/25/15.
 */
public class FeatureItemView extends BaseItemView{

    private TextView mTitleView;
    private TextView mSummaryView;
    private Button mFuctionView;
    private FeatureInfo mInfo;

    public FeatureItemView(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.item_view_feature;
    }

    @Override
    protected void initViews() {
        mTitleView = (TextView)findViewById(R.id.tv_title);
        mSummaryView = (TextView)findViewById(R.id.tv_summary);
        mFuctionView = (Button)findViewById(R.id.bt_function);
    }

    @Override
    public void bindData(Object data) {
        mInfo = (FeatureInfo)data;
        mTitleView.setText(mInfo.getName());
        mSummaryView.setText(mInfo.getSummary());
        mFuctionView.setTag(mInfo);
        updateFunctionView();
    }

    private void updateFunctionView(){
        int state = mInfo.getState();
        switch (state){
            case FeatureInfo.STATE_NO_AUTHORITY:
                mFuctionView.setText(mInfo.getPrize()+"");
                break;
            case FeatureInfo.STATE_ENABLE:
                mFuctionView.setText("关闭");
                break;
            case FeatureInfo.STATE_DISABLE:
                mFuctionView.setText("开启");
                break;
        }
    }

    public void setOnFunctionViewClickListener(View.OnClickListener listener){
        mFuctionView.setOnClickListener(listener);
    }
}
