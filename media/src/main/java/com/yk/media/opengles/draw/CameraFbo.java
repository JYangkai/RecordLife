package com.yk.media.opengles.draw;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.yk.media.opengles.OnSurfaceTextureListener;
import com.yk.media.utils.OpenGLESUtils;
import com.yk.media.utils.ShaderCodeUtils;

import java.nio.FloatBuffer;

public class CameraFbo {
    private static final float[] vertexData = {
            -1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, 1.0f,
            1.0f, -1.0f
    };

    private static final float[] coordinateData = {
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            1.0f, 0.0f
    };

    private FloatBuffer vertexBuffer, coordinateBuffer;

    private int program;

    private int textureId;
    private int fboTextureId;
    private int fboId;

    public void init(Context context) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        vertexBuffer = OpenGLESUtils.createFloatBuffer(vertexData);
        coordinateBuffer = OpenGLESUtils.createFloatBuffer(coordinateData);
        textureId = OpenGLESUtils.createVideoTexture();

        String vertexCode = ShaderCodeUtils.generateShaderCode(context, "CameraFboVertex.glsl");
        String fragCode = ShaderCodeUtils.generateShaderCode(context, "CameraFboFrag.glsl");

        int vertexShader = OpenGLESUtils.loadShader(GLES20.GL_VERTEX_SHADER, vertexCode);
        int fragShader = OpenGLESUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, fragCode);

        program = OpenGLESUtils.linkProgram(vertexShader, fragShader);
    }

    private int width;
    private int height;

    public void setSize(int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        this.width = width;
        this.height = height;

        int[] fboData = OpenGLESUtils.createFBO(width, height);
        fboId = fboData[0];
        fboTextureId = fboData[1];
    }

    private OnSurfaceTextureListener onSurfaceTextureListener;

    public void setOnSurfaceTextureListener(OnSurfaceTextureListener onSurfaceTextureListener) {
        isReady = false;
        this.onSurfaceTextureListener = onSurfaceTextureListener;
    }

    private SurfaceTexture surfaceTexture;

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        isReady = false;
        this.surfaceTexture = surfaceTexture;
    }

    public void setCameraSize(int width, int height) {
        if (this.width == 0 || this.height == 0 || width == 0 || height == 0) {
            return;
        }
        mMVPMatrix = OpenGLESUtils.getMatrix(this.width, this.height, width, height);
    }

    private float[] mMVPMatrix = new float[16];
    private float[] mCameraMatrix = new float[16];

    private int aPosLocation;
    private int aCoordinateLocation;
    private int uMatrixLocation;
    private int uCameraMatrixLocation;
    private int uSamplerLocation;

    private boolean isReady = false;

    public void draw() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        if (!isReady) {
            if (onSurfaceTextureListener != null) {
                if (surfaceTexture != null) {
                    surfaceTexture.release();
                    surfaceTexture = null;
                }
                surfaceTexture = new SurfaceTexture(textureId);
                onSurfaceTextureListener.onSurfaceTexture(surfaceTexture);
                isReady = true;
            } else if (surfaceTexture != null) {
                surfaceTexture.attachToGLContext(textureId);
                isReady = true;
            } else {
                return;
            }
        }
        if (mMVPMatrix.length == 0 || surfaceTexture == null) {
            return;
        }
        surfaceTexture.updateTexImage();
        surfaceTexture.getTransformMatrix(mCameraMatrix);

        GLES20.glUseProgram(program);

        aPosLocation = GLES20.glGetAttribLocation(program, "aPos");
        aCoordinateLocation = GLES20.glGetAttribLocation(program, "aCoordinate");
        uMatrixLocation = GLES20.glGetUniformLocation(program, "uMatrix");
        uCameraMatrixLocation = GLES20.glGetUniformLocation(program, "uCameraMatrix");
        uSamplerLocation = GLES20.glGetUniformLocation(program, "uSampler");

        // 绑定FBO
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId);

        // 激活纹理单元
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        GLES20.glUniform1i(uSamplerLocation, 0);

        GLES20.glEnableVertexAttribArray(aPosLocation);
        GLES20.glVertexAttribPointer(aPosLocation, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        GLES20.glEnableVertexAttribArray(aCoordinateLocation);
        GLES20.glVertexAttribPointer(aCoordinateLocation, 2, GLES20.GL_FLOAT, false, 0, coordinateBuffer);

        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(uCameraMatrixLocation, 1, false, mCameraMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(aPosLocation);
        GLES20.glDisableVertexAttribArray(aCoordinateLocation);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public int getFboTextureId() {
        return fboTextureId;
    }
}
