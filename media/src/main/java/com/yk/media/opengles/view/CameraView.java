package com.yk.media.opengles.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;

import com.yk.media.opengles.renderer.CameraRenderer;

public class CameraView extends EGLSurfaceView {
    public CameraView(Context context) {
        this(context, null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private CameraRenderer renderer;

    private void init(Context context) {
        setEGLContextClientVersion(2);
        renderer = new CameraRenderer(context);
        setRenderer(renderer);
        setRenderMode(EGLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void setCameraSize(int width, int height) {
        setRatioSize(width, height);
        if (renderer == null) {
            return;
        }
        renderer.setCameraSize(width, height);
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        if (renderer == null) {
            return;
        }
        surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                requestRender();
            }
        });
        renderer.setSurfaceTexture(surfaceTexture);
    }

    public int getTextureId() {
        if (renderer == null) {
            return 0;
        }
        return renderer.getTextureId();
    }

    private int finalWidth = 0;
    private int finalHeight = 0;

    public int getFinalWidth() {
        return finalWidth;
    }

    public int getFinalHeight() {
        return finalHeight;
    }

    private int ratioWidth = 0;
    private int ratioHeight = 0;

    public void setRatioSize(int width, int height) {
        ratioWidth = width;
        ratioHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (ratioWidth == 0 || ratioHeight == 0) {
            finalWidth = width;
            finalHeight = height;
            setMeasuredDimension(width, height);
            return;
        }
        if (width < height * ratioWidth / ratioHeight) {
            finalWidth = width;
            finalHeight = width * ratioHeight / ratioWidth;
        } else {
            finalWidth = height * ratioWidth / ratioHeight;
            finalHeight = height;
        }
        setMeasuredDimension(finalWidth, finalHeight);
    }
}
