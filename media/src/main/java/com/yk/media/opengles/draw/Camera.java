package com.yk.media.opengles.draw;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.yk.media.utils.OpenGLESUtils;
import com.yk.media.utils.ShaderCodeUtils;

import java.nio.FloatBuffer;

public class Camera {
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

    public void init(Context context) {
        vertexBuffer = OpenGLESUtils.createFloatBuffer(vertexData);
        coordinateBuffer = OpenGLESUtils.createFloatBuffer(coordinateData);

        String vertexCode = ShaderCodeUtils.generateShaderCode(context, "CameraVertex.glsl");
        String fragCode = ShaderCodeUtils.generateShaderCode(context, "CameraFrag.glsl");

        int vertexShader = OpenGLESUtils.loadShader(GLES20.GL_VERTEX_SHADER, vertexCode);
        int fragShader = OpenGLESUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, fragCode);

        program = OpenGLESUtils.linkProgram(vertexShader, fragShader);
    }

    private int textureId;

    public void setTextureId(int textureId) {
        this.textureId = textureId;
    }

    private int width;
    private int height;

    public void setSize(int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        this.width = width;
        this.height = height;
    }

    private int aPosLocation;
    private int aCoordLocation;
    private int uSamplerLocation;
    private int uStepLocation;
    private int uBeautyLevelLocation;

    public void draw() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glUseProgram(program);

        aPosLocation = GLES20.glGetAttribLocation(program, "aPos");
        aCoordLocation = GLES20.glGetAttribLocation(program, "aCoordinate");
        uSamplerLocation = GLES20.glGetUniformLocation(program, "uSampler");
        uStepLocation = GLES20.glGetUniformLocation(program, "uStep");
        uBeautyLevelLocation = GLES20.glGetUniformLocation(program, "uBeautyLevel");

        // 激活纹理单元
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(uSamplerLocation, 0);

        // 顶点坐标
        GLES20.glEnableVertexAttribArray(aPosLocation);
        GLES20.glVertexAttribPointer(aPosLocation, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        // 纹理坐标
        GLES20.glEnableVertexAttribArray(aCoordLocation);
        GLES20.glVertexAttribPointer(aCoordLocation, 2, GLES20.GL_FLOAT, false, 0, coordinateBuffer);

        // 传入宽高
        GLES20.glUniform2f(uStepLocation, (float) 2 / width, (float) 2 / height);

        // 是否开启美颜
        if (!isOpenBeauty) {
            beautyLevel = 1.0f;
        }
        if (isOpenBeauty && beautyLevel == 1.0f) {
            beautyLevel = 0.99f;
        }
        GLES20.glUniform1f(uBeautyLevelLocation, beautyLevel);

        // 绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        // 解除绑定
        GLES20.glDisableVertexAttribArray(aPosLocation);
        GLES20.glDisableVertexAttribArray(aCoordLocation);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    private boolean isOpenBeauty = false;

    public void openBeauty(boolean isOpenBeauty) {
        Log.i("JOJO", "camera openBeauty:" + isOpenBeauty);
        this.isOpenBeauty = isOpenBeauty;
    }

    private float beautyLevel = 1.0f;

    /**
     * 设置美颜等级
     *
     * @param level 0.33 - 0.99，值越小，美颜程度越大
     */
    public void setBeautyLevel(float level) {
        beautyLevel = level;
        if (beautyLevel > 0.99f) {
            beautyLevel = 0.99f;
        } else if (beautyLevel < 0.33f) {
            beautyLevel = 0.33f;
        }
    }
}
