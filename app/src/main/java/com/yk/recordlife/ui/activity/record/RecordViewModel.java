package com.yk.recordlife.ui.activity.record;

import android.app.Application;
import android.media.AudioFormat;
import android.media.MediaFormat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.yk.media.core.MediaRecorder;
import com.yk.media.core.OnConcatListener;
import com.yk.media.core.OnRecordListener;
import com.yk.media.core.bean.Section;
import com.yk.media.core.param.AudioEncodeParam;
import com.yk.media.core.param.CameraParam;
import com.yk.media.core.param.MicParam;
import com.yk.media.core.param.RecordParam;
import com.yk.media.core.param.VideoEncodeParam;
import com.yk.media.opengles.renderer.RecordRenderer;
import com.yk.media.opengles.view.EGLSurfaceView;
import com.yk.recordlife.ui.base.BaseViewModel;

import java.io.File;

import javax.microedition.khronos.egl.EGLContext;

public class RecordViewModel extends BaseViewModel implements OnRecordListener, OnConcatListener {
    private MediaRecorder mediaRecorder;

    private MicParam micParam;
    private CameraParam cameraParam;
    private AudioEncodeParam audioEncodeParam;
    private VideoEncodeParam videoEncodeParam;
    private RecordParam recordParam;

    MutableLiveData<String> recordResult = new MutableLiveData<>();

    public RecordViewModel(@NonNull Application application) {
        super(application);
    }

    private void initRecordParam(int facing, EGLContext eglContext, int textureId,
                                 int width, int height) {
        if (micParam == null || micParam.isEmpty()) {
            micParam = new MicParam.Builder()
                    .setAudioSource(android.media.MediaRecorder.AudioSource.MIC)
                    .setSampleRateInHz(44100)
                    .setChannelConfig(AudioFormat.CHANNEL_IN_STEREO)
                    .setAudioFormat(AudioFormat.ENCODING_PCM_16BIT)
                    .build();
        }

        // CameraParam的EGLContext和TextureId必须每次都传，因为EGL环境会在每次
        // 打开摄像头的时候
        if (cameraParam == null || cameraParam.isEmpty()) {
            cameraParam = new CameraParam.Builder()
                    .setFacing(facing)
                    .setRenderMode(EGLSurfaceView.RENDERMODE_CONTINUOUSLY)
                    .build();
        }
        cameraParam.setEglContext(eglContext);
        cameraParam.setRenderer(new RecordRenderer(context, textureId));

        if (audioEncodeParam == null || audioEncodeParam.isEmpty()) {
            audioEncodeParam = new AudioEncodeParam.Builder()
                    .setBitRate(96000)
                    .setMaxInputSize(4096)
                    .setMime(MediaFormat.MIMETYPE_AUDIO_AAC)
                    .build();
        }

        if (videoEncodeParam == null || videoEncodeParam.isEmpty()) {
            videoEncodeParam = new VideoEncodeParam.Builder()
                    .setFrameRate(30)
                    .setSize(width, height)
                    .setIFrameInterval(1)
                    .setMime(MediaFormat.MIMETYPE_VIDEO_AVC)
                    .build();
        }

        File file = new File(context.getExternalFilesDir(null), "mixer.mp4");
        if (file.exists()) {
            file.delete();
        }
        if (recordParam == null || recordParam.isEmpty()) {
            recordParam = new RecordParam.Builder()
                    .setMaxRecordDuration(30000)
                    .setPath(file.getPath())
                    .build();
        }

        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
        }
        mediaRecorder.setOnRecordListener(this);
        mediaRecorder.prepare(micParam, cameraParam, audioEncodeParam, videoEncodeParam, recordParam);
    }

    boolean beginSection(int facing, EGLContext eglContext, int textureId,
                         int width, int height) {
        endSection();
        initRecordParam(facing, eglContext, textureId, width, height);
        return mediaRecorder.beginSection();
    }

    void endSection() {
        if (mediaRecorder != null) {
            mediaRecorder.endSection();
        }
    }

    void startConcat() {
        if (mediaRecorder != null) {
            mediaRecorder.startConcat(this);
        }
    }

    //-----------------------------------录制相关-----------------------------------------------

    @Override
    public void onSectionStart() {
        Log.i("JOJO", "onSectionStart");
    }

    @Override
    public void onSectionStop(Section section) {
        Log.i("JOJO", "onSectionStop:" + section.getPath());
    }

    @Override
    public void onRecordStop(String path) {
        Log.i("JOJO", "onRecordStop:" + path);
    }

    @Override
    public void onRecordError(String error) {
        Log.i("JOJO", "onRecordError:" + error);
    }

    //-----------------------------------合成相关-----------------------------------------------

    @Override
    public void onConcatStart() {
        Log.i("JOJO", "onConcatStart");
    }

    @Override
    public void onConcatProgress(float progress) {
        Log.i("JOJO", "onConcatProgress:" + progress);
    }

    @Override
    public void onConcatStop() {
        Log.i("JOJO", "onConcatStop");
    }

    @Override
    public void onConcatComplete(String path) {
        Log.i("JOJO", "onConcatComplete:" + path);
        recordResult.postValue(path);
    }

    @Override
    public void onConcatError(String error) {
        Log.i("JOJO", "onConcatError:" + error);
    }
}
