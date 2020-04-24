package com.yk.recordlife.data.bean;

import android.graphics.Bitmap;

public class Frame {
    private Bitmap bitmap;
    private long time;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
