package com.yk.media.core;

public interface OnConcatListener {
    void onConcatStart();

    void onConcatProgress(float progress);

    void onConcatStop();

    void onConcatComplete(String path);

    void onConcatError(String error);
}
