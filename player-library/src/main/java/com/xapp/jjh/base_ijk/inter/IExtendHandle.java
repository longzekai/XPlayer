package com.xapp.jjh.base_ijk.inter;

import com.xapp.jjh.base_ijk.listener.OnSlideHandleListener;

/**
 * ------------------------------------
 * Created by Taurus on 2016/8/10.
 * ------------------------------------
 */
public interface IExtendHandle extends IVideoPlayer{
    void setSlideChangeLightEnable(boolean enable);
    void setSlideChangeSoundEnable(boolean enable);
    void setSlideSeekEnable(boolean enable);
    void setOnSlideHandleListener(OnSlideHandleListener onSlideHandleListener);
}
