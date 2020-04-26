package com.yk.media.core;

import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Build;
import android.text.TextUtils;
import android.view.Surface;

import com.yk.media.core.bean.Section;
import com.yk.media.core.param.AudioEncodeParam;
import com.yk.media.core.param.CameraParam;
import com.yk.media.core.param.MicParam;
import com.yk.media.core.param.RecordParam;
import com.yk.media.core.param.VideoEncodeParam;
import com.yk.media.opengles.egl.EglHelper;
import com.yk.media.opengles.view.EGLSurfaceView;
import com.yk.media.utils.FileUtils;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLContext;

public class MediaRecorder {
    private MicParam micParam;
    private CameraParam cameraParam;

    private AudioEncodeParam audioEncodeParam;
    private VideoEncodeParam videoEncodeParam;

    private RecordParam recordParam;

    private Surface surface;

    private int recordIndex = 0;

    public void prepare(MicParam micParam, CameraParam cameraParam,
                        AudioEncodeParam audioEncodeParam, VideoEncodeParam videoEncodeParam,
                        RecordParam recordParam) {
        this.micParam = micParam;
        this.cameraParam = cameraParam;
        this.audioEncodeParam = audioEncodeParam;
        this.videoEncodeParam = videoEncodeParam;
        this.recordParam = recordParam;
    }

    /**
     * 检查参数是否为空
     */
    private boolean checkState() {
        return micParam.isEmpty() || cameraParam.isEmpty() ||
                audioEncodeParam.isEmpty() || videoEncodeParam.isEmpty() ||
                recordParam.isEmpty();
    }

    private MediaThread mediaThread;
    private GLThread glThread;

