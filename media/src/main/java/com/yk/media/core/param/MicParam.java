package com.yk.media.core.param;

public class MicParam {
    private int audioSource = -1;
    private int sampleRateInHz = -1;
    private int channelConfig = -1;
    private int audioFormat = -1;

    private MicParam() {
    }

    public int getAudioSource() {
        return audioSource;
    }

    public void setAudioSource(int audioSource) {
        this.audioSource = audioSource;
    }

    public int getSampleRateInHz() {
        return sampleRateInHz;
    }

    public void setSampleRateInHz(int sampleRateInHz) {
        this.sampleRateInHz = sampleRateInHz;
    }

    public int getChannelConfig() {
        return channelConfig;
    }

    public void setChannelConfig(int channelConfig) {
        this.channelConfig = channelConfig;
    }

    public int getAudioFormat() {
        return audioFormat;
    }

    public void setAudioFormat(int audioFormat) {
        this.audioFormat = audioFormat;
    }

    public static class Builder {
        private MicParam micParam;

        public Builder() {
            micParam = new MicParam();
        }

        public Builder setAudioSource(int audioSource) {
            micParam.setAudioSource(audioSource);
            return this;
        }

        public Builder setSampleRateInHz(int sampleRateInHz) {
            micParam.setSampleRateInHz(sampleRateInHz);
            return this;
        }

        public Builder setChannelConfig(int channelConfig) {
            micParam.setChannelConfig(channelConfig);
            return this;
        }

        public Builder setAudioFormat(int audioFormat) {
            micParam.setAudioFormat(audioFormat);
            return this;
        }

        public MicParam build() {
            return micParam;
        }
    }

    public boolean isEmpty() {
        return audioSource == -1 || sampleRateInHz == -1 ||
                channelConfig == -1 || audioFormat == -1;
    }
}
