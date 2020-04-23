package com.yk.media.core;

import com.yk.media.core.bean.Section;

public interface OnRecordListener {
    void onBeginSection();

    void onEndSection(Section section);

    void onConcatStart();

    void onConcatEnd(String path);

    void onRecordError(String error);
}
