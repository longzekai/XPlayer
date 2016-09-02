package com.xapp.jjh.base_ijk.widget;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.SeekBar;
import com.xapp.jjh.base_ijk.R;
import com.xapp.jjh.base_ijk.inter.IPlayer;
import com.xapp.jjh.base_ijk.utils.TimeUtil;


/**
 * Created by Taurus on 2016/8/29.
 */
public abstract class BaseBindControllerPlayer extends BasePlayController implements IPlayer {

    private final String TAG = "BindControllerPlayer";
    private float brightness=-1;
    private int volume=-1;
    private long newPosition = -1;
    private AudioManager audioManager;
    private int mMaxVolume;

    private OrientationEventListener orientationEventListener;
    private boolean portrait;
    protected boolean isFullScreen;

    private final long MSC_TIME_DELAY = 5000;

    private final int MSG_PLAYING = 10000;
    private final int MSG_HIDDEN_SLIDE_CONTROL = 10001;
    private final int MSG_SLIDE_SEEK = 10002;
    private final int MSG_DELAY_HIDDEN_PLAY_CONTROL = 10003;

    protected Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_PLAYING:
                    setSeekMax(getDuration());
                    setSeekProgress(getCurrentPosition());
                    setSeekSecondProgress(getBufferPercentage()*getDuration()/100);
                    setPlayTime(getCurrentPosition(),getDuration());
                    mHandler.sendEmptyMessageDelayed(MSG_PLAYING,1000);
                    break;

                case MSG_HIDDEN_SLIDE_CONTROL:
                    setVolumeState(false);
                    setLightState(false);
                    setFastForwardState(false);
                    break;

                case MSG_SLIDE_SEEK:
                    if(newPosition > 0){
                        seekTo((int) newPosition);
                        newPosition = -1;
                    }
                    break;

