package com.carl.netspeednotification.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.carl.netspeednotification.utils.Log;

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

    //status
    private AtomicBoolean mIsDrawing = new AtomicBoolean(false);

    //数据
    private int mWidth, mHeight;

    //Rect
    private Rect mLeftRect, mRightRect;

    //paint
    private Paint mBackgroundPaint;

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
        mBackgroundPaint = new Paint();
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

    public void startDraw(){
        if (mIsDrawing.compareAndSet(false, true)){
            mDrawThread.start();
        }
    }

    public void stopDraw(){
        mIsDrawing.set(false);
    }

    private class DrawThread extends Thread{
        @Override
        public void run() {
            super.run();
            Canvas canvas = null;
            while (mIsDrawing.get()){
                canvas = mHolder.lockCanvas();
                log.info("draw");
                if (canvas == null){
                    continue;
                }
                clearDraw(canvas);
                drawBackground(canvas);
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
        mBackgroundPaint.setColor(Color.parseColor("#166096"));
        canvas.drawRect(mLeftRect, mBackgroundPaint);

        //draw right side
        mBackgroundPaint.setColor(Color.parseColor("#1a81d5"));
        canvas.drawRect(mRightRect, mBackgroundPaint);
    }

}
