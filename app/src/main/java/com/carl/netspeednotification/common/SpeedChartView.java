
package com.carl.netspeednotification.common;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.View;

import com.carl.netspeednotification.utils.UiUtils;

public class SpeedChartView extends View {

    private class EndInfo {
        int endPosition;
        double endPercent;
    }

    private static final String TAG = "PowerChartView";

    private static final int TOTAL_PERCENT_FRACTION = 1;
    private static final int TEMPERATION_LINE_WIDTH = 3;
    private static final int HORIZONTAL_LINE_COUNT = 5;
    private static final int TIME_LINE_COUNT = 6;

    private static final int TOP_DISTANCE = 35;
    private static final int BOTTOM_DISTANCE = 20;
    private static final int LEFT_DISTANCE = 20;
    private static final int PATH_RIGHT_DISTANCE = 35;
    private static final int CURVE_RIGHT_DISTANCE = 20;

    private static final int TIME_TEXT_COLOR = 0x99FFFFFF;
    private static final int TIME_TEXT_SIZE = 10;
    private static final int HORIZONTAL_LINE_COLOR = 0x99FFFFFF;

    private static final int HORIZONTAL_LINE_SIZE = 1;
    private static final int TIME_VERTICAL_LINE_HEIGHT = 3;
    private static final int DRAW_PERCENT_DISTANCE_WITH_HLINE = 3;

    private static final int MIN_CIRCLE_RADIUS = 2;

    private static final int FILL_COLOR_TOP = 0xae5ec5f9;
    private static final int FILL_COLOR_BOTTOM = 0x583d83fe;
    private static final int LINE_COLOR_TOP = 0xff5ec5f9;
    private static final int LINE_COLOR_BOTTOM = 0xff3d83fe;

    private Resources mResources;
    private Paint mFillPaint, mLinePaint;
    private Paint mTimeTextPaint, mPercentTextPaint;
    private Paint mHorizontalLinePaint, mVerticalLinePaint;
    private Paint mCirclePaint;

    private int mTotalWidth, mTotalHeight;
    private Shader mFillShader;
    private Shader mLineShader;

    private Path mPowerLinePath, mPowerFillPath;
    private Context mContext;
    private DrawFilter mDrawFilter;

    private ArrayList<PointF> mPointFs = new ArrayList<PointF>();

    private float mVerticalLinePerRealWidth;
    private int mHorizontalLineHeight;

    private int mTopDistance, mBottomDistance;
    private float mLeftDistance, mPathRightDistance, mCurveRightDistance;

    private int mTextSize;
    private float mRealCurveWidth;
    private int mRealCurveHeight;

    private int mCurveBottom;
    private int mPowerLineWidth;
    private int mPercentFixLine;
    private int mTimeVerticalLineHeight;

    private int mMinCircleRadius;
    private PointF mEndPointF;

    private boolean mHasDatas;
    private double[] mDataInfos;
    private EndInfo mEndInfo;
    private StringBuffer mTimeSB, mPercentSB;

    public SpeedChartView(Context context) {
        super(context);
        init(context);
    }

    public SpeedChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @SuppressLint("NewApi")
    private void init(Context context) {

        mContext = context;
        mResources = getResources();

        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                | Paint.FILTER_BITMAP_FLAG);

        mTopDistance = UiUtils.dip2Px(mContext, TOP_DISTANCE);
        mBottomDistance = UiUtils.dip2Px(mContext, BOTTOM_DISTANCE);
        mLeftDistance = UiUtils.dip2Px(mContext, LEFT_DISTANCE);
        mPathRightDistance = UiUtils.dip2Px(mContext, PATH_RIGHT_DISTANCE);
        mCurveRightDistance = UiUtils.dip2Px(mContext, CURVE_RIGHT_DISTANCE);

        mTextSize = UiUtils.dip2Px(mContext, TIME_TEXT_SIZE);
        mPowerLineWidth = UiUtils.dip2Px(mContext, TEMPERATION_LINE_WIDTH);

        mPercentFixLine = UiUtils.dip2Px(mContext, DRAW_PERCENT_DISTANCE_WITH_HLINE);
        mTimeVerticalLineHeight = UiUtils.dip2Px(mContext, TIME_VERTICAL_LINE_HEIGHT);

        mMinCircleRadius = UiUtils.dip2Px(mContext, MIN_CIRCLE_RADIUS);

        initPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(mDrawFilter);
        super.onDraw(canvas);
        if (!mHasDatas) {
            return;
        }
        if (mFillShader == null || mLineShader == null) {
            initShader();
            setShader();
        }

        if (mPowerFillPath == null || mPowerLinePath == null) {
            initPath(mPointFs);
        }

        drawHorizontalLine(canvas);
        drawVerticalLineAndTimeText(canvas);
        if (mPowerFillPath != null && mPowerLinePath != null) {
            drawTempCubicLine(canvas);
        }

        if (mEndPointF != null) {
            drawEndCircle(canvas);
        }

