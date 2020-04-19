package com.yk.media.core.param;

import android.text.TextUtils;

public class VideoEncodeParam {
    private int width = -1;
    private int height = -1;
    private int frameRate = -1;
    private int iFrameInterval = -1;
    private String mime;

    private VideoEncodeParam() {
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }

    public int getiFrameInterval() {
        return iFrameInterval;
    }

    public void setiFrameInterval(int iFrameInterval) {
        this.iFrameInterval = iFrameInterval;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public static class Builder {
        private VideoEncodeParam videoEncodeParam;

        public Builder() {
            videoEncodeParam = new VideoEncodeParam();
        }

        public Builder setSize(int width, int height) {
            videoEncodeParam.setWidth(width);
            videoEncodeParam.setHeight(height);
            return this;
        }

        public Builder setFrameRate(int frameRate) {
            videoEncodeParam.setFrameRate(frameRate);
            return this;
        }

        public Builder setIFrameInterval(int iFrameInterval) {
            videoEncodeParam.setiFrameInterval(iFrameInterval);
            return this;
        }

        public Builder setMime(String mime) {
            videoEncodeParam.setMime(mime);
            return this;
        }

        public VideoEncodeParam build() {
            return videoEncodeParam;
        }
    }

    public boolean isEmpty() {
        return width == -1 || height == -1 ||
                frameRate == -1 || iFrameInterval == -1 ||
                TextUtils.isEmpty(mime);
    }
}
