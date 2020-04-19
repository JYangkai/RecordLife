package com.yk.recordlife.data.bean;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class Video implements Serializable {
    private String name;
    private String path;
    private int width;
    private int height;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Video) {
            Video video = (Video) obj;
            return video.path.equals(this.path);
        }
        return false;
    }
}
