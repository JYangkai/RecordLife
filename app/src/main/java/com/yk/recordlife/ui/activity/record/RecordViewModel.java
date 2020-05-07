package com.yk.recordlife.ui.activity.record;

import android.app.Application;
import android.media.AudioFormat;
import android.media.MediaFormat;
import android.util.Log;

import androidx.annotation.NonNull;

import com.yk.media.core.MediaRecorder;
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
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLContext;

public class RecordViewModel extends BaseViewModel implements OnRecordListener {
    private static final String TAG = RecordViewModel.class.getSimpleName();

    private MediaRecorder mediaRecorder;

    private MicParam micParam;
    private CameraParam cameraParam;
    private AudioEncodeParam audioEncodeParam;
    private VideoEncodeParam videoEncodeParam;
    private RecordParam recordParam;

    private List<Section> sectionList = new ArrayList<>();

    private RecordRenderer recordRenderer;

    private boolean isOpenBeauty = false;

    public RecordViewModel(@NonNull Application application) {
        super(application);
    }

    void openBeauty(boolean isOpenBeauty) {
        this.isOpenBeauty = isOpenBeauty;
        if (recordRenderer != null) {
            recordRenderer.openBeauty(isOpenBeauty);
        }
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
        // 打开摄像头的时候变化
        if (cameraParam == null || cameraParam.isEmpty()) {
            cameraParam = new CameraParam.Builder()
                    .setFacing(facing)
                    .setRenderMode(EGLSurfaceView.RENDERMODE_CONTINUOUSLY)
                    .build();
        }
        cameraParam.setEglContext(eglContext);
        if (recordRenderer != null) {
            recordRenderer = null;
        }
        recordRenderer = new RecordRenderer(context, textureId);
        recordRenderer.openBeauty(isOpenBeauty);
        cameraParam.setRenderer(recordRenderer);

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

    boolean startRecord(int facing, EGLContext eglContext, int textureId,
                        int width, int height) {
        initRecordParam(facing, eglContext, textureId, width, height);
        return mediaRecorder.startRecord();
    }

    void stopRecord() {
        Log.i(TAG, "stop record");
        if (mediaRecorder != null) {
            mediaRecorder.stopRecord();
        }
    }

    void release() {
        stopRecord();
        if (mediaRecorder != null) {
            mediaRecorder.reset();
        }
        if (sectionList != null) {
            sectionList.clear();
            sectionList = null;
        }
    }

    List<Section> getSectionList() {
        return sectionList;
    }

    String getPath() {
        if (recordParam != null) {
            return recordParam.getPath();
        }
        return null;
    }

    //----------------------------------录制相关-------------------------------------------------

    @Override
    public void onRecordStart() {
        Log.i(TAG, "onRecordStart");
    }

    @Override
    public void onRecordTime(long time) {
        Log.i(TAG, "onRecordTime:" + time);
    }

    @Override
    public void onRecordStop(Section section) {
        Log.i(TAG, "onRecordStop:" + section.getPath() + " bitmap list size:");
        if (sectionList == null) {
            sectionList = new ArrayList<>();
        }
        sectionList.add(section);
    }

    @Override
    public void onRecordError(String error) {
        Log.i(TAG, "onRecordError:" + error);
    }
}
