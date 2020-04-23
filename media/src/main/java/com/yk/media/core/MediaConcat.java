package com.yk.media.core;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.text.TextUtils;

import com.yk.media.core.bean.Section;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public class MediaConcat {
    private String concatPath;

    private List<Section> sectionList;

    private ConcatThread concatThread;

    public MediaConcat(List<Section> sectionList, String concatPath) {
        this.sectionList = sectionList;
        this.concatPath = concatPath;
    }

    public void startConcat() {
        stopConcat();
        concatThread = new ConcatThread();
        concatThread.init();
        concatThread.start();
    }

    public void stopConcat() {
        if (concatThread != null) {
            concatThread.stopConcat();
            concatThread = null;
        }
    }

    private class ConcatThread extends Thread {
        private MediaMuxer mediaMuxer;

        private MediaFormat audioFormat;
        private MediaFormat videoFormat;

        private int audioTrack;
        private int videoTrack;

        void init() {
            // 第一步，获取MediaFormat
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
                int audioTrackIndex = getTrackIndex(extractor, "audio/");
                if (audioTrackIndex != -1) {
                    audioFormat = extractor.getTrackFormat(audioTrackIndex);
                }

                // 获取视频轨道
                int videoTrackIndex = getTrackIndex(extractor, "video/");
                if (videoTrackIndex != -1) {
                    videoFormat = extractor.getTrackFormat(videoTrackIndex);
                }

                extractor.release();
            }

            if (audioFormat == null || videoFormat == null) {
                return;
            }

            // 第二步，初始化MediaMuxer
            try {
                mediaMuxer = new MediaMuxer(concatPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            } catch (IOException e) {
                e.printStackTrace();
                mediaMuxer = null;
            }

            if (mediaMuxer == null) {
                return;
            }

            audioTrack = mediaMuxer.addTrack(audioFormat);
            videoTrack = mediaMuxer.addTrack(videoFormat);
        }

        void stopConcat() {
            if (mediaMuxer==null) {
                return;
            }
            mediaMuxer.stop();
            mediaMuxer.release();
        }

        @Override
        public void run() {
            if (mediaMuxer == null) {
                return;
            }
            mediaMuxer.start();

            long audioPts = 0;
            long videoPts = 0;

            for (Section section : sectionList) {
                String path = section.getPath();

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
            }
            stopConcat();
        }

        private int getTrackIndex(MediaExtractor extractor, String mime) {
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
}
