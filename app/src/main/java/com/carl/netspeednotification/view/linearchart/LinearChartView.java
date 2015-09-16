package com.carl.netspeednotification.view.linearchart;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.carl.netspeednotification.utils.Log;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by carl on 9/10/15.
 */
public class LinearChartView extends SurfaceView implements SurfaceHolder.Callback {

    private static final int LEFT_SIDE_WIDTH = 100;

    private static final int FPS = 25;
    private static final int TIME_PER_FRAME = 1000/25;

    private Log log = new Log(LinearChartView.class.getSimpleName());
    private SurfaceHolder mHolder;
    private DrawThread mDrawThread;
    private ValueTranslate mValueTransfer = new ValueTranslate();

    //status
    private AtomicBoolean mIsDrawing = new AtomicBoolean(false);

    //数据
    private int mWidth, mHeight;
    private Vector<Line> mHorizontalLines = new Vector<Line>();
    private Vector<Line> mVerticalLines = new Vector<Line>();
    private Vector<ChartPoint> mPoints = new Vector<ChartPoint>();

    //Rect
    private Rect mLeftRect, mRightRect;

    //paint
    private Paint mColorPaint;

    public LinearChartView(Context context) {
        super(context);
        log.info("init 1");
        init();
    }

    public LinearChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        log.info("init 2");
        init();
    }

    public LinearChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        log.info("init 3");
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LinearChartView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        log.info("init 4");
        init();
    }

    private void init(){
        mHolder = getHolder();
        mHolder.addCallback(this);

        mDrawThread = new DrawThread();

        //init paint
        mColorPaint = new Paint();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mWidth = width;
        mHeight = height;
        mLeftRect = new Rect(0, 0, LEFT_SIDE_WIDTH, mHeight);
        mRightRect = new Rect(LEFT_SIDE_WIDTH, 0, mWidth, mHeight);

        startDraw();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopDraw();
    }

    public void setMax(int x, int y){
        mValueTransfer.xMaxValue = x;
        mValueTransfer.yMaxValue = y;
    }

    public void startDraw(){
        if (mIsDrawing.compareAndSet(false, true)){
            mDrawThread.start();
        }
    }

    public void updateHorizontalLines(List<Line> lines){
        mHorizontalLines.clear();
        mHorizontalLines.addAll(lines);
    }

    public void updateVerticalLines(List<Line> lines){
        mVerticalLines.clear();
        mVerticalLines.addAll(lines);
    }

    public void updatePoints(List<ChartPoint> points){
        mPoints.clear();
        mPoints.addAll(points);
    }

    public void stopDraw(){
        mIsDrawing.set(false);
    }

    public int getChartWidth(){
        return mWidth - LEFT_SIDE_WIDTH;
    }

    private class DrawThread extends Thread{
        @Override
        public void run() {
            super.run();
            Canvas canvas = null;
            while (mIsDrawing.get()){
                canvas = mHolder.lockCanvas();
                if (canvas == null){
                    continue;
                }
                clearDraw(canvas);
                drawBackground(canvas);
                drawLines(canvas);
                drawPoints(canvas);
                mHolder.unlockCanvasAndPost(canvas);
                try {
                    sleep(TIME_PER_FRAME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
        }
    }

    private void clearDraw(Canvas canvas){
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }

    private void drawBackground(Canvas canvas){
        //draw left side
        mColorPaint.setColor(Color.parseColor("#166096"));
        canvas.drawRect(mLeftRect, mColorPaint);

        //draw right side
        mColorPaint.setColor(Color.parseColor("#1a81d5"));
        canvas.drawRect(mRightRect, mColorPaint);
    }

    private void drawLines(Canvas canvas){
        mColorPaint.setColor(Color.WHITE);
        int postion = 0;
        for (Line line : mHorizontalLines){
            postion = mValueTransfer.getHLinePosition(line.value);
            canvas.drawLine(LEFT_SIDE_WIDTH, postion, mWidth, postion, mColorPaint);
        }
        for (Line line : mVerticalLines){
            postion = mValueTransfer.getVLinePosition(line.value);
            canvas.drawLine(postion, 0, postion, mHeight, mColorPaint);
        }
    }

    private void drawPoints(Canvas canvas){
        if (mPoints.size() == 0){
            return;
        }
        Path path = new Path();
        ChartPoint startPoint = mPoints.get(0);
        path.moveTo(startPoint.x, startPoint.y);
        for (ChartPoint point : mPoints){
            path.lineTo(point.x, point.y);
        }
        mColorPaint.setColor(Color.GREEN);
        canvas.drawPath(path, mColorPaint);
    }

    private class ValueTranslate{
        int xMaxValue;
        int yMaxValue;

        int getHLinePosition(int value){
            return (int) ((1f - (float)value/(float) yMaxValue)*mHeight);
        }

        int getVLinePosition(int value){
            return (int) ((float)value/(float) xMaxValue *mHeight);
        }
    }
}
