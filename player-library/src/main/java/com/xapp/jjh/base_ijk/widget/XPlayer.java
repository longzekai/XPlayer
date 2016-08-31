package com.xapp.jjh.base_ijk.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import com.xapp.jjh.base_ijk.config.DecodeMode;
import com.xapp.jjh.base_ijk.config.ViewType;
import com.xapp.jjh.base_ijk.ijk.IjkVideoView;
import com.xapp.jjh.base_ijk.inter.OnPlayerEventListener;
import com.xapp.jjh.base_ijk.inter.OnErrorListener;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.MediaPlayerProxy;

/**
 * ------------------------------------
 * Created by Taurus on 2016/8/2.
 * ------------------------------------
 */
public class XPlayer extends BasePlayer{

    private final String TAG = "IjkVideoPlayer";
    protected IjkVideoView mVideoView;
    private IjkMediaPlayer mMediaPlayer;
    private boolean hasLoadLibrary;

    private final int STATUS_IDLE = 0;
    private final int STATUS_PLAYING = 1;
    private final int STATUS_PAUSE = 2;
    private int mStatus = STATUS_IDLE;
    private TableLayout tableLayout;

    public XPlayer(Context context) {
        super(context);
    }

    public XPlayer(Context context, int width, int height) {
        super(context, width, height);
    }

    public XPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public XPlayer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void initPlayerWidget(Context context) {
        loadLibrary();
        mVideoView = new IjkVideoView(context);
        mVideoView.setBackgroundColor(Color.BLACK);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        addView(mVideoView,params);
        initPlayerListener();
    }

