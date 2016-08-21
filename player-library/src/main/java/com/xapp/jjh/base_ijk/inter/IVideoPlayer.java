package com.xapp.jjh.base_ijk.inter;

import com.xapp.jjh.base_ijk.bean.VideoPlayingInfo;

/**
 * ------------------------------------
 * Created by Taurus on 2016/8/2.
 * ------------------------------------
 */
public interface IVideoPlayer {

    int STATUS_ERROR=-1;
    int STATUS_IDLE=0;
    int STATUS_LOADING=1;
    int STATUS_PLAYING=2;
    int STATUS_PAUSE=3;
    int STATUS_COMPLETED=4;
    int STATUS_STOP=5;

    void play(String url);
    void rePlay();
    void start();
    void seekTo(int msc);
    void resume();
    void pause();
    void stop();
    boolean isPlaying();
    void toggleAspectRatio();
    int getCurrentPosition();
    int getDuration();
    int getStatus();
    int getBufferPercentage();
    VideoPlayingInfo getVideoPlayingInfo();
    void onDestroy();
}
