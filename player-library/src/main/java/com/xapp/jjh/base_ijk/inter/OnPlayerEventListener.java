package com.xapp.jjh.base_ijk.inter;

/**
 * Created by Taurus on 2016/8/30.
 */
public interface OnPlayerEventListener {
    int EVENT_CODE_ON_INTENT_TO_START = 10000;
    int EVENT_CODE_PREPARED = 20000;
    int EVENT_CODE_VIDEO_INFO_READY = 20001;
    int EVENT_CODE_RENDER_START = 20002;
    int EVENT_CODE_BUFFERING_START = 20003;
    int EVENT_CODE_BUFFERING_END = 20004;
    int EVENT_CODE_SEEK_COMPLETE = 20005;
    int EVENT_CODE_PLAY_COMPLETE = 20006;
    int EVENT_CODE_PLAY_PAUSE = 20007;
    int EVENT_CODE_PLAY_RESUME = 20008;
    int EVENT_CODE_PLAYER_DESTROY = 20009;

    void onPlayerEvent(int eventCode);
}
