package com.ljz.musicscaleview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

public class LineScaleWaveIndicator extends BaseIndicatorController {

    public static final float SCALE = 1.0f;

    float[] scaleYFloats = new float[]{SCALE,
            SCALE,
            SCALE,
            SCALE};

    @Override
    public void draw(Canvas canvas, Paint paint, float[] scales) {
        float translateX = getWidth() / 14;
        float translateY = getHeight();
        for (int i = 0; i < 4; i++) {
            canvas.save();
            canvas.translate(((i + 1) * 2) * translateX + translateX * i, translateY);
            canvas.scale(SCALE, scaleYFloats[i]);
            RectF rectF;
            if (scales[i] == 1) {
                rectF = new RectF(-translateX, -getHeight() , 0, 0);
            } else if (scales[i] == 2) {
                rectF = new RectF(-translateX, -getHeight()*2/3 , 0, 0);
            } else if (scales[i] == 3) {
                rectF = new RectF(-translateX, -getHeight()/3 , 0, 0);
            } else {
                rectF = new RectF(-translateX, -getHeight() , 0, 0);
            }
            canvas.drawRoundRect(rectF, 6, 6, paint);
            canvas.restore();
        }
    }

    @Override
    public List<Animator> createAnimation(float[] scales) {
        List<Animator> animators = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            final int index = i;
            ValueAnimator scaleAnim;
            if (scales[i] == 1) {
                scaleAnim = ValueAnimator.ofFloat(1, 0.333f, 1);
            } else if (scales[i] == 2) {
                scaleAnim = ValueAnimator.ofFloat(1, 0.5f, 1.5f, 1);
            } else if (scales[i] == 3) {
                scaleAnim = ValueAnimator.ofFloat(1, 3f, 1);
            } else {
                scaleAnim = ValueAnimator.ofFloat(1, 0.333f, 1);
            }
            scaleAnim.setDuration(900);
            scaleAnim.setRepeatCount(-1);
            scaleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    scaleYFloats[index] = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            scaleAnim.start();
            animators.add(scaleAnim);
        }
        return animators;
    }

}