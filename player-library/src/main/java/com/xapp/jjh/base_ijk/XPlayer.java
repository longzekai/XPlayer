package com.xapp.jjh.base_ijk;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.xapp.jjh.base_ijk.inter.IExtendHandle;
import com.xapp.jjh.base_ijk.listener.OnSlideHandleListener;
import com.xapp.jjh.base_ijk.utils.TimeUtil;
import com.xapp.jjh.base_ijk.widget.VideoPlayer;

/**
 * ------------------------------------
 * Created by Taurus on 2016/8/10.
 * ------------------------------------
 */
public class XPlayer extends VideoPlayer implements IExtendHandle{

    private View touchLayout;
    private OnSlideHandleListener mOnSlideHandleListener;

    private boolean changeLightEnable = true;
    private boolean changeSoundEnable = true;
    private boolean seekEnable = true;
    private float brightness=-1;
    private int volume=-1;
    private long newPosition = -1;
    private AudioManager audioManager;
    private int mMaxVolume;

    private final int MSG_HIDDEN_SLIDE_CONTROL = 10001;
    private final int MSG_SLIDE_SEEK = 10002;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
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
            }
        }
    };

    public XPlayer(Context context) {
        super(context);
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
    protected void initPlayer(Context context) {
        super.initPlayer(context);
        initManager();
        addTouchLayout();
        updateGestureDetector();
    }

    private void initManager() {
        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    private void addTouchLayout() {
        touchLayout = View.inflate(getContext(),R.layout.layout_player_extend_center_box,null);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        addView(touchLayout,params);
    }

    private void updateGestureDetector() {
        final GestureDetector gestureDetector = new GestureDetector(getContext(), new PlayerGestureListener());
        touchLayout.setClickable(true);
        touchLayout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event))
                    return true;
                // 处理手势结束
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        endGesture();
                        break;
                }

                return false;
            }
        });
    }

    @Override
    public void setOnSlideHandleListener(OnSlideHandleListener onSlideHandleListener) {
        this.mOnSlideHandleListener = onSlideHandleListener;
    }

    @Override
    public void setSlideChangeLightEnable(boolean enable) {
        this.changeLightEnable = enable;
    }

    @Override
    public void setSlideChangeSoundEnable(boolean enable) {
        this.changeSoundEnable = enable;
    }

    @Override
    public void setSlideSeekEnable(boolean enable) {
        this.seekEnable = enable;
    }

    /**
     * 手势结束
     */
    private void endGesture() {
        volume = -1;
        brightness = -1f;
        mHandler.sendEmptyMessageDelayed(MSG_HIDDEN_SLIDE_CONTROL,500);
        if(newPosition > 0){
            mHandler.sendEmptyMessage(MSG_SLIDE_SEEK);
        }
    }

    public class PlayerGestureListener extends GestureDetector.SimpleOnGestureListener {
        private boolean firstTouch;
        private boolean volumeControl;
        private boolean toSeek;

        /**
         * 双击
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            toggleAspectRatio();
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            firstTouch = true;
            return super.onDown(e);
        }

        /**
         * 滑动
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if(!changeLightEnable && !changeSoundEnable && !seekEnable)
                return super.onScroll(e1, e2, distanceX, distanceY);
            float mOldX = e1.getX(), mOldY = e1.getY();
            float deltaY = mOldY - e2.getY();
            float deltaX = mOldX - e2.getX();
            if (firstTouch) {
                toSeek = Math.abs(distanceX) >= Math.abs(distanceY);
                volumeControl=mOldX > mWidthPixels * 0.5f;
                firstTouch = false;
            }

            if (toSeek && seekEnable) {
                onHorizontalProgressSlide(-deltaX / getWidth());
            } else {
                float percent = deltaY / getHeight();
                if (volumeControl && changeSoundEnable) {
                    onRightVolumeSlide(percent);
                } else if(changeLightEnable){
                    onLeftBrightnessSlide(percent);
                }
            }

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            return true;
        }
    }

    private void onHorizontalProgressSlide(float percent) {
        if(mOnSlideHandleListener!=null){
            mOnSlideHandleListener.onHorizontalSlide(percent);
        }
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

    private void onRightVolumeSlide(float percent) {
        if(mOnSlideHandleListener!=null){
            mOnSlideHandleListener.onRightVerticalSlide(percent);
        }
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
        setVolumeIcon(i==0?R.drawable.ic_volume_off_white_36dp:R.drawable.ic_volume_up_white_36dp);
        setLightState(false);
        setFastForwardState(false);
        setVolumeState(true);
        setVolumeText(s);
    }

    private void onLeftBrightnessSlide(float percent) {
        if(mOnSlideHandleListener!=null){
            mOnSlideHandleListener.onLeftVerticalSlide(percent);
        }
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

    public void setLoadingState(boolean state){
        touchLayout.findViewById(R.id.app_video_loading).setVisibility(state?View.VISIBLE:View.GONE);
    }

    private void setVolumeState(boolean state){
        touchLayout.findViewById(R.id.app_video_volume_box).setVisibility(state?View.VISIBLE:View.GONE);
    }

    private void setVolumeText(String volumeText){
        ((TextView)touchLayout.findViewById(R.id.app_video_volume)).setText(volumeText);
    }

    private void setVolumeIcon(int id){
        ((ImageView)touchLayout.findViewById(R.id.app_video_volume_icon)).setImageResource(id);
    }

    private void setLightState(boolean state){
        touchLayout.findViewById(R.id.app_video_brightness_box).setVisibility(state?View.VISIBLE:View.GONE);
    }

    private void setLightText(String lightText){
        ((TextView)touchLayout.findViewById(R.id.app_video_brightness)).setText(lightText);
    }

    private void setFastForwardText(String fastForwardText){
        ((TextView)touchLayout.findViewById(R.id.app_video_fastForward)).setText(fastForwardText);
    }

    private void setFastForwardTargetText(String fastForwardTargetText){
        ((TextView)touchLayout.findViewById(R.id.app_video_fastForward_target)).setText(fastForwardTargetText);
    }

    private void setFastForwardAllText(String fastForwardAllText){
        ((TextView)touchLayout.findViewById(R.id.app_video_fastForward_all)).setText(fastForwardAllText);
    }

    private void setFastForwardState(boolean state){
        touchLayout.findViewById(R.id.app_video_fastForward_box).setVisibility(state?View.VISIBLE:View.GONE);
    }

}
