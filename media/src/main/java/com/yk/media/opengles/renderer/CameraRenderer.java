package com.yk.media.opengles.renderer;

import android.content.Context;
import android.graphics.SurfaceTexture;

import com.yk.media.opengles.OnSurfaceTextureListener;
import com.yk.media.opengles.draw.Camera;
import com.yk.media.opengles.draw.CameraFbo;
import com.yk.media.opengles.view.EGLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraRenderer implements EGLSurfaceView.Renderer {
    private Context context;

    private CameraFbo cameraFbo;
    private Camera camera;

    public CameraRenderer(Context context) {
        this.context = context;
        cameraFbo = new CameraFbo();
        camera = new Camera();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        if (cameraFbo == null || camera == null) {
            return;
        }
        cameraFbo.init(context);
        camera.init(context);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (cameraFbo == null || camera == null) {
            return;
        }
        cameraFbo.setSize(width, height);
        cameraFbo.setCameraSize(cameraWidth, cameraHeight);
        camera.setSize(width, height);
        camera.setTextureId(cameraFbo.getFboTextureId());
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (cameraFbo == null || camera == null) {
            return;
        }
        cameraFbo.draw();
        camera.draw();
    }

    private int cameraWidth;
    private int cameraHeight;

    public void setCameraSize(int width, int height) {
        cameraWidth = width;
        cameraHeight = height;
    }

    public void setOnSurfaceTextureListener(OnSurfaceTextureListener onSurfaceTextureListener) {
        if (cameraFbo == null) {
            return;
        }
        cameraFbo.setOnSurfaceTextureListener(onSurfaceTextureListener);
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        if (cameraFbo == null) {
            return;
        }
        cameraFbo.setSurfaceTexture(surfaceTexture);
    }

    public int getTextureId() {
        if (cameraFbo == null) {
            return 0;
        }
        return cameraFbo.getFboTextureId();
    }

    public void openBeauty(boolean isOpenBeauty) {
        if (camera != null) {
            camera.openBeauty(isOpenBeauty);
        }
    }
}
