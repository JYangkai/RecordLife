package com.yk.media.core;

import com.yk.media.core.bean.Section;

public interface OnRecordListener {
    void onRecordStart();

    void onRecordTime(long time);

    void onRecordStop(Section section);

    void onRecordError(String error);
}