                case MSG_DELAY_HIDDEN_PLAY_CONTROL:
                    setPlayControlState(false);
                    setTopStatusBarState(false);
                    break;
            }
        }
    };

    public BaseBindControllerPlayer(Context context) {
        super(context);
    }

    public BaseBindControllerPlayer(Context context, int width, int height) {
        super(context, width, height);
    }

    public BaseBindControllerPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseBindControllerPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BaseBindControllerPlayer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void bindController(Context context) {
        initAudioManager(context);
        initOrientationListener();
    }

    private void initAudioManager(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    private void initOrientationListener() {
        if(mActivity == null)
            return;
        orientationEventListener = new OrientationEventListener(mActivity) {
            @Override
            public void onOrientationChanged(int orientation) {
                portrait = getScreenOrientation()==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
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

    public boolean isFullScreen() {
        return isFullScreen;
    }

    @Override
    public void doConfigChange(Configuration newConfig){
        portrait = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT;
        isFullScreen = !portrait;
        if(!isFullScreen){
            setTopStatusBarState(false);
        }
        post(new Runnable() {
            @Override
            public void run() {
                tryFullScreen(!portrait);
                togglePlayerLayoutParams(!portrait);
                onScreenOrientationChange();
                orientationEventListener.enable();
            }
        });
    }

    @Override
    public void toggleFullScreen(){
        if(mActivity == null)
            return;
        if (isFullScreen()) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            Log.d(TAG,"toggle portrait");
            isFullScreen = false;
        } else {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            Log.d(TAG,"toggle landscape");
            isFullScreen = true;
        }
        onScreenOrientationChange();
    }

    protected void onScreenOrientationChange() {

    }

    private void tryFullScreen(boolean fullScreen) {
        if (mActivity instanceof AppCompatActivity) {
            ActionBar supportActionBar = ((AppCompatActivity)mActivity).getSupportActionBar();
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
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            Log.d(TAG,"set land");
        }else{
            params.height = mOriginalHeight;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            Log.d(TAG,"set port");
        }
        setLayoutParams(params);
    }

    @Override
    protected void onPlayIconClick() {
        super.onPlayIconClick();
        if(isPlaying()){
            pause();
            setPlayState(false);
        }else{
            resume();
            setPlayState(true);
        }
        delayHiddenPlayControl();
    }

    protected void sendPlayingMsg(){
        mHandler.removeMessages(MSG_PLAYING);
        mHandler.sendEmptyMessage(MSG_PLAYING);
    }

    private void delayHiddenPlayControl() {
        mHandler.removeMessages(MSG_DELAY_HIDDEN_PLAY_CONTROL);
        mHandler.sendEmptyMessageDelayed(MSG_DELAY_HIDDEN_PLAY_CONTROL,MSC_TIME_DELAY);
    }

    @Override
    protected void endGesture() {
        super.endGesture();
        volume = -1;
        brightness = -1f;
        mHandler.sendEmptyMessageDelayed(MSG_HIDDEN_SLIDE_CONTROL,500);
        if(newPosition > 0){
            mHandler.sendEmptyMessage(MSG_SLIDE_SEEK);
        }
    }

    @Override
    protected void onSeekBarProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        super.onSeekBarProgressChanged(seekBar, progress, fromUser);
        if(fromUser){
            setPlayTime(progress,seekBar.getMax());
        }
    }

    @Override
    protected void onSeekBarStartTrackingTouch(SeekBar seekBar) {
        super.onSeekBarStartTrackingTouch(seekBar);
        mHandler.removeMessages(MSG_DELAY_HIDDEN_PLAY_CONTROL);
    }

    @Override
    protected void onSeekBarStopTrackingTouch(SeekBar seekBar) {
        super.onSeekBarStopTrackingTouch(seekBar);
        int progress = seekBar.getProgress();
        seekTo(progress);
        delayHiddenPlayControl();
    }

    @Override
    public void leftVerticalSlide(float percent) {
        if (brightness < 0) {
            brightness = mActivity.getWindow().getAttributes().screenBrightness;
            if (brightness <= 0.00f){
                brightness = 0.50f;
            }else if (brightness < 0.01f){
                brightness = 0.01f;
            }
        }
        setVolumeState(false);
        setFastForwardState(false);
        setLightState(true);
        WindowManager.LayoutParams lpa = mActivity.getWindow().getAttributes();
        lpa.screenBrightness = brightness + percent;
        if (lpa.screenBrightness > 1.0f){
            lpa.screenBrightness = 1.0f;
        }else if (lpa.screenBrightness < 0.01f){
            lpa.screenBrightness = 0.01f;
        }
        setLightText(((int) (lpa.screenBrightness * 100))+"%");
        mActivity.getWindow().setAttributes(lpa);
    }

    @Override
    public void rightVerticalSlide(float percent) {
        if (volume == -1) {
            volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (volume < 0)
                volume = 0;
        }
        int index = (int) (percent * mMaxVolume) + volume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;
        // 变更声音
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
        // 变更进度条
        int i = (int) (index * 1.0 / mMaxVolume * 100);
        String s = i + "%";
        if (i == 0) {
            s = "OFF";
        }
        // 显示
        setVolumeIcon(i==0?R.mipmap.ic_volume_off_white_36dp: R.mipmap.ic_volume_up_white_36dp);
        setLightState(false);
        setFastForwardState(false);
        setVolumeState(true);
        setVolumeText(s);
    }

    @Override
    public void horizontalSlide(float percent) {
        long position = getCurrentPosition();
        long duration = getDuration();
        long deltaMax = Math.min(100 * 1000, duration - position);
        long delta = (long) (deltaMax * percent);
        newPosition = delta + position;
        if (newPosition > duration) {
            newPosition = duration;
        } else if (newPosition <= 0) {
            newPosition =0;
            delta=-position;
        }
        int showDelta = (int) delta / 1000;
        if (showDelta != 0) {
            setVolumeState(false);
            setLightState(false);
            setFastForwardState(true);
            String text = showDelta > 0 ? ("+" + showDelta) : "" + showDelta;
            setFastForwardText(text + "s");
            setFastForwardTargetText(TimeUtil.getTime(newPosition)+"/");
            setFastForwardAllText(TimeUtil.getTime(duration));
        }
    }

    @Override
    public void onGestureDoubleTap() {

    }

    @Override
    public void onGestureSingleTapUp() {
        togglePlayControlState();
    }

    private void togglePlayControlState() {
        if(isPlayControlShow()){
            setPlayControlState(false);
            setTopStatusBarState(false);
        }else{
            setPlayControlState(true);
            if(isFullScreen){
                setTopStatusBarState(true);
            }
            delayHiddenPlayControl();
        }
    }

    protected int getScreenOrientation() {
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

    @Override
    public void destroy() {
        mHandler.removeMessages(MSG_PLAYING);
        mHandler.removeMessages(MSG_SLIDE_SEEK);
        mHandler.removeMessages(MSG_HIDDEN_SLIDE_CONTROL);
        mHandler.removeMessages(MSG_DELAY_HIDDEN_PLAY_CONTROL);
    }
}
