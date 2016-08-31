package com.xapp.jjh.base_ijk.inter;

import android.content.res.Configuration;

/**
 * Created by Taurus on 2016/8/29.
 */
public interface IPlayer {
    /** 设置播放资源*/
    void setData(String... data);
    /** 开始播放*/
    void start();
    /** 指定时刻开始播放*/
    void start(int msc);
    /** 暂停*/
    void pause();
    /** 恢复播放*/
    void resume();
    /** 定位播放*/
    void seekTo(int msc);
    /** 停止播放*/
    void stop();
    /** 是否播放中*/
    boolean isPlaying();
    /** 获取当前播放时刻*/
    int getCurrentPosition();
    /** 获取资源总时长*/
    int getDuration();
    /** 获取缓冲进度*/
    int getBufferPercentage();
    /** 页面状态变化*/
    void doConfigChange(Configuration newConfig);
    /** 切换全屏装态*/
    void toggleFullScreen();
    /** 是否处于全屏*/
    boolean isFullScreen();
    /** 销毁播放器*/
    void destroy();
}
