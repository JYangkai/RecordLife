package com.yk.media.core;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.text.TextUtils;
import android.util.Log;

import com.yk.media.core.bean.Section;
import com.yk.media.utils.FileUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public class MediaConcat {
    private static final String TAG = MediaConcat.class.getSimpleName();

    // 合成的录制
    private String path;
    // 所有片段
    private List<Section> sectionList;
    // 合成线程
    private ConcatThread concatThread;

    /**
     * 开始合成
     */
    public void startConcat(String path, List<Section> sectionList) {
        stopConcat();
        Log.i(TAG, "startConcat:" + path + " sectionList:" + sectionList);
        this.path = path;
        this.sectionList = sectionList;
        concatThread = new ConcatThread();
        concatThread.start();
    }

    /**
     * 停止合成
     */
    public void stopConcat() {
        Log.i(TAG, "release");
        if (concatThread != null) {
            concatThread.stopConcat();
            concatThread = null;
        }
        deleteAllSections();
    }

    /**
     * 删除所有片段
     */
    private void deleteAllSections() {
        Log.i(TAG, "delete all sections");
        if (sectionList == null || sectionList.size() == 0) {
            Log.i(TAG, "section list is empty");
            return;
        }
        for (Section section : sectionList) {
            FileUtils.deleteFile(section.getPath());
        }
        sectionList.clear();
        sectionList = null;
    }

    /**
     * 合成线程
     */
    private class ConcatThread extends Thread {
        private MediaMuxer mediaMuxer;

        private int audioTrack = -1;
        private int videoTrack = -1;

        /**
         * release
         */
        void stopConcat() {
            Log.i(TAG, "concat thread release");
            if (mediaMuxer == null) {
                Log.i(TAG, "concat thread mediaMuxer is null");
                return;
            }
            mediaMuxer.stop();
            mediaMuxer.release();
            mediaMuxer = null;

            deleteAllSections();

            if (onConcatListener != null) {
                onConcatListener.onConcatComplete(path);
            }
        }

        /**
         * 初始化
         */
        private void init() {
            Log.i(TAG, "concat thread init");
            if (sectionList == null || sectionList.size() == 0) {
                Log.i(TAG, "concat thread sectionList is empty");
                if (onConcatListener != null) {
                    onConcatListener.onConcatError("sectionList is empty");
                }
                return;
            }
            // 第一步，获取MediaFormat
            MediaFormat audioFormat = null;
            MediaFormat videoFormat = null;
            for (Section section : sectionList) {
                if (audioFormat != null && videoFormat != null) {
                    break;
                }
                MediaExtractor extractor;
                String path = section.getPath();
                try {
                    extractor = new MediaExtractor();
                    extractor.setDataSource(path);
                } catch (IOException e) {
                    e.printStackTrace();
                    extractor = null;
                }
                if (extractor == null) {
                    continue;
                }

                // 获取音频轨道
                if (audioFormat == null) {
                    int audioTrackIndex = getTrackIndex(extractor, "audio/");
                    if (audioTrackIndex != -1) {
                        audioFormat = extractor.getTrackFormat(audioTrackIndex);
                    }
                }

                // 获取视频轨道
                if (videoFormat == null) {
                    int videoTrackIndex = getTrackIndex(extractor, "video/");
                    if (videoTrackIndex != -1) {
                        videoFormat = extractor.getTrackFormat(videoTrackIndex);
                    }
                }

                extractor.release();
            }

            if (audioFormat == null || videoFormat == null) {
                Log.i(TAG, "concat thread audioFormat or videoFormat is null");
                if (onConcatListener != null) {
                    onConcatListener.onConcatError("audioFormat or videoFormat is null");
                }
                return;
            }

            // 第二步，初始化MediaMuxer
            if (TextUtils.isEmpty(path)) {
                Log.i(TAG, "concat thread path is empty");
                if (onConcatListener != null) {
                    onConcatListener.onConcatError("path is empty");
                }
                return;
            }
            try {
                path = FileUtils.getFilePath(path);
                mediaMuxer = new MediaMuxer(path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            } catch (IOException e) {
                e.printStackTrace();
                mediaMuxer = null;
            }

            if (mediaMuxer == null) {
                Log.i(TAG, "concat thread mediaMuxer is null");
                if (onConcatListener != null) {
                    onConcatListener.onConcatError("mediaMuxer is null");
                }
                return;
            }

            audioTrack = mediaMuxer.addTrack(audioFormat);
            videoTrack = mediaMuxer.addTrack(videoFormat);
        }

        @Override
        public void run() {
            if (sectionList != null && sectionList.size() == 1) {
                Log.i(TAG, "concat thread sectionList size is 1");
                if (onConcatListener != null) {
                    onConcatListener.onConcatProgress(1);
                    onConcatListener.onConcatComplete(sectionList.get(0).getPath());
                }
                return;
            }

            init();

            if (checkState()) {
                Log.i(TAG, "concat thread concat is not ready");
                if (onConcatListener != null) {
                    onConcatListener.onConcatError("concat is not ready");
                }
                return;
            }

            if (onConcatListener != null) {
                onConcatListener.onConcatStart();
            }

            mediaMuxer.start();

            long audioPts = 0;
            long videoPts = 0;

            int size = sectionList.size();

            for (int i = 0; i < size; i++) {
                Section section = sectionList.get(i);
                String path = section.getPath();
                Log.i(TAG, "concat thread write data:" + path + " index:" + i);

                // 获取音频轨道
                MediaExtractor audioExtractor;
                try {
                    audioExtractor = new MediaExtractor();
                    audioExtractor.setDataSource(path);
                } catch (IOException e) {
                    e.printStackTrace();
                    audioExtractor = null;
                }
                if (audioExtractor != null) {
                    int audioTrackIndex = getTrackIndex(audioExtractor, "audio/");
                    if (audioTrackIndex != -1) {
                        audioExtractor.selectTrack(audioTrackIndex);
                        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
                        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
                        while (true) {
                            int sampleSize = audioExtractor.readSampleData(buffer, 0);
                            if (sampleSize < 0) {
                                break;
                            }
                            info.offset = 0;
                            info.size = sampleSize;
                            info.presentationTimeUs = audioPts + audioExtractor.getSampleTime();
                            if ((audioExtractor.getSampleFlags() & MediaCodec.BUFFER_FLAG_KEY_FRAME) != 0) {
                                info.flags = MediaCodec.BUFFER_FLAG_KEY_FRAME;
                            }
                            buffer.rewind();
                            mediaMuxer.writeSampleData(audioTrack, buffer, info);
                            audioExtractor.advance();
                        }
                        audioPts = info.presentationTimeUs;
                        audioExtractor.release();
                    }
                }

                // 获取视频轨道
                MediaExtractor videoExtractor;
                try {
                    videoExtractor = new MediaExtractor();
                    videoExtractor.setDataSource(path);
                } catch (IOException e) {
                    e.printStackTrace();
                    videoExtractor = null;
                }
                if (videoExtractor != null) {
                    int videoTrackIndex = getTrackIndex(videoExtractor, "video/");
                    if (videoTrackIndex != -1) {
                        videoExtractor.selectTrack(videoTrackIndex);
                        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
                        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
                        while (true) {
                            int sampleSize = videoExtractor.readSampleData(buffer, 0);
                            if (sampleSize < 0) {
                                break;
                            }
                            info.offset = 0;
                            info.size = sampleSize;
                            info.presentationTimeUs = videoPts + videoExtractor.getSampleTime();
                            if ((videoExtractor.getSampleFlags() & MediaCodec.BUFFER_FLAG_KEY_FRAME) != 0) {
                                info.flags = MediaCodec.BUFFER_FLAG_KEY_FRAME;
                            }
                            buffer.rewind();
                            mediaMuxer.writeSampleData(videoTrack, buffer, info);
                            videoExtractor.advance();
                        }
                        videoPts = info.presentationTimeUs;
                        videoExtractor.release();
                    }
                }
                if (onConcatListener != null) {
                    onConcatListener.onConcatProgress((float) (i + 1) / size);
                }
            }
            stopConcat();
        }

        private boolean checkState() {
            return mediaMuxer == null || audioTrack == -1 || videoTrack == -1 ||
                    sectionList == null || sectionList.size() == 0;
        }

        private int getTrackIndex(MediaExtractor extractor, String mime) {
            Log.i(TAG, "get track index:" + mime);
            if (extractor == null || TextUtils.isEmpty(mime)) {
                return -1;
            }
            int trackCount = extractor.getTrackCount();
            for (int i = 0; i < trackCount; i++) {
                MediaFormat format = extractor.getTrackFormat(i);
                if (format.getString(MediaFormat.KEY_MIME).startsWith(mime)) {
                    return i;
                }
            }
            return -1;
        }
    }

    private OnConcatListener onConcatListener;

    public void setOnConcatListener(OnConcatListener onConcatListener) {
        this.onConcatListener = onConcatListener;
    }
}
