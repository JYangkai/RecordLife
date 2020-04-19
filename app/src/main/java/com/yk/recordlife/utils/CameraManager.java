package com.yk.recordlife.utils;

import android.util.Size;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraX;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.lifecycle.LifecycleOwner;

import com.yk.media.core.param.CameraParam;

public class CameraManager {
    private CameraX.LensFacing lensFacing = CameraX.LensFacing.BACK;
    private int mWidth;
    private int mHeight;

    private Preview preview;

    private PreviewConfig initPreviewConfig() {
        if (mWidth == 0 || mHeight == 0) {
            return new PreviewConfig.Builder()
                    .setLensFacing(lensFacing)
                    .build();
        } else {
            return new PreviewConfig.Builder()
                    .setTargetResolution(new Size(mWidth, mHeight))
                    .setLensFacing(lensFacing)
                    .build();
        }
    }

    private LifecycleOwner lifecycleOwner;
    private Preview.OnPreviewOutputUpdateListener newListener;

    public void openCamera(LifecycleOwner lifecycleOwner, Preview.OnPreviewOutputUpdateListener newListener) {
        this.lifecycleOwner = lifecycleOwner;
        this.newListener = newListener;
        closeCamera();
        PreviewConfig previewConfig = initPreviewConfig();
        preview = new Preview(previewConfig);
        preview.setOnPreviewOutputUpdateListener(new Preview.OnPreviewOutputUpdateListener() {
            @Override
            public void onUpdated(@NonNull Preview.PreviewOutput output) {
                mHeight = output.getTextureSize().getWidth();
                mWidth = output.getTextureSize().getHeight();
                newListener.onUpdated(output);
            }
        });
        CameraX.bindToLifecycle(lifecycleOwner, preview);
    }

    public void closeCamera() {
        if (preview == null) {
            return;
        }
        CameraX.unbind(preview);
        preview = null;
    }

    public void switchCamera() {
        if (lensFacing == CameraX.LensFacing.BACK) {
            lensFacing = CameraX.LensFacing.FRONT;
        } else {
            lensFacing = CameraX.LensFacing.BACK;
        }
        openCamera(lifecycleOwner, newListener);
    }

    public void setCameraSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public int getFacing() {
        if (lensFacing == CameraX.LensFacing.BACK) {
            return CameraParam.FACING_BACK;
        } else if (lensFacing == CameraX.LensFacing.FRONT) {
            return CameraParam.FACING_FRONT;
        }
        return -1;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }
}
