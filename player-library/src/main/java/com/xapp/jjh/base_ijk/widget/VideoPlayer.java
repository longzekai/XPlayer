package com.xapp.jjh.base_ijk.widget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.xapp.jjh.base_ijk.bean.VideoPlayingInfo;
import com.xapp.jjh.base_ijk.config.DecodeMode;
import com.xapp.jjh.base_ijk.config.ViewType;
import com.xapp.jjh.base_ijk.ijk.IjkVideoView;
import com.xapp.jjh.base_ijk.inter.IVideoPlayer;
import com.xapp.jjh.base_ijk.listener.OnCompletionListener;
import com.xapp.jjh.base_ijk.listener.OnErrorListener;
import com.xapp.jjh.base_ijk.listener.OnPlayInfoListener;
import com.xapp.jjh.base_ijk.listener.OnPreparedListener;
import com.xapp.jjh.base_ijk.listener.OnSeekCompleteListener;
import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.MediaPlayerProxy;

/**
 * ------------------------------------
 * Created by Taurus on 2016/8/2.
 * ------------------------------------
 */
public class VideoPlayer extends FrameLayout implements IVideoPlayer{

    private String TAG = "VideoPlayer";
    private IjkVideoView mVideoView;
    private IjkMediaPlayer mMediaPlayer;
    protected Activity mActivity;
    private int mOriginalHeight;
    private boolean hasLoadLibrary;
    private OnPreparedListener mOnPreparedListener;
    private OnPlayInfoListener mOnPlayerInfoListener;
    private OnCompletionListener mOnCompletionListener;
    private OnErrorListener mOnErrorListener;
    private OnSeekCompleteListener mOnSeekCompleteListener;

    private DecodeMode mDecodeMode = DecodeMode.SOFT;
    private ViewType mViewType = ViewType.SURFACEVIEW;

    private int mStatus = STATUS_IDLE;
    private String mUrl;
    private int mCurrentPosition;
    protected int mWidthPixels;
    protected int mHeightPixels;
    private OrientationEventListener orientationEventListener;
    private boolean portrait;

    public VideoPlayer(Context context) {
        super(context);
        initPlayer(context);
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPlayer(context);
    }

    public VideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPlayer(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VideoPlayer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initPlayer(context);
    }

    public void setOnPreparedListener(OnPreparedListener mOnPreparedListener) {
        this.mOnPreparedListener = mOnPreparedListener;
    }

    public void setOnPlayerInfoListener(OnPlayInfoListener mOnPlayerInfoListener) {
        this.mOnPlayerInfoListener = mOnPlayerInfoListener;
    }

    public void setOnCompletionListener(OnCompletionListener mOnCompletionListener) {
        this.mOnCompletionListener = mOnCompletionListener;
    }

    public void setOnErrorListener(OnErrorListener mOnErrorListener) {
        this.mOnErrorListener = mOnErrorListener;
    }

    public void setOnSeekCompleteListener(OnSeekCompleteListener mOnSeekCompleteListener) {
        this.mOnSeekCompleteListener = mOnSeekCompleteListener;
    }

    public DecodeMode getDecodeMode() {
        return mDecodeMode;
    }

    public void setDecodeMode(DecodeMode mDecodeMode) {
        this.mDecodeMode = mDecodeMode;
        updateVideoViewDecodeMode();
    }

    public ViewType getViewType() {
        return mViewType;
    }

    public void setViewType(ViewType mViewType) {
        this.mViewType = mViewType;
        updateVideoViewViewType();
    }

    private void updateVideoViewViewType() {
        if(mVideoView!=null){
            if(mViewType == ViewType.SURFACEVIEW){
                mVideoView.setEnableSurfaceView();
            }else if(mViewType == ViewType.TEXTUREVIEW){
                mVideoView.setEnableTextureView();
            }
        }
    }

    private void updateVideoViewDecodeMode() {
        if(mVideoView!=null){
            if(mDecodeMode == DecodeMode.MEDIAPLAYER){
                mVideoView.setUsingAndroidPlayer(true);
            }else if(mDecodeMode == DecodeMode.SOFT){
                mVideoView.setUsingAndroidPlayer(false);
            }else if(mDecodeMode == DecodeMode.HARD){
                mVideoView.setUsingAndroidPlayer(false);
                mVideoView.setUsingMediaCodec(true);
            }
        }
    }

