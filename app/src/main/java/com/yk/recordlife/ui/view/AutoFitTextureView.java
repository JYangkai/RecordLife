package com.yk.recordlife.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

public class AutoFitTextureView extends TextureView {
    public AutoFitTextureView(Context context) {
        super(context);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int finalWidth = 0;
    private int finalHeight = 0;

    public int getFinalWidth() {
        return finalWidth;
    }

    public int getFinalHeight() {
        return finalHeight;
    }

    private int ratioWidth = 0;
    private int ratioHeight = 0;

    public void setRatioSize(int width, int height) {
        ratioWidth = width;
        ratioHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (ratioWidth == 0 || ratioHeight == 0) {
            finalWidth = width;
            finalHeight = height;
            setMeasuredDimension(width, height);
            return;
        }
        if (width < height * ratioWidth / ratioHeight) {
            finalWidth = width;
            finalHeight = width * ratioHeight / ratioWidth;
        } else {
            finalWidth = height * ratioWidth / ratioHeight;
            finalHeight = height;
        }
        setMeasuredDimension(finalWidth, finalHeight);
    }
}
