package com.carl.netspeednotification.view.linearchart;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by carl on 9/12/15.
 */
public class SpeedAdapter {

    private LinearChartView mChartView;
    private LinkedList<ChartPoint> mPoints = new LinkedList<ChartPoint>();
    private LinkedList<Float> mSpeedList = new LinkedList<Float>();
    private int mPointMaxCount = 10;
    private int mChartWidth;

    public SpeedAdapter(LinearChartView view){
        mChartView = view;
        mChartWidth = mChartView.getChartWidth();
    }

    public void setMaxCount(int maxCount){
        mPointMaxCount = maxCount;
    }

    public void addSpeed(float speed){
        while (mSpeedList.size() > mPointMaxCount){
            mSpeedList.removeLast();
        }
        mSpeedList.push(speed);
        updateUI();
    }

    public void updateUI(){
        mPoints.clear();
        float x = mPointMaxCount;
        for (Float s : mSpeedList){
            mPoints.add(new ChartPoint((int)(x/mPointMaxCount*mChartWidth), (int)s.floatValue()));
            x--;
        }
        mChartView.updatePoints(mPoints);
    }
}
