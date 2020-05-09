package com.yk.recordlife.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.yk.recordlife.R;

import java.util.ArrayList;
import java.util.List;

public class MultiProgressBar extends View {
    private static final int INIT_BACK_COLOR = R.color.colorWhite;
    private static final int INIT_PROGRESS_COLOR = R.color.colorPrimary;

    private Paint backPaint;
    private Paint progressPaint;

    public MultiProgressBar(Context context) {
        this(context, null);
    }

    public MultiProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        backPaint = new Paint();
        backPaint.setAntiAlias(true);
        backPaint.setColor(getResources().getColor(INIT_BACK_COLOR));
        backPaint.setStyle(Paint.Style.FILL);

        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setColor(getResources().getColor(INIT_PROGRESS_COLOR));
        progressPaint.setStyle(Paint.Style.FILL);

        MultiColor multiColor = new MultiColor();
        multiColor.setStart(0);
        multiColor.setColor(INIT_PROGRESS_COLOR);
        multiColorList.add(multiColor);
    }

    private int maxProgress = 100;

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    private int mProgress = 0;

    public void setProgress(int progress) {
        mProgress = progress;
        if (mProgress >= maxProgress) {
            mProgress = maxProgress;
        }
        invalidate();
    }

    private List<MultiColor> multiColorList = new ArrayList<>();

    public void setMultiColor(int color) {
        Log.i("JOJO", "setMultiColor");
        int size = multiColorList.size();
        if (mProgress == 0 && size == 1) {
            multiColorList.get(0).setColor(color);
            return;
        }

        MultiColor multiColor = new MultiColor();
        if (size == 0) {
            multiColor.setStart(0);
        } else {
            Log.i("JOJO", "setMultiColor setEnd:" + mProgress);
            multiColorList.get(size - 1).setEnd(mProgress);
            multiColor.setStart(mProgress);
        }
        multiColor.setColor(color);
        multiColorList.add(multiColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        // 画背景
        canvas.drawRect(0, 0, width, height, backPaint);

        // 画进度条
        if (multiColorList.size() == 0) {
            canvas.drawRect(0, 0, width * mProgress / maxProgress, height, progressPaint);
        } else {
            for (MultiColor multiColor : multiColorList) {
                progressPaint.setColor(getResources().getColor(multiColor.getColor()));
                int start = width * multiColor.getStart() / maxProgress;
                int end;
                if (multiColor.getEnd() == 0) {
                    end = width * mProgress / maxProgress;
                } else {
                    end = width * multiColor.getEnd() / maxProgress;
                }
                canvas.drawRect(start, 0, end, height, progressPaint);
            }
        }
    }

    private class MultiColor {
        private int start;
        private int end = 0;
        private int color;

        int getStart() {
            return start;
        }

        void setStart(int start) {
            this.start = start;
        }

        int getEnd() {
            return end;
        }

        void setEnd(int end) {
            this.end = end;
        }

        int getColor() {
            return color;
        }

        void setColor(int color) {
            this.color = color;
        }
    }
}
