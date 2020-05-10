package com.yk.recordlife.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MultiProgressBar extends View {
    private static final int INIT_BACK_COLOR = Color.WHITE; // 初始背景颜色
    private static final int INIT_PROGRESS_COLOR = Color.RED; // 初始进度条颜色

    // 进度条颜色数组
    private int[] colors = {
            Color.RED, Color.GREEN, Color.BLUE
    };

    // 当前进度条颜色索引
    private int nextColorIndex = 1;

    private Paint backPaint; // 背景画笔
    private Paint progressPaint; // 进度条画笔

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

    // 初始化
    private void init() {
        if (backPaint == null) {
            backPaint = new Paint();
        }
        backPaint.setAntiAlias(true);
        backPaint.setColor(INIT_BACK_COLOR);
        backPaint.setStyle(Paint.Style.FILL);

        if (progressPaint == null) {
            progressPaint = new Paint();
        }
        progressPaint.setAntiAlias(true);
        progressPaint.setColor(INIT_PROGRESS_COLOR);
        progressPaint.setStyle(Paint.Style.FILL);

        if (multiColorList != null) {
            multiColorList.clear();
        } else {
            multiColorList = new ArrayList<>();
        }

        MultiColor multiColor = new MultiColor();
        multiColor.setStart(0);
        multiColor.setColor(INIT_PROGRESS_COLOR);
        multiColorList.add(multiColor);

        nextColorIndex = 1;
    }

    // 最大进度
    private int maxProgress = 100;

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    // 当前进度
    private int mProgress = 0;

    public void setProgress(int progress) {
        mProgress = progress;
        if (mProgress >= maxProgress) {
            mProgress = maxProgress;
        }
        if (mProgress == 0) {
            init();
        }
        invalidate();
    }

    // 存储多段多颜色进度
    private List<MultiColor> multiColorList = new ArrayList<>();

    public void marker() {
        int size = multiColorList.size();
        if (size == 0) {
            return;
        }
        if (mProgress == 0) {
            return;
        }
        multiColorList.get(size - 1).setEnd(mProgress);

        MultiColor multiColor = new MultiColor();
        multiColor.setStart(mProgress);
        if (nextColorIndex == colors.length) {
            nextColorIndex = 0;
        }
        multiColor.setColor(colors[nextColorIndex++]);
        multiColorList.add(multiColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        // 画背景
        canvas.drawRect(0, 0, width, height, backPaint);

        if (mProgress == 0 || multiColorList.size() == 0) {
            return;
        }

        // 画进度条
        for (MultiColor multiColor : multiColorList) {
            progressPaint.setColor(multiColor.getColor());
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
