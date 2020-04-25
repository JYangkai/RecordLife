package com.yk.media.core;

import com.yk.media.core.bean.Section;

public interface OnRecordListener {
    void onSectionStart();

    void onSectionStop(Section section);

    void onRecordStop(String path);

    void onRecordError(String error);
}
