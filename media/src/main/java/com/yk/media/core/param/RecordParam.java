package com.yk.media.core.param;

import android.text.TextUtils;

public class RecordParam {
    private long maxRecordDuration = -1;
    private String path;
    private String concatPath;
    private boolean keepAudio = true;
    private boolean keepVideo = true;

    private RecordParam() {
    }

    public long getMaxRecordDuration() {
        return maxRecordDuration;
    }

    public void setMaxRecordDuration(long maxRecordDuration) {
        this.maxRecordDuration = maxRecordDuration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getConcatPath() {
        return concatPath;
    }

    public void setConcatPath(String concatPath) {
        this.concatPath = concatPath;
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
        private RecordParam recordParam;

        public Builder() {
            recordParam = new RecordParam();
        }

        public Builder setMaxRecordDuration(int maxRecordDuration) {
            recordParam.setMaxRecordDuration(maxRecordDuration);
            return this;
        }

        public Builder setPath(String path) {
            recordParam.setPath(path);
            return this;
        }

        public Builder setConcatPath(String concatPath) {
            recordParam.setConcatPath(concatPath);
            return this;
        }

        public Builder keepAudio(boolean keepAudio) {
            recordParam.keepAudio(keepAudio);
            return this;
        }

        public Builder keepVideo(boolean keepVideo) {
            recordParam.keepVideo(keepVideo);
            return this;
        }

        public RecordParam build() {
            return recordParam;
        }
    }

    public boolean isEmpty() {
        return maxRecordDuration == -1 || TextUtils.isEmpty(path);
    }
}
