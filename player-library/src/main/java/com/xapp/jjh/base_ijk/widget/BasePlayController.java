package com.xapp.jjh.base_ijk.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xapp.jjh.base_ijk.R;
import com.xapp.jjh.base_ijk.inter.IController;
import com.xapp.jjh.base_ijk.utils.TimeUtil;

/**
 * Created by Taurus on 2016/8/29.
 */
public abstract class BasePlayController extends FrameLayout implements IController {

    private final String TAG = "BasePlayController";
    protected Activity mActivity;
    private View mTopStatusBarView;
    private ProgressBar mProgressBar;
    private View mPlayControlView;
    private ImageView mIvPlayStateIcon;
    private SeekBar mSeekBar;
    private TextView mTvPlayTime;
    private TextView mTvSystemTime;
    private View mTouchLayout;

    private boolean mGestureEnable = true;
    private boolean mControllerEnable = true;
    private boolean mTopBarEnable = true;
    private boolean mTapEnable = true;

    protected int mWidthPixels;
    protected int mHeightPixels;

    protected int mOriginalWidth;
    protected int mOriginalHeight;

    public BasePlayController(Context context) {
        super(context);
        initController(context);
    }

    public BasePlayController(Context context, int width, int height){
        super(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width==-1?ViewGroup.LayoutParams.MATCH_PARENT:width,height);
        setLayoutParams(params);
        initController(context);
    }

    public BasePlayController(Context context, AttributeSet attrs) {
        super(context, attrs);
        initController(context);
    }

