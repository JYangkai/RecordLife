package com.yk.media.core.bean;

public class Section {
    private String path;
    private boolean keepAudio = true;
    private boolean keepVideo = true;

    private Section() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isKeepAudio() {
        return keepAudio;
    }

    public void keepAudio(boolean keepAudio) {
        this.keepAudio = keepAudio;
    }

    public boolean isKeepVideo() {
        return keepVideo;
    }

    public void keepVideo(boolean keepVideo) {
        this.keepVideo = keepVideo;
    }

    public static class Builder {
        private Section section;

        public Builder() {
            section = new Section();
        }

        public Builder setPath(String path) {
            section.setPath(path);
            return this;
        }

        public Builder keepAudio(boolean keepAudio) {
            section.keepAudio(keepAudio);
            return this;
        }

        public Builder keepVideo(boolean keepVideo) {
            section.keepVideo(keepVideo);
            return this;
        }

        public Section build() {
            return section;
        }
    }
}
