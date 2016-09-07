package com.xapp.jjh.base_ijk.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.xapp.jjh.base_ijk.config.DecodeMode;
import com.xapp.jjh.base_ijk.config.ViewType;
import com.xapp.jjh.base_ijk.inter.OnErrorListener;
import com.xapp.jjh.base_ijk.inter.OnPlayerEventListener;

/**
 * Created by Taurus on 2016/8/30.
 */
public abstract class BasePlayer extends BaseBindControllerPlayer {

    private DecodeMode mDecodeMode = DecodeMode.SOFT;
    private ViewType mViewType = ViewType.SURFACEVIEW;

    protected OnPlayerEventListener mOnPlayerEventListener;
    protected OnErrorListener mOnErrorListener;

    protected int startSeekPos = -1;

    protected boolean isLive = false;

    public BasePlayer(Context context) {
        super(context);
    }

    public BasePlayer(Context context, int width, int height) {
        super(context, width, height);
    }

    public BasePlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BasePlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BasePlayer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public DecodeMode getDecodeMode() {
        return mDecodeMode;
    }

    public void setDecodeMode(DecodeMode mDecodeMode) {
        this.mDecodeMode = mDecodeMode;
    }

    public ViewType getViewType() {
        return mViewType;
    }

    public void setViewType(ViewType mViewType) {
        this.mViewType = mViewType;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
        setLiveState(isLive);
    }

    public void setOnPlayerEventListener(OnPlayerEventListener onPlayerEventListener) {
        this.mOnPlayerEventListener = onPlayerEventListener;
    }

    public void setOnErrorListener(OnErrorListener onErrorListener) {
        this.mOnErrorListener = onErrorListener;
    }

    protected void onPlayerEvent(int eventCode){
        updateController(eventCode);
        if(mOnPlayerEventListener!=null){
            mOnPlayerEventListener.onPlayerEvent(eventCode);
        }
    }

    private void updateController(int eventCode) {
        switch (eventCode){
            case OnPlayerEventListener.EVENT_CODE_ON_INTENT_TO_START:
                setLoadingState(true);
                break;

            case OnPlayerEventListener.EVENT_CODE_PREPARED:

                break;

            case OnPlayerEventListener.EVENT_CODE_VIDEO_INFO_READY:

                break;

            case OnPlayerEventListener.EVENT_CODE_RENDER_START:
                handleSome();
                setLoadingState(false);
                setPlayState(true);
                break;

            case OnPlayerEventListener.EVENT_CODE_BUFFERING_START:
                setLoadingState(true);
                break;

            case OnPlayerEventListener.EVENT_CODE_BUFFERING_END:
                setLoadingState(false);
                break;

            case OnPlayerEventListener.EVENT_CODE_SEEK_COMPLETE:
                setLoadingState(false);
                break;

            case OnPlayerEventListener.EVENT_CODE_PLAY_COMPLETE:
                setLoadingState(false);
                break;

            case OnPlayerEventListener.EVENT_CODE_PLAY_PAUSE:
                setPlayState(false);
                break;

            case OnPlayerEventListener.EVENT_CODE_PLAY_RESUME:
                setPlayState(true);
                break;
        }
    }

    private void handleSome() {
        setSeekBarEnable(!(getDuration() <= 0));
        sendPlayingMsg();
        if(startSeekPos > 0){
            seekTo(startSeekPos);
            startSeekPos = -1;
        }
    }

    protected void onErrorEvent(int errorCode){
        handleErrorEvent(errorCode);
        if(mOnErrorListener!=null){
            mOnErrorListener.onError(errorCode);
        }
    }

    private void handleErrorEvent(int errorCode) {
        switch (errorCode){
            case OnErrorListener.ERROR_CODE_COMMON:
                setPlayState(false);
                setLoadingState(false);
                break;
        }
    }

    @Override
    public void horizontalSlide(float percent) {
        if(getDuration()<=0)
            return;
        super.horizontalSlide(percent);
    }

    @Override
    public void destroy() {
        super.destroy();
        onPlayerEvent(OnPlayerEventListener.EVENT_CODE_PLAYER_DESTROY);
    }
}
