package com.yk.media.opengles.renderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.HandlerThread;

import com.yk.media.opengles.draw.Camera;
import com.yk.media.opengles.view.EGLSurfaceView;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class RecordRenderer implements EGLSurfaceView.Renderer {
    private Context context;
    private int textureId;

    private Camera camera;

    private int width;
    private int height;

    private List<Bitmap> bitmapList = new ArrayList<>();

    public RecordRenderer(Context context, int textureId) {
        this.context = context;
        this.textureId = textureId;
        camera = new Camera();
        startBackground();
        startGetBitmapTimer();
    }

    private Handler handler;
    private HandlerThread handlerThread;

    private void startBackground() {
        stopBackground();
        handlerThread = new HandlerThread("Bitmap_Process");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    private void stopBackground() {
        if (handlerThread != null) {
            handlerThread.quitSafely();
            handlerThread = null;
            handler = null;
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        camera.init(context);
        camera.setTextureId(textureId);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        camera.setSize(width, height);
        this.width = width;
        this.height = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        camera.draw();

        if (isGetBitmap) {
            isGetBitmap = false;
            ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * 4);
            GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer);
            handler.post(new BitmapProcess(buffer));
        }
    }

    public void release() {
        stopGetBitmapTimer();
        stopBackground();
    }

    public List<Bitmap> getBitmapList() {
        return bitmapList;
    }

    private static final long GET_BITMAP_TIME = 1000;

    private boolean isGetBitmap = false;

    private Timer getBitmapTimer;

    private void startGetBitmapTimer() {
        stopGetBitmapTimer();
        getBitmapTimer = new Timer();
        getBitmapTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                isGetBitmap = true;
            }
        }, GET_BITMAP_TIME);
    }

    private void stopGetBitmapTimer() {
        if (getBitmapTimer != null) {
            getBitmapTimer.cancel();
            getBitmapTimer = null;
        }
        isGetBitmap = false;
    }

    private class BitmapProcess implements Runnable {
        private ByteBuffer buffer;

        BitmapProcess(ByteBuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public void run() {
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);
            Bitmap convertBitmap = convert(bitmap);
            Bitmap scaleBitmap = scaleBitmap(convertBitmap, 10);
            bitmapList.add(scaleBitmap);
        }

        private Bitmap convert(Bitmap bitmap) {
            android.graphics.Matrix matrix = new android.graphics.Matrix();
            matrix.setRotate(180);
            matrix.setScale(1, -1);
            Bitmap convertBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
            bitmap.recycle();
            return convertBitmap;
        }

        private Bitmap scaleBitmap(Bitmap bitmap, int scale) {
            Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap,
                    bitmap.getWidth() / scale, bitmap.getHeight() / scale,
                    false);
            bitmap.recycle();
            return scaleBitmap;
        }
    }
}
