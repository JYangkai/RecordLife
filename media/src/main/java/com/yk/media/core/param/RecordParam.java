package com.yk.media.core.param;

import android.text.TextUtils;

public class RecordParam {
    private long maxRecordDuration = -1;
    private String path;

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

        public RecordParam build() {
            return recordParam;
        }
    }

    public boolean isEmpty() {
        return maxRecordDuration == -1 || TextUtils.isEmpty(path);
    }
}
