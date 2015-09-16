package com.carl.netspeednotification.tests;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.carl.netspeednotification.R;
import com.carl.netspeednotification.manager.NetworkManager;
import com.carl.netspeednotification.view.linearchart.Line;
import com.carl.netspeednotification.view.linearchart.LinearChartView;
import com.carl.netspeednotification.view.linearchart.SpeedAdapter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LinearChartTestActivity extends ActionBarActivity implements NetworkManager.DataChangeListener {

    private LinearChartView mChartView;
    private NetworkManager mNetworkManager;
    private SpeedAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linear_chart_test);
        mChartView = (LinearChartView) findViewById(R.id.view);
        mNetworkManager = NetworkManager.getInstance(this);
        mNetworkManager.addListener(this);
        mAdapter = new SpeedAdapter(mChartView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mChartView.setMax(100, 100);
        List<Line> lines = new LinkedList<Line>();
        lines.add(new Line("", 20));
        lines.add(new Line("", 40));
        lines.add(new Line("", 60));
        mChartView.updateHorizontalLines(lines);
    }

    @Override
    public void onDataChanged(float speed, float rxSpeed, float txSpeed) {
        mAdapter.addSpeed(speed);
    }
}
