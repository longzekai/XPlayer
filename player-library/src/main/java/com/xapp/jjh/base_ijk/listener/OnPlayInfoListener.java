package com.xapp.jjh.base_ijk.listener;

/**
 * ------------------------------------
 * Created by Taurus on 2016/8/2.
 * ------------------------------------
 */
public interface OnPlayInfoListener {
    void onPlayerInfo(int what, int extra);
    void onRenderStart();
    void onBufferingStart();
    void onBufferingEnd();
}
