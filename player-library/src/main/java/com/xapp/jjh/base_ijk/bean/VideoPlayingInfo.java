package com.xapp.jjh.base_ijk.bean;

import java.io.Serializable;

/**
 * ------------------------------------
 * Created by Taurus on 2016/8/3.
 * ------------------------------------
 */
public class VideoPlayingInfo implements Serializable {
    /**
     * 输出帧率
     */
    private float outputFramesPerSecond;
    /**
     * 解码帧率
     */
    private float decodeFramesPerSecond;
    /**
     * 视频已缓冲时间
     */
    private long videoCachedDuration;
    /**
     * 音频已缓冲时间
     */
    private long audioCachedDuration;
    /**
     * 视频已缓冲字节数
     */
    private long videoCachedBytes;
    /**
     * 音频已缓冲字节数
     */
    private long audioCachedBytes;

    public float getOutputFramesPerSecond() {
        return outputFramesPerSecond;
    }

    public void setOutputFramesPerSecond(float outputFramesPerSecond) {
        this.outputFramesPerSecond = outputFramesPerSecond;
    }

    public float getDecodeFramesPerSecond() {
        return decodeFramesPerSecond;
    }

    public void setDecodeFramesPerSecond(float decodeFramesPerSecond) {
        this.decodeFramesPerSecond = decodeFramesPerSecond;
    }

    public long getVideoCachedDuration() {
        return videoCachedDuration;
    }

    public void setVideoCachedDuration(long videoCachedDuration) {
        this.videoCachedDuration = videoCachedDuration;
    }

    public long getAudioCachedDuration() {
        return audioCachedDuration;
    }

    public void setAudioCachedDuration(long audioCachedDuration) {
        this.audioCachedDuration = audioCachedDuration;
    }

    public long getVideoCachedBytes() {
        return videoCachedBytes;
    }

    public void setVideoCachedBytes(long videoCachedBytes) {
        this.videoCachedBytes = videoCachedBytes;
    }

    public long getAudioCachedBytes() {
        return audioCachedBytes;
    }

    public void setAudioCachedBytes(long audioCachedBytes) {
        this.audioCachedBytes = audioCachedBytes;
    }
}
