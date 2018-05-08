package com.ljz.musicscaleview;

import android.animation.Animator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.List;


public abstract class BaseIndicatorController {

    private WeakReference<View> mTarget;

    private List<Animator> mAnimators;

    public void setTarget(View target) {
        this.mTarget = new WeakReference<>(target);
    }

    public View getTarget() {
        return mTarget != null ? mTarget.get() : null;
    }


    public int getWidth() {
        return getTarget() != null ? getTarget().getWidth() : 0;
    }

    public int getHeight() {
        return getTarget() != null ? getTarget().getHeight() : 0;
    }

    public void postInvalidate() {
        if (getTarget() != null) {
            getTarget().postInvalidate();//刷新界面
        }
    }

    public abstract void draw(Canvas canvas, Paint paint, float[] scaleYFloats);

    public abstract List<Animator> createAnimation(float[] scales);

    public void initAnimation(float[] scales ) {
        mAnimators = createAnimation(scales);
    }

    public void setAnimationStatus(AnimStatus animStatus) {
        if (mAnimators == null) {
            return;
        }
        int count = mAnimators.size();
        for (int i = 0; i < count; i++) {
            Animator animator = mAnimators.get(i);
            boolean isRunning = animator.isRunning();
            switch (animStatus) {
                case START:
                    if (!isRunning) {
                        animator.start();
                    }
                    break;
                case END: //恢复初始位置
                    if (isRunning) {
                        animator.end();
                    }
                    break;
                case CANCEL: //正常停止
                    if (isRunning) {
                        animator.cancel();
                    }
                    break;
                default:
                    if (isRunning) {
                        animator.end();
                    }
                    break;
            }
        }
    }


    public enum AnimStatus {
        START, END, CANCEL
    }


}