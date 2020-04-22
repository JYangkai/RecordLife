package com.yk.recordlife.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatTextView;

public class RecordTextView extends AppCompatTextView {
    private Context context;

    public RecordTextView(Context context) {
        this(context, null);
    }

    public RecordTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void scaleHeight(int startHieght, int endHeight) {
        post(new Runnable() {
            @Override
            public void run() {
                startScaleHeightAnimator(startHieght, endHeight);
            }
        });
    }

    private ValueAnimator scaleHeightAnimator;

    private void startScaleHeightAnimator(int startHieght, int endHeight) {
        stopScaleHeightAnimator();
        scaleHeightAnimator = ValueAnimator.ofInt(startHieght, endHeight);
        scaleHeightAnimator.setDuration(500);
        scaleHeightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                setCurrentHeight(value);
            }
        });
        scaleHeightAnimator.start();
    }

    private void stopScaleHeightAnimator() {
        if (scaleHeightAnimator != null && scaleHeightAnimator.isRunning()) {
            scaleHeightAnimator.cancel();
            scaleHeightAnimator = null;
        }
    }

    private int height;

    private void setCurrentHeight(int height) {
        this.height = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (this.height == 0) {
            setMeasuredDimension(width, height);
        } else {
            setMeasuredDimension(width, this.height);
        }
    }

    public void autoScale() {
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        scaleHeight(screenHeight / 2, screenHeight / 5);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        autoScale();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopScaleHeightAnimator();
    }
}
