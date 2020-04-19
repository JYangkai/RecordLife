package com.yk.recordlife.data.bean;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class Image implements Serializable {
    private String name;
    private String path;

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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Image) {
            Image image = (Image) obj;
            return image.path.equals(this.path);
        }
        return false;
    }
}
