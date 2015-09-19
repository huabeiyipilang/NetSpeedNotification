package com.carl.netspeednotification;

import com.carl.netspeednotification.common.SpeedChartView;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by carl on 9/12/15.
 */
public class SpeedAdapter {

    private SpeedChartView mChartView;
    private LinkedList<Float> mSpeedList = new LinkedList<Float>();
    private int mPointMaxCount = 20;
    private int mChartWidth;

    public SpeedAdapter(SpeedChartView view){
        mChartView = view;
    }

    public void setMaxCount(int maxCount){
        mPointMaxCount = maxCount;
    }

    public void addSpeed(float speed){
        while (mSpeedList.size() > mPointMaxCount - 1){
            mSpeedList.removeLast();
        }
        mSpeedList.push(speed);
        updateUI();
    }

    public void updateUI(){
        float max = 100*1000;
//        for (Float speed : mSpeedList){
//            if (speed > max){
//                max = speed;
//            }
//        }

        double[] speeds = new double[mPointMaxCount];
        for (int i = 0; i < mPointMaxCount; i++){
            double speed = 0;
            try{
                speed = mSpeedList.get(i)/max;
            }catch (Exception e){

            }
            speeds[i] = speed+0.05;
        }

        mChartView.setDatas(speeds);
    }
}