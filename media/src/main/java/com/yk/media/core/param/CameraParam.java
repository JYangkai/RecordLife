package com.yk.media.core.param;

import com.yk.media.opengles.view.EGLSurfaceView;

import javax.microedition.khronos.egl.EGLContext;

public class CameraParam {
    public static final int FACING_FRONT = 0;
    public static final int FACING_BACK = 1;

    private EGLContext eglContext;
    private EGLSurfaceView.Renderer renderer;
    private int renderMode = -1;
    private int facing = -1;

    private CameraParam() {
    }

    public EGLContext getEglContext() {
        return eglContext;
    }

    public void setEglContext(EGLContext eglContext) {
        this.eglContext = eglContext;
    }

    public EGLSurfaceView.Renderer getRenderer() {
        return renderer;
    }

    public void setRenderer(EGLSurfaceView.Renderer renderer) {
        this.renderer = renderer;
    }

    public int getRenderMode() {
        return renderMode;
    }

    public void setRenderMode(int renderMode) {
        this.renderMode = renderMode;
    }

    public int getFacing() {
        return facing;
    }

    public void setFacing(int facing) {
        this.facing = facing;
    }

    public static class Builder {
        private CameraParam cameraParam;

        public Builder() {
            cameraParam = new CameraParam();
        }

        public Builder setEGLContext(EGLContext eglContext) {
            cameraParam.setEglContext(eglContext);
            return this;
        }

        public Builder setRenderer(EGLSurfaceView.Renderer renderer) {
            cameraParam.setRenderer(renderer);
            return this;
        }

        public Builder setRenderMode(int renderMode) {
            cameraParam.setRenderMode(renderMode);
            return this;
        }

        public Builder setFacing(int facing) {
            cameraParam.setFacing(facing);
            return this;
        }

        public CameraParam build() {
            return cameraParam;
        }
    }

    public boolean isEmpty() {
        return eglContext == null || renderer == null ||
                renderMode == -1 || facing == -1;
    }
}
