package com.yk.media.opengles.renderer;

import android.content.Context;

import com.yk.media.opengles.draw.Camera;
import com.yk.media.opengles.view.EGLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class RecordRenderer implements EGLSurfaceView.Renderer {
    private Context context;
    private int textureId;

    private Camera camera;

    public RecordRenderer(Context context, int textureId) {
        this.context = context;
        this.textureId = textureId;
        camera = new Camera();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        camera.init(context);
        camera.setTextureId(textureId);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        camera.setSize(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        camera.draw();
    }
}