    protected void initPlayer(Context context) {
        mActivity = (Activity) context;
        initScreenParams();
        initOrientationListener();
        loadLibrary();
        mVideoView = new IjkVideoView(context);
        mVideoView.setBackgroundColor(Color.BLACK);
        initPlayerListener();
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        addView(mVideoView,params);
        post(new Runnable() {
            @Override
            public void run() {
                mOriginalHeight = getMeasuredHeight();
                Log.d(TAG,"mOriginalHeight : " + mOriginalHeight);
            }
        });
    }

    private void initOrientationListener() {
        if(mActivity == null)
            return;
        orientationEventListener = new OrientationEventListener(mActivity) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation >= 0 && orientation <= 30 || orientation >= 330 || (orientation >= 150 && orientation <= 210)) {
                    //竖屏
                    if (portrait) {
                        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        orientationEventListener.disable();
                    }
                } else if ((orientation >= 90 && orientation <= 120) || (orientation >= 240 && orientation <= 300)) {
                    if (!portrait) {
                        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        orientationEventListener.disable();
                    }
                }
            }
        };
        portrait = getScreenOrientation()==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    private void initScreenParams() {
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        mWidthPixels = dm.widthPixels;
        mHeightPixels = dm.heightPixels;
    }

    private void loadLibrary() {
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
            hasLoadLibrary = true;
        } catch (Throwable e) {
            Log.e("GiraffePlayer", "loadLibraries error", e);
        }
    }

    private void initPlayerListener() {
        mVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                statusChange(STATUS_COMPLETED);
                if(mOnCompletionListener!=null){
                    mOnCompletionListener.onCompletion();
                }
            }
        });
        mVideoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                switch (what) {
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        statusChange(STATUS_LOADING);
                        if(mOnPlayerInfoListener!=null){
                            mOnPlayerInfoListener.onBufferingStart();
                        }
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        statusChange(STATUS_PLAYING);
                        if(mOnPlayerInfoListener!=null){
                            mOnPlayerInfoListener.onBufferingEnd();
                        }
                        break;
                    case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                        //显示 下载速度
//                        Toaster.show("download rate:" + extra);
//                        System.out.println("download_speed : " + extra);
                        break;
                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        statusChange(STATUS_PLAYING);
                        if(mOnPlayerInfoListener!=null){
                            mOnPlayerInfoListener.onRenderStart();
                        }
                        break;
                }
                if(mOnPlayerInfoListener!=null){
                    mOnPlayerInfoListener.onPlayerInfo(what, extra);
                }
                return false;
            }
        });
        mVideoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer mp, int what, int extra) {
                statusChange(STATUS_ERROR);
                if(mOnErrorListener!=null){
                    mOnErrorListener.onError(what, extra);
                }
                return false;
            }
        });
        mVideoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                preparedMediaPlayer(mp);
                if(mOnPreparedListener!=null){
                    mOnPreparedListener.onPrepared();
                }
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
        }else if(mediaPlayer instanceof AndroidMediaPlayer){
            MediaPlayer player = ((AndroidMediaPlayer) mediaPlayer).getInternalMediaPlayer();
            getMediaTrackInfo(player);
        }
        mediaPlayer.setOnSeekCompleteListener(new IMediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(IMediaPlayer mp) {
                if(mOnSeekCompleteListener!=null){
                    mOnSeekCompleteListener.onSeekComplete();
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void getMediaTrackInfo(MediaPlayer mediaPlayer) {
        MediaPlayer player = mediaPlayer;
        MediaPlayer.TrackInfo[] trackInfos = player.getTrackInfo();
        if(trackInfos!=null && trackInfos.length>0){
            MediaPlayer.TrackInfo trackInfo = trackInfos[0];
            MediaFormat mediaFormat = trackInfo.getFormat();
            if(mediaFormat!=null){
                Log.d(TAG,mediaFormat.toString());
            }
        }
    }

    public void doConfigChange(Configuration newConfig){
        portrait = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT;
        if(mVideoView!=null){
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    tryFullScreen(!portrait);
                    togglePlayerLayoutParams(!portrait);
                }
            });
            orientationEventListener.enable();
        }
    }

    public void toggleFullScreen(){
        if(mActivity == null)
            return;
        if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            Log.d(TAG,"toggle portrait");
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            Log.d(TAG,"toggle landscape");
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private int getScreenOrientation() {
        if(mActivity == null)
            return 1;
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        int width = mWidthPixels;
        int height = mHeightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }

    private void tryFullScreen(boolean fullScreen) {
        if (mActivity instanceof AppCompatActivity) {
            ActionBar supportActionBar = ((AppCompatActivity) mActivity).getSupportActionBar();
            if (supportActionBar != null) {
                if (fullScreen) {
                    supportActionBar.hide();
                } else {
                    supportActionBar.show();
                }
            }
        }
        setFullScreen(fullScreen);
    }

    private void setFullScreen(boolean fullScreen) {
        if (mActivity != null) {
            WindowManager.LayoutParams attrs = mActivity.getWindow().getAttributes();
            if (fullScreen) {
                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                mActivity.getWindow().setAttributes(attrs);
                mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            } else {
                attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                mActivity.getWindow().setAttributes(attrs);
                mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
        }

    }

    private void togglePlayerLayoutParams(boolean fullScreen) {
        ViewGroup.LayoutParams params = getLayoutParams();
        if(fullScreen){
            params.height = mWidthPixels;
            params.width = mHeightPixels;
        }else{
            params.height = mOriginalHeight;
            params.width = mWidthPixels;
        }
        setLayoutParams(params);
    }

    @Override
    public VideoPlayingInfo getVideoPlayingInfo() {
        VideoPlayingInfo videoPlayingInfo = null;
        if(mMediaPlayer!=null){
            videoPlayingInfo = new VideoPlayingInfo();
            videoPlayingInfo.setOutputFramesPerSecond(mMediaPlayer.getVideoOutputFramesPerSecond());
            videoPlayingInfo.setDecodeFramesPerSecond(mMediaPlayer.getVideoDecodeFramesPerSecond());
            videoPlayingInfo.setAudioCachedBytes(mMediaPlayer.getAudioCachedBytes());
            videoPlayingInfo.setVideoCachedBytes(mMediaPlayer.getVideoCachedBytes());
            videoPlayingInfo.setAudioCachedDuration(mMediaPlayer.getAudioCachedDuration());
            videoPlayingInfo.setVideoCachedDuration(mMediaPlayer.getVideoCachedDuration());
        }
        return videoPlayingInfo;
    }

    private void statusChange(int status) {
        mStatus = status;
    }

    @Override
    public int getStatus(){
        return mStatus;
    }

    @Override
    public void play(String url) {
        Log.d(TAG,"url : " + url);
        this.mUrl = url;
        if(hasLoadLibrary && mVideoView!=null){
            mVideoView.setVideoPath(url);
            mVideoView.start();
        }
    }

    @Override
    public void start() {
        if(mVideoView!=null){
            mVideoView.start();
        }
    }

    @Override
    public void seekTo(int msc) {
        if(mVideoView!=null){
            mVideoView.seekTo(msc);
        }
    }

    @Override
    public void pause() {
        if(mVideoView!=null){
            mStatus = STATUS_PAUSE;
            mVideoView.pause();
            mCurrentPosition = mVideoView.getCurrentPosition();
        }
    }

    @Override
    public void resume() {
        if(mVideoView!=null && mStatus == STATUS_PAUSE){
            if(mCurrentPosition > 0){
                mVideoView.seekTo(mCurrentPosition);
            }
            mVideoView.start();
            mStatus = STATUS_PLAYING;
        }
    }

    @Override
    public boolean isPlaying() {
        if(mVideoView == null)
            return false;
        return mVideoView.isPlaying();
    }

    @Override
    public void toggleAspectRatio() {
        if(mVideoView != null){
            mVideoView.toggleAspectRatio();
        }
    }

    @Override
    public int getCurrentPosition() {
        if(mVideoView == null)
            return 0;
        return mVideoView.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        if(mVideoView == null)
            return 0;
        return mVideoView.getDuration();
    }

    @Override
    public int getBufferPercentage() {
        if(mVideoView == null)
            return 0;
        return mVideoView.getBufferPercentage();
    }

    @Override
    public void onDestroy() {
        mOnPreparedListener = null;
        mOnPlayerInfoListener = null;
        mOnErrorListener = null;
        mOnCompletionListener = null;
        if(orientationEventListener!=null){
            orientationEventListener.disable();
        }
        if(mVideoView!=null){
            mVideoView.stopPlayback();
        }
    }
}