        drawPercentText(canvas);
    }

    private void drawPercentText(Canvas canvas) {

        int perPercent = TOTAL_PERCENT_FRACTION / (HORIZONTAL_LINE_COUNT - 1);
        for (int i = 0; i < HORIZONTAL_LINE_COUNT; i++) {
            int lineY = mCurveBottom - i * mHorizontalLineHeight;

            if (i == 0) {
                continue;
            }
            if (mPercentSB == null) {
                mPercentSB = new StringBuffer();
                mPercentSB.append(perPercent);
                mPercentSB.append("%");
            }
            int percent = perPercent * i;
            mPercentSB.replace(0, mPercentSB.length() - 1, String.valueOf(percent));
            canvas.drawText(mPercentSB.toString(), mLeftDistance
                    , lineY
                            - mPercentFixLine,
                    mPercentTextPaint);
        }

    }

    private void drawVerticalLineAndTimeText(Canvas canvas) {
        float left = 0, top = 0, bottom = 0;

        for (int i = 0; i <= TIME_LINE_COUNT; i++) {
            left = mLeftDistance + i * mVerticalLinePerRealWidth;
            log("draw vertical line i = " + i + " left = " + left);

            if (i == 0) {
                top = 0;
                bottom = mCurveBottom;
            } else if (i == 1) {
                top = mCurveBottom;
                bottom = mCurveBottom + mTimeVerticalLineHeight;
            }

            canvas.drawLine(left, top, left, bottom, mVerticalLinePaint);

            int xPosition = (int) (mLeftDistance + (float) i / TIME_LINE_COUNT
                    * mRealCurveWidth);
            if (mTimeSB == null) {
                mTimeSB = new StringBuffer();
                mTimeSB.append("00:00");
            }
            int timeCount = i * 4;
            if (timeCount < 10) {
                mTimeSB.replace(1, 2, String.valueOf(timeCount));
            } else {
                mTimeSB.replace(0, 2, String.valueOf(timeCount));
            }
            String formatTime = mTimeSB.toString();
            canvas.drawText(formatTime, xPosition, mTotalHeight - mBottomDistance / 4,
                    mTimeTextPaint);
        }
    }

    private void drawEndCircle(Canvas canvas) {
        canvas.drawCircle(mEndPointF.x, mEndPointF.y, mMinCircleRadius, mCirclePaint);
    }

    private void drawTempCubicLine(Canvas canvas) {

        int saveLayerCount = canvas.saveLayer(0, 0, mTotalWidth, mTotalHeight, mLinePaint,
                Canvas.ALL_SAVE_FLAG);
        canvas.drawPath(mPowerLinePath, mLinePaint);
        canvas.drawPath(mPowerFillPath, mFillPaint);
        canvas.restoreToCount(saveLayerCount);

    }

    private void drawHorizontalLine(Canvas canvas) {
        float rightWidth = mTotalWidth - mCurveRightDistance;
        for (int i = 0; i < HORIZONTAL_LINE_COUNT; i++) {
            int lineY = mCurveBottom - i * mHorizontalLineHeight;
            canvas.drawLine(mLeftDistance, lineY, rightWidth, lineY, mHorizontalLinePaint);

        }
    }

    private void initShader() {
        mFillShader = new LinearGradient(0, mTopDistance, 0, mCurveBottom,
                FILL_COLOR_TOP,
                FILL_COLOR_BOTTOM, TileMode.CLAMP);
        mLineShader = new LinearGradient(0, mTopDistance, 0, mCurveBottom,
                LINE_COLOR_TOP,
                LINE_COLOR_BOTTOM, TileMode.CLAMP);

    }

    private void setShader() {
        if (mFillPaint == null || mLinePaint == null) {
            initPaint();
        }
        mFillPaint.setShader(mFillShader);
        mLinePaint.setShader(mLineShader);
        mCirclePaint.setShader(mLineShader);
    }

    private void initPaint() {

        mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFillPaint.setDither(true);
        mFillPaint.setFilterBitmap(true);
        mFillPaint.setStyle(Style.FILL);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setDither(true);
        mLinePaint.setFilterBitmap(true);
        mLinePaint.setStyle(Style.STROKE);
        mLinePaint.setStrokeCap(Cap.ROUND);
        mLinePaint.setStrokeJoin(Join.ROUND);
        mLinePaint.setStrokeWidth(mPowerLineWidth);

        mTimeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimeTextPaint.setTextAlign(Align.CENTER);
        mTimeTextPaint.setTextSize(mTextSize);
        mTimeTextPaint.setColor(TIME_TEXT_COLOR);

        mPercentTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPercentTextPaint.setTextAlign(Align.LEFT);
        mPercentTextPaint.setTextSize(mTextSize);
        mPercentTextPaint.setColor(TIME_TEXT_COLOR);

        mHorizontalLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHorizontalLinePaint.setColor(HORIZONTAL_LINE_COLOR);
        mHorizontalLinePaint.setStyle(Style.STROKE);
        mHorizontalLinePaint.setStrokeWidth(HORIZONTAL_LINE_SIZE);

        mVerticalLinePaint = new Paint(mHorizontalLinePaint);
        mVerticalLinePaint.setStyle(Style.FILL_AND_STROKE);
        mVerticalLinePaint.setStrokeWidth(HORIZONTAL_LINE_SIZE);

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Style.FILL_AND_STROKE);
        mCirclePaint.setStrokeWidth(mMinCircleRadius);

    }

    public void setDatas(double[] dataInfo) {

        if (dataInfo == null || dataInfo.length == 0) {
            mHasDatas = false;
            return;
        }
        mDataInfos = dataInfo;
        generatePointDatas(mDataInfos);
        mEndInfo = getEndInfo(mDataInfos);
        initPath(mPointFs);
        mHasDatas = true;
        postInvalidate();
    }

    private EndInfo getEndInfo(double[] dataInfos) {
        if (dataInfos == null || dataInfos.length == 0) {
            return null;
        }
        EndInfo endInfo = null;
        for (int i = dataInfos.length - 1; i >= 0; i--) {
            if (dataInfos[i] == 0) {
                continue;
            } else {
                endInfo = new EndInfo();
                endInfo.endPosition = i;
                endInfo.endPercent = dataInfos[i];
                return endInfo;
            }
        }
        return endInfo;
    }

    private void generatePointDatas(double[] dataInfos) {

        if (dataInfos == null || dataInfos.length == 0) {
            return;
        }
        if (mTotalWidth == 0 || mTotalHeight == 0) {
            mTotalWidth = getWidth();
            mTotalHeight = getHeight();
        }

        int dataCount = dataInfos.length;
        mCurveBottom = mTotalHeight - mBottomDistance;
        mRealCurveWidth = mTotalWidth - mLeftDistance - mPathRightDistance;
        mRealCurveHeight = mCurveBottom - mTopDistance;
        mHorizontalLineHeight = mRealCurveHeight / (HORIZONTAL_LINE_COUNT - 1);
        mVerticalLinePerRealWidth = mRealCurveWidth / (TIME_LINE_COUNT);

        mPointFs.clear();
        for (int i = 0; i < dataCount; i++) {

            PointF pointF = new PointF();
            float realWidth = mRealCurveWidth * (float) (i)
                    / (dataCount - 1);
            pointF.x = mLeftDistance + realWidth;

            float currentPercentFraction = (float) dataInfos[i] / TOTAL_PERCENT_FRACTION;
            float curveYHeight = currentPercentFraction * mRealCurveHeight;
            float screenCurveHeight = mRealCurveHeight - curveYHeight;
            float curveYPosition = mTopDistance + screenCurveHeight;
            float curveMinPosition = mCurveBottom
                    - mPowerLineWidth;

            if (curveYPosition >= curveMinPosition) {
                curveYPosition = curveMinPosition;
            }

            log("generating  : time = " + i + "---x = " + pointF.x + "---y = " + pointF.y);
            pointF.y = curveYPosition;
            mPointFs.add(pointF);

        }

    }

    private void initPath(List<PointF> points) {

        if (points == null || points.size() == 0 || mEndInfo == null) {
            return;
        }

        mPowerLinePath = new Path();
        mPowerFillPath = new Path();
        mPowerLinePath.reset();
        mPowerFillPath.reset();

        PointF startP;
        PointF endP = null;
        PointF controlOne = new PointF();
        PointF controlTwo = new PointF();
        boolean hasStart = false;
        float startX = 0;

        for (int i = 0; i < mEndInfo.endPosition; i++) {
            float x = points.get(i).x;
            float y = points.get(i).y;

            if (!hasStart && (points.get(i).y + mPowerLineWidth) != mCurveBottom) {
                hasStart = true;
                startX = x;
                mPowerFillPath.moveTo(x, y);
                mPowerLinePath.moveTo(x, y);
            }
            if (!hasStart) {
                continue;
            }

            startP = points.get(i);
            endP = points.get(i + 1);

            float wt = (startP.x + endP.x) / 2;
            controlOne.y = startP.y;
            controlOne.x = wt;
            controlTwo.y = endP.y;
            controlTwo.x = wt;

            mPowerFillPath.cubicTo(controlOne.x, controlOne.y, controlTwo.x, controlTwo.y, endP.x,
                    endP.y);
            mPowerLinePath.cubicTo(controlOne.x, controlOne.y, controlTwo.x, controlTwo.y, endP.x,
                    endP.y);
            log("cubic to x = " + endP.x + "---y =" + endP.y);

        }
        if (endP == null) {
            return;
        }

        if (mEndPointF == null) {
            mEndPointF = new PointF();
        }
        mEndPointF.x = endP.x;
        mEndPointF.y = endP.y;
        mPowerFillPath.lineTo(endP.x, mCurveBottom);
        mPowerFillPath.lineTo(startX, mCurveBottom);
        mPowerFillPath.close();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTotalWidth = w;
        mTotalHeight = h;
        log("total width = " + mTotalWidth + "---total height = " + mTotalHeight);

    }

    private void log(String msg) {
        log(TAG, msg);
    }

    private void log(String tag, String msg) {
    }

    public boolean isHasDatas() {
        return mHasDatas;
    }
}
