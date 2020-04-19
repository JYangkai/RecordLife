package com.yk.media.core;

public interface OnRecordListener {
    void onStartRecord();

    void onStopRecord(String path);

    void onRecordError(String error);
}
