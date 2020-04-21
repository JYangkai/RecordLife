package com.yk.recordlife.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;

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

    public void scaleHeight(int height) {
        post(new Runnable() {
            @Override
            public void run() {
                startScaleHeightAnimator(height);
            }
        });
    }

    private ValueAnimator scaleHeightAnimator;

    private void startScaleHeightAnimator(int height) {
        stopScaleHeightAnimator();
        int currentHeight = getHeight();
        int targetHeight;
        if (height <= 0) {
            targetHeight = currentHeight / 3 * 2;
        } else {
            targetHeight = height;
        }
        scaleHeightAnimator = ValueAnimator.ofInt(currentHeight, targetHeight);
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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopScaleHeightAnimator();
    }
}
