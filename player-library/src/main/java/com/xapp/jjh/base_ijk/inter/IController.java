package com.xapp.jjh.base_ijk.inter;

import android.graphics.drawable.Drawable;

/**
 * Created by Taurus on 2016/8/29.
 */
public interface IController {
    void setGestureEnable(boolean enable);
    void setControllerEnable(boolean enable);
    void setTopStatusBarEnable(boolean enable);
    void setTapEnable(boolean enable);
    void setPlayControlState(boolean state);
    boolean isPlayControlShow();
    void setTopStatusBarState(boolean state);
    boolean isTopStatusBarShow();
    void setSystemTime();
    void setLoadingState(boolean state);
    void setPlayState(boolean isPlaying);
    void setLiveState(boolean state);
    void setPlayTime(long curr, long total);
    void setSeekBarEnable(boolean enable);
    void setSeekMax(int max);
    void setSeekProgress(int progress);
    void setSeekSecondProgress(int progress);
    void setSeekThumb(Drawable drawable);
    void leftVerticalSlide(float percent);
    void rightVerticalSlide(float percent);
    void horizontalSlide(float percent);
    void onGestureDoubleTap();
    void onGestureSingleTapUp();
}