    /**
     * 开始录制
     *
     * @return 启动成功
     */
    public boolean startRecord() {
        if (checkState()) {
            if (onRecordListener != null) {

            return false;
        }
        stopRecord();

        // 初始化MediaThread
        mediaThread = new MediaThread();

        // 初始化GLThread
        glThread = new GLThread();
        glThread.isCreate = true;
        glThread.isChange = true;

        // 启动线程
        mediaThread.start();
        glThread.start();

        return true;
    }

    /**
     * 停止录制
     */
    public void stopRecord() {
        if (mediaThread != null) {
            mediaThread.stopRecord();
            mediaThread = null;
        }
        if (glThread != null) {
            glThread.onDestroy();
            glThread = null;
        }
    }

    private class MediaThread extends Thread {
        private MediaMuxer mediaMuxer;

        private MediaCodec audioCodec;
        private MediaCodec videoCodec;

        private AudioRecord audioRecord;

        private int bufferSizeInBytes;

        private String path;

        private void init() {
            initMuxer();
            initAudio();
            initVideo();
        }

        private void initMuxer() {
            try {
                this.path = FileUtils.getRecordFilePath(recordParam.getPath(), recordIndex++);
                if (TextUtils.isEmpty(path)) {
                    return;
                }
                mediaMuxer = new MediaMuxer(this.path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            } catch (IOException e) {
                e.printStackTrace();
                mediaMuxer = null;
            }
        }

        private void initAudio() {
            bufferSizeInBytes = AudioRecord.getMinBufferSize(micParam.getSampleRateInHz(),
                    micParam.getChannelConfig(), micParam.getAudioFormat());
            audioRecord = new AudioRecord(micParam.getAudioSource(), micParam.getSampleRateInHz(),
                    micParam.getChannelConfig(), micParam.getAudioFormat(), bufferSizeInBytes);
            try {
                audioCodec = MediaCodec.createEncoderByType(audioEncodeParam.getMime());
                MediaFormat format = MediaFormat.createAudioFormat(audioEncodeParam.getMime(), micParam.getSampleRateInHz(), 2);
                format.setInteger(MediaFormat.KEY_BIT_RATE, audioEncodeParam.getBitRate());
                format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
                format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, audioEncodeParam.getMaxInputSize());
                audioCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            } catch (IOException e) {
                e.printStackTrace();
                audioRecord = null;
                audioCodec = null;
            }
        }

        private void initVideo() {
            try {
                videoCodec = MediaCodec.createEncoderByType(videoEncodeParam.getMime());
                MediaFormat format = MediaFormat.createVideoFormat(videoEncodeParam.getMime(),
                        videoEncodeParam.getWidth(), videoEncodeParam.getHeight());
                format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
                format.setInteger(MediaFormat.KEY_FRAME_RATE, videoEncodeParam.getFrameRate());
                format.setInteger(MediaFormat.KEY_BIT_RATE,
                        videoEncodeParam.getWidth() * videoEncodeParam.getHeight() * 4);
                format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, videoEncodeParam.getiFrameInterval());

                //设置压缩等级  默认是baseline
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    format.setInteger(MediaFormat.KEY_PROFILE, MediaCodecInfo.CodecProfileLevel.AVCProfileMain);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        format.setInteger(MediaFormat.KEY_LEVEL, MediaCodecInfo.CodecProfileLevel.AVCLevel3);
                    }
                }

                videoCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
                surface = videoCodec.createInputSurface();
            } catch (IOException e) {
                e.printStackTrace();
                videoCodec = null;
                surface = null;
            }
        }

        private boolean isStopRecord;

        void stopRecord() {
            isStopRecord = true;
        }

        @Override
        public void run() {
            init();
            boolean isStartMuxer = false;
            isStopRecord = false;
            long audioPts = 0;
            long videoPts = 0;
            int audioTrackIndex = -1;
            int videoTrackIndex = -1;
            if (checkState()) {
                if (onRecordListener != null) {
                    onRecordListener.onRecordError("录制组件初始化失败");
                }
                return;
            }

            if (onRecordListener != null) {
                onRecordListener.onRecordStart();
            }

            audioRecord.startRecording();
            audioCodec.start();
            videoCodec.start();
            MediaCodec.BufferInfo audioInfo = new MediaCodec.BufferInfo();
            MediaCodec.BufferInfo videoInfo = new MediaCodec.BufferInfo();
            while (true) {
                if (isStopRecord) {
                    release();
                    break;
                }

                // 将AudioRecord录制的PCM原始数据送入编码器
                int audioInputBufferId = audioCodec.dequeueInputBuffer(0);
                if (audioInputBufferId >= 0) {
                    ByteBuffer inputBuffer = audioCodec.getInputBuffer(audioInputBufferId);
                    int readSize = -1;
                    if (inputBuffer != null) {
                        readSize = audioRecord.read(inputBuffer, bufferSizeInBytes);
                    }
                    if (readSize >= 0) {
                        audioCodec.queueInputBuffer(audioInputBufferId, 0, readSize, System.nanoTime() / 1000, 0);
                    } else {
                        continue;
                    }
                }

                // 获取从surface直接编码得到的数据，写入Muxer
                int videoOutputBufferId = videoCodec.dequeueOutputBuffer(videoInfo, 0);
                if (videoOutputBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    videoTrackIndex = mediaMuxer.addTrack(videoCodec.getOutputFormat());
                    if (audioTrackIndex != -1 && !isStartMuxer) {
                        isStartMuxer = true;
                        mediaMuxer.start();
                    }
                } else if (videoOutputBufferId >= 0) {
                    ByteBuffer outputBuffer = videoCodec.getOutputBuffer(videoOutputBufferId);
                    if (outputBuffer != null && videoInfo.size != 0 && isStartMuxer) {
                        outputBuffer.position(videoInfo.offset);
                        outputBuffer.limit(videoInfo.offset + videoInfo.size);
                        if (videoPts == 0) {
                            videoPts = videoInfo.presentationTimeUs;
                        }
                        videoInfo.presentationTimeUs = videoInfo.presentationTimeUs - videoPts;
                        mediaMuxer.writeSampleData(videoTrackIndex, outputBuffer, videoInfo);

                        // 以视频为准，回调当前录制时间
                        if (onRecordListener != null) {
                            onRecordListener.onRecordTime(videoInfo.presentationTimeUs / 1000);
                        }
                    }
                    videoCodec.releaseOutputBuffer(videoOutputBufferId, false);
                }

                // 获取音频编码数据，写入Muxer
                int audioOutputBufferId = audioCodec.dequeueOutputBuffer(audioInfo, 0);
                if (audioOutputBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    audioTrackIndex = mediaMuxer.addTrack(audioCodec.getOutputFormat());
                    if (videoTrackIndex != -1 && !isStartMuxer) {
                        isStartMuxer = true;
                        mediaMuxer.start();
                    }
                } else if (audioOutputBufferId >= 0) {
                    ByteBuffer outputBuffer = audioCodec.getOutputBuffer(audioOutputBufferId);
                    if (outputBuffer != null && audioInfo.size != 0 && isStartMuxer) {
                        outputBuffer.position(audioInfo.offset);
                        outputBuffer.limit(audioInfo.offset + audioInfo.size);
                        if (audioPts == 0) {
                            audioPts = audioInfo.presentationTimeUs;
                        }
                        audioInfo.presentationTimeUs = audioInfo.presentationTimeUs - audioPts;
                        mediaMuxer.writeSampleData(audioTrackIndex, outputBuffer, audioInfo);
                    }
                    audioCodec.releaseOutputBuffer(audioOutputBufferId, false);
                }
            }
        }

        private boolean checkState() {
            return audioRecord == null || surface == null ||
                    audioCodec == null || videoCodec == null;
        }

        private void release() {
            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
            }

            if (audioCodec != null) {
                audioCodec.stop();
                audioCodec.release();
                audioCodec = null;
            }

            if (videoCodec != null) {
                videoCodec.stop();
                videoCodec.release();
                videoCodec = null;
            }

            if (mediaMuxer != null) {
                mediaMuxer.stop();
                mediaMuxer.release();
                mediaMuxer = null;
            }

            // 保存片段
            Section section = new Section.Builder()
                    .setPath(path)
                    .keepAudio(recordParam.isKeepAudio())
                    .keepVideo(recordParam.isKeepVideo())
                    .build();
            if (onRecordListener != null) {
                onRecordListener.onRecordStop(section);
            }
        }
    }

    private class GLThread extends Thread {
        private EglHelper eglHelper;

        private boolean isCreate;
        private boolean isChange;
        private boolean isStart;
        private boolean isExit;

        private Object object;

        @Override
        public void run() {
            if (checkState()) {
                return;
            }
            try {
                guardedRun();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private boolean checkState() {
            return surface == null;
        }

        private void guardedRun() throws InterruptedException {
            isExit = false;
            isStart = false;
            object = new Object();
            eglHelper = new EglHelper();
            eglHelper.init(cameraParam.getEglContext(), surface);

            while (true) {
                if (isExit) {
                    release();
                    break;
                }
                if (isStart) {
                    if (cameraParam.getRenderMode() == EGLSurfaceView.RENDERMODE_WHEN_DIRTY) {
                        synchronized (object) {
                            object.wait();
                        }
                    } else if (cameraParam.getRenderMode() == EGLSurfaceView.RENDERMODE_CONTINUOUSLY) {
                        Thread.sleep(1000 / 60);
                    } else {
                        throw new IllegalArgumentException("render mode error");
                    }
                }

                onCreate();
                onChange(videoEncodeParam.getWidth(), videoEncodeParam.getHeight());
                onDrawFrame();
                isStart = true;
            }
        }

        private void onCreate() {
            if (!isCreate || cameraParam.getRenderer() == null) {
                return;
            }
            isCreate = false;
            cameraParam.getRenderer().onSurfaceCreated(null, null);
        }

        private void onChange(int width, int height) {
            if (!isChange || cameraParam.getRenderer() == null) {
                return;
            }
            isChange = false;
            cameraParam.getRenderer().onSurfaceChanged(null, width, height);
        }

        private void onDrawFrame() {
            if (cameraParam.getRenderer() == null) {
                return;
            }
            cameraParam.getRenderer().onDrawFrame(null);
            if (!isStart) {
                cameraParam.getRenderer().onDrawFrame(null);
            }
            eglHelper.swapBuffers();
        }

        void requestRender() {
            if (object != null) {
                synchronized (object) {
                    object.notifyAll();
                }
            }
        }

        void onDestroy() {
            isExit = true;
            requestRender();
        }

        void release() {
            if (eglHelper != null) {
                eglHelper.destoryEgl();
                eglHelper = null;
                object = null;
            }
        }

        EGLContext getEGLContext() {
            if (eglHelper != null) {
                return eglHelper.getEglContext();
            }
            return null;
        }
    }

    public MicParam getMicParam() {
        return micParam;
    }

    public void setMicParam(MicParam micParam) {
        this.micParam = micParam;
    }

    public CameraParam getCameraParam() {
        return cameraParam;
    }

    public void setCameraParam(CameraParam cameraParam) {
        this.cameraParam = cameraParam;
    }

    public AudioEncodeParam getAudioEncodeParam() {
        return audioEncodeParam;
    }

    public void setAudioEncodeParam(AudioEncodeParam audioEncodeParam) {
        this.audioEncodeParam = audioEncodeParam;
    }

    public VideoEncodeParam getVideoEncodeParam() {
        return videoEncodeParam;
    }

    public void setVideoEncodeParam(VideoEncodeParam videoEncodeParam) {
        this.videoEncodeParam = videoEncodeParam;
    }

    public RecordParam getRecordParam() {
        return recordParam;
    }

    public void setRecordParam(RecordParam recordParam) {
        this.recordParam = recordParam;
    }

    private OnRecordListener onRecordListener;

    public void setOnRecordListener(OnRecordListener onRecordListener) {
        this.onRecordListener = onRecordListener;
    }
}