    public BasePlayController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initController(context);
    }

    @SuppressLint("NewApi")
    public BasePlayController(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initController(context);
    }

    protected void initController(Context context) {
        if(!(context instanceof Activity)){
            throw new IllegalArgumentException("-----please set activity context !-----");
        }
        mActivity = (Activity) context;
        setBackgroundColor(Color.BLACK);
        initSystemInfo();
        initPlayerWidget(context);
        initGestureLayout(context);
        initPlayerControl(context);
        bindController(context);
        onInitOver(context);
        post(new Runnable() {
            @Override
            public void run() {
                mOriginalWidth = getMeasuredWidth();
                mOriginalHeight = getMeasuredHeight();
                Log.d(TAG,"mOriginalWidth : " + mOriginalWidth + " mOriginalHeight : " + mOriginalHeight);
            }
        });
    }

    protected void onInitOver(Context context){

    }

    protected abstract void bindController(Context context);

    private void initPlayerWidget(Context context){
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        addView(getPlayerWidget(context),params);
    }

    protected abstract View getPlayerWidget(Context context);

    protected void initSystemInfo() {
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        mWidthPixels = dm.widthPixels;
        mHeightPixels = dm.heightPixels;
        keepScreenOn();
    }

    protected void keepScreenOn(){
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void initPlayerControl(Context context) {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        View view = View.inflate(context, R.layout.layout_player_controller,null);
        view.setBackgroundColor(Color.TRANSPARENT);
        mTopStatusBarView = view.findViewById(R.id.ll_top_status);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mPlayControlView = view.findViewById(R.id.ll_play_control);
        mIvPlayStateIcon = (ImageView) view.findViewById(R.id.iv_play_state);
        mSeekBar = (SeekBar) view.findViewById(R.id.seek_bar);
        mTvPlayTime = (TextView) view.findViewById(R.id.tv_play_time);
        mTvSystemTime = (TextView) view.findViewById(R.id.tv_system_time);
        addView(view,params);
        addListener();
    }

    private void addListener() {
        mIvPlayStateIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlayIconClick();
            }
        });
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                onSeekBarProgressChanged(seekBar, progress, fromUser);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                onSeekBarStartTrackingTouch(seekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                onSeekBarStopTrackingTouch(seekBar);
            }
        });
    }

    protected void onSeekBarProgressChanged(SeekBar seekBar, int progress, boolean fromUser){

    }

    protected void onSeekBarStartTrackingTouch(SeekBar seekBar) {

    }

    protected void onSeekBarStopTrackingTouch(SeekBar seekBar) {

    }

    protected void onPlayIconClick() {

    }

    private void initGestureLayout(Context context){
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mTouchLayout = View.inflate(context, R.layout.layout_player_extend_center_box,null);
        mTouchLayout.setBackgroundColor(Color.TRANSPARENT);
        addView(mTouchLayout,params);
        final GestureDetector gestureDetector = new GestureDetector(getContext(), new PlayerGestureListener());
        mTouchLayout.setClickable(true);
        mTouchLayout.setOnTouchListener(new OnTouchListener() {
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

    protected void endGesture(){

    }

    @Override
    public void setGestureEnable(boolean enable) {
        this.mGestureEnable = enable;
    }

    @Override
    public void setControllerEnable(boolean enable) {
        this.mControllerEnable = enable;
    }

    @Override
    public void setTopStatusBarEnable(boolean enable) {
        this.mTopBarEnable = enable;
    }

    @Override
    public void setTapEnable(boolean enable){
        this.mTapEnable = enable;
    }

    @Override
    public void setPlayControlState(boolean state) {
        if(state && !mControllerEnable)
            return;
        mPlayControlView.setVisibility(state?View.VISIBLE:View.GONE);
    }

    @Override
    public boolean isPlayControlShow() {
        return mPlayControlView.getVisibility() == View.VISIBLE;
    }

    @Override
    public void setTopStatusBarState(boolean state) {
        if(state && !mTopBarEnable)
            return;
        if(state)
            setSystemTime();
        mTopStatusBarView.setVisibility(state?View.VISIBLE:View.GONE);
    }

    @Override
    public boolean isTopStatusBarShow() {
        return mTopStatusBarView.getVisibility() == View.VISIBLE;
    }

    @Override
    public void setSystemTime() {
        mTvSystemTime.setText(TimeUtil.getNowTime());
    }

    @Override
    public void setLoadingState(boolean state) {
        mProgressBar.setVisibility(state?View.VISIBLE:View.GONE);
    }

    @Override
    public void setPlayState(boolean isPlaying) {
        mIvPlayStateIcon.setImageResource(isPlaying?R.mipmap.ic_video_player_btn_pause:R.mipmap.ic_video_player_btn_play);
    }

    @Override
    public void setPlayTime(long curr, long total) {
        mTvPlayTime.setText(TimeUtil.getTime(curr) + "/" + TimeUtil.getTime(total));
    }

    @Override
    public void setSeekMax(int max) {
        mSeekBar.setMax(max);
    }

    @Override
    public void setSeekProgress(int progress) {
        mSeekBar.setProgress(progress);
    }

    @Override
    public void setSeekSecondProgress(int progress) {
        mSeekBar.setSecondaryProgress(progress);
    }

    @Override
    public abstract void leftVerticalSlide(float percent);

    @Override
    public abstract void rightVerticalSlide(float percent);

    @Override
    public abstract void horizontalSlide(float percent);

    @Override
    public abstract void onGestureDoubleTap();

    @Override
    public abstract void onGestureSingleTapUp();

    protected void setVolumeState(boolean state){
        mTouchLayout.findViewById(R.id.app_video_volume_box).setVisibility(state?View.VISIBLE:View.GONE);
    }

    protected void setVolumeText(String volumeText){
        ((TextView)mTouchLayout.findViewById(R.id.app_video_volume)).setText(volumeText);
    }

    protected void setVolumeIcon(int id){
        ((ImageView)mTouchLayout.findViewById(R.id.app_video_volume_icon)).setImageResource(id);
    }

    protected void setLightState(boolean state){
        mTouchLayout.findViewById(R.id.app_video_brightness_box).setVisibility(state?View.VISIBLE:View.GONE);
    }

    protected void setLightText(String lightText){
        ((TextView)mTouchLayout.findViewById(R.id.app_video_brightness)).setText(lightText);
    }

    protected void setFastForwardText(String fastForwardText){
        ((TextView)mTouchLayout.findViewById(R.id.app_video_fastForward)).setText(fastForwardText);
    }

    protected void setFastForwardTargetText(String fastForwardTargetText){
        ((TextView)mTouchLayout.findViewById(R.id.app_video_fastForward_target)).setText(fastForwardTargetText);
    }

    protected void setFastForwardAllText(String fastForwardAllText){
        ((TextView)mTouchLayout.findViewById(R.id.app_video_fastForward_all)).setText(fastForwardAllText);
    }

    protected void setFastForwardState(boolean state){
        mTouchLayout.findViewById(R.id.app_video_fastForward_box).setVisibility(state?View.VISIBLE:View.GONE);
    }

    public class PlayerGestureListener extends GestureDetector.SimpleOnGestureListener {
        private boolean firstTouch;
        private boolean volumeControl;
        private boolean toSeek;

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if(mTapEnable){
                onGestureDoubleTap();
            }
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            firstTouch = true;
            return super.onDown(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if(!mGestureEnable)
                return super.onScroll(e1, e2, distanceX, distanceY);
            float mOldX = e1.getX(), mOldY = e1.getY();
            float deltaY = mOldY - e2.getY();
            float deltaX = mOldX - e2.getX();
            if (firstTouch) {
                toSeek = Math.abs(distanceX) >= Math.abs(distanceY);
                volumeControl=mOldX > mWidthPixels * 0.5f;
                firstTouch = false;
            }

            if (toSeek) {
                horizontalSlide(-deltaX / getWidth());
            } else {
                float percent = deltaY / getHeight();
                if (volumeControl) {
                    rightVerticalSlide(percent);
                } else{
                    leftVerticalSlide(percent);
                }
            }

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if(mTapEnable){
                onGestureSingleTapUp();
            }
            return true;
        }
    }
}
