package com.carl.netspeednotification.tests;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.carl.netspeednotification.R;
import com.carl.netspeednotification.view.LinearChartView;

public class LinearChartTestActivity extends ActionBarActivity {

    private LinearChartView mChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linear_chart_test);
        mChartView = (LinearChartView) findViewById(R.id.view);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
