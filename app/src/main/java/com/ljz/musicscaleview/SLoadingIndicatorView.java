package com.ljz.musicscaleview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Random;

public class SLoadingIndicatorView extends View {
    private static final String TAG = SLoadingIndicatorView.class.getSimpleName();

    public static final int DEFAULT_HEIGHT = 20;
    public static final int DEFAULT_WIDTH = 25;

    int mIndicatorColor;
    float one;
    float two;
    float three;
    float four;
    float[] scaleYFloats = new float[4];

    Paint mPaint;
    BaseIndicatorController mIndicatorController;

    private boolean mHasAnimation;

    public SLoadingIndicatorView(Context context) {
        super(context);
        init(null, 0);
    }

    public SLoadingIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public SLoadingIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SLoadingIndicatorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SLoadingIndicatorView);
        mIndicatorColor = a.getColor(R.styleable.SLoadingIndicatorView_s_indicator_color, Color.BLUE);
        one = a.getFloat(R.styleable.SLoadingIndicatorView_s_indicator_one, 3f);
        two = a.getFloat(R.styleable.SLoadingIndicatorView_s_indicator_two, 1f);
        three = a.getFloat(R.styleable.SLoadingIndicatorView_s_indicator_three, 3f);
        four = a.getFloat(R.styleable.SLoadingIndicatorView_s_indicator_four, 1f);
        a.recycle();
        scaleYFloats[0] = one;
        scaleYFloats[1] = two;
        scaleYFloats[2] = three;
        scaleYFloats[3] = four;
        mPaint = new Paint();
        mPaint.setColor(mIndicatorColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        applyIndicator();
    }

    private void applyIndicator() {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        this.measure(w, h);
        mIndicatorController = new LineScaleWaveIndicator();
        bringToFront();
        mIndicatorController.setTarget(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureDimension(dp2px(DEFAULT_WIDTH), widthMeasureSpec);
        int height = measureDimension(dp2px(DEFAULT_HEIGHT), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawIndicator(canvas, scaleYFloats);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!mHasAnimation) {
            mHasAnimation = true;
            applyAnimation();
        }
    }

    @Override
    public void setVisibility(int v) {
        if (getVisibility() != v) {
            super.setVisibility(v);
            if (v == GONE || v == INVISIBLE) {
                mIndicatorController.setAnimationStatus(BaseIndicatorController.AnimStatus.END);
            } else {
                mIndicatorController.setAnimationStatus(BaseIndicatorController.AnimStatus.START);
            }
        }
    }

    /**
     * onAttachedToWindow是在第一次onDraw前调用的。也就是我们写的View在没有绘制出来时调用的，但只会调用一次。
     * onDetachedFromWindow相反
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mHasAnimation) {
            mIndicatorController.setAnimationStatus(BaseIndicatorController.AnimStatus.START);
        }
    }

    /**
     * This is called when the view is detached from a window. At this point it no longer has a surface for drawing.
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIndicatorController.setAnimationStatus(BaseIndicatorController.AnimStatus.CANCEL);
    }

    void drawIndicator(Canvas canvas, float[] scaleYFloats) {
        mIndicatorController.draw(canvas, mPaint, scaleYFloats);
    }

    private int measureDimension(int defaultSize, int measureSpec) {
        int result = defaultSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(defaultSize, specSize);
        } else {
            result = defaultSize;
        }
        return result;
    }

    void applyAnimation() {
        mIndicatorController.initAnimation(scaleYFloats);
    }

    public void cancel() {
        mIndicatorController.setAnimationStatus(BaseIndicatorController.AnimStatus.CANCEL);
    }

    public void end() {
        mIndicatorController.setAnimationStatus(BaseIndicatorController.AnimStatus.END);
    }

    public void start() {
        mIndicatorController.setAnimationStatus(BaseIndicatorController.AnimStatus.START);
    }

    private int dp2px(int dpValue) {
        return (int) getContext().getResources().getDisplayMetrics().density * dpValue;
    }
}