    private void loadLibrary() {
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
            hasLoadLibrary = true;
        } catch (Throwable e) {
            Log.e(TAG, "loadLibraries error", e);
        }
    }

    private void initPlayerListener() {
        mVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                onPlayerEvent(OnPlayerEventListener.EVENT_CODE_PLAY_COMPLETE);
            }
        });
        mVideoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                Log.d(TAG,"onInfo : what = " + what);
                switch (what) {
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        onPlayerEvent(OnPlayerEventListener.EVENT_CODE_BUFFERING_START);
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        onPlayerEvent(OnPlayerEventListener.EVENT_CODE_BUFFERING_END);
                        break;
                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        onPlayerEvent(OnPlayerEventListener.EVENT_CODE_RENDER_START);
                        break;
                }
                return false;
            }
        });
        mVideoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer mp, int what, int extra) {
                onErrorEvent(OnErrorListener.ERROR_CODE_COMMON);
                return false;
            }
        });
        mVideoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                preparedMediaPlayer(mp);
                onPlayerEvent(OnPlayerEventListener.EVENT_CODE_PREPARED);
            }
        });
    }

    private void preparedMediaPlayer(IMediaPlayer mediaPlayer) {
        if (mediaPlayer == null)
            return;
        if (mediaPlayer instanceof IjkMediaPlayer) {
            mMediaPlayer = (IjkMediaPlayer) mediaPlayer;
        } else if (mediaPlayer instanceof MediaPlayerProxy) {
            MediaPlayerProxy proxy = (MediaPlayerProxy) mediaPlayer;
            IMediaPlayer internal = proxy.getInternalMediaPlayer();
            if (internal != null && internal instanceof IjkMediaPlayer){
                mMediaPlayer = (IjkMediaPlayer) internal;
            }
        }
        mediaPlayer.setOnSeekCompleteListener(new IMediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(IMediaPlayer mp) {
                onPlayerEvent(OnPlayerEventListener.EVENT_CODE_SEEK_COMPLETE);
            }
        });
    }

    @Override
    public void setDecodeMode(DecodeMode mDecodeMode) {
        super.setDecodeMode(mDecodeMode);
        updateVideoViewDecodeMode();
    }

    @Override
    public void setViewType(ViewType mViewType) {
        super.setViewType(mViewType);
        updateVideoViewViewType();
    }

    private void updateVideoViewViewType() {
        if(mVideoView!=null){
            if(getViewType() == ViewType.SURFACEVIEW){
                mVideoView.setEnableSurfaceView();
            }else if(getViewType() == ViewType.TEXTUREVIEW){
                mVideoView.setEnableTextureView();
            }
        }
    }

    private void updateVideoViewDecodeMode() {
        if(mVideoView!=null){
            if(getDecodeMode() == DecodeMode.MEDIAPLAYER){
                mVideoView.setUsingAndroidPlayer(true);
            }else if(getDecodeMode() == DecodeMode.SOFT){
                mVideoView.setUsingAndroidPlayer(false);
            }else if(getDecodeMode() == DecodeMode.HARD){
                mVideoView.setUsingAndroidPlayer(false);
                mVideoView.setUsingMediaCodec(true);
            }
        }
    }

    @Override
    public void onGestureDoubleTap() {
        super.onGestureDoubleTap();
        toggleAspectRatio();
    }

    private void toggleAspectRatio() {
        if(available()){
            mVideoView.toggleAspectRatio();
        }
    }

    public void showTableLayout(){
        if(getDecodeMode() == DecodeMode.MEDIAPLAYER)
            return ;
        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        relativeLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
        tableLayout = new TableLayout(getContext());
        RelativeLayout.LayoutParams tableParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        tableParams.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
        tableParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        int padding = 15;
        tableLayout.setPadding(padding,padding,padding,padding);
        tableLayout.setBackgroundColor(Color.parseColor("#77ffffff"));
        relativeLayout.addView(tableLayout,tableParams);
        mVideoView.setHudView(tableLayout);
        setTableLayoutState(false);
        addView(relativeLayout);
    }

    public boolean isTableLayoutShow(){
        if(tableLayout == null)
            return false;
        return tableLayout.getVisibility() == View.VISIBLE;
    }

    public void setTableLayoutState(boolean state){
        if(tableLayout!=null){
            tableLayout.setVisibility(state?View.VISIBLE: View.GONE);
        }
    }

    public void showMediaInfo(){
        if(mVideoView!=null){
            mVideoView.showMediaInfo();
        }
    }

    private boolean available(){
        return mVideoView!=null && hasLoadLibrary;
    }

    @Override
    public void setData(String... data) {
        if(available() && data!=null && data.length>0){
            mVideoView.setVideoPath(data[0]);
        }
    }

    @Override
    public void start() {
        if(available()){
            mVideoView.start();
            onPlayerEvent(OnPlayerEventListener.EVENT_CODE_ON_INTENT_TO_START);
        }
    }

    @Override
    public void start(int msc){
        if(available()){
            if(msc > 0){
                startSeekPos = msc;
            }
            start();
        }
    }

    @Override
    public void pause() {
        if(available() && mVideoView.isPlaying()){
            mVideoView.pause();
            mStatus = STATUS_PAUSE;
            onPlayerEvent(OnPlayerEventListener.EVENT_CODE_PLAY_PAUSE);
        }
    }

    @Override
    public void resume() {
        if(available() && mStatus == STATUS_PAUSE){
            mVideoView.start();
            mStatus = STATUS_PLAYING;
            onPlayerEvent(OnPlayerEventListener.EVENT_CODE_PLAY_RESUME);
        }
    }

    @Override
    public void seekTo(int msc) {
        if(available()){
            mVideoView.seekTo(msc);
        }
    }

    @Override
    public void stop() {
        if(available()){
            mVideoView.stop();
        }
    }

    @Override
    public boolean isPlaying() {
        if(available()){
            return mVideoView.isPlaying();
        }
        return false;
    }

    @Override
    public int getCurrentPosition() {
        if(available()){
            return mVideoView.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public int getDuration() {
        if(available()){
            return mVideoView.getDuration();
        }
        return 0;
    }

    @Override
    public int getBufferPercentage() {
        if(available()){
            return mVideoView.getBufferPercentage();
        }
        return 0;
    }

    @Override
    public void destroy() {
        super.destroy();
        if(mVideoView!=null){
            mVideoView.stopPlayback();
        }
    }
}
