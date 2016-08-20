package com.xapp.jjh.xplayer;

import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.xapp.jjh.base_ijk.XPlayer;
import com.xapp.jjh.base_ijk.config.DecodeMode;
import com.xapp.jjh.base_ijk.config.ViewType;
import com.xapp.jjh.base_ijk.listener.OnCompletionListener;
import com.xapp.jjh.base_ijk.listener.OnErrorListener;
import com.xapp.jjh.base_ijk.listener.OnPlayInfoListener;
import com.xapp.jjh.base_ijk.listener.OnPreparedListener;
import com.xapp.jjh.base_ijk.listener.OnScreenChangeListener;
import com.xapp.jjh.base_ijk.listener.OnSeekCompleteListener;
import com.xapp.jjh.base_ijk.listener.OnSlideHandleListener;
import com.xapp.jjh.xui.activity.TopBarActivity;


public class PlayerActivity extends TopBarActivity implements OnPreparedListener, OnPlayInfoListener, OnErrorListener, OnSeekCompleteListener, OnCompletionListener, OnSlideHandleListener {

    private XPlayer mVideoPlayer;
    private String url;

    @Override
    public void parseIntent() {
        super.parseIntent();
        url = getIntent().getStringExtra("path");
    }

    @Override
    public View getContentView(LayoutInflater layoutInflater, ViewGroup container) {
        return inflate(R.layout.activity_main);
    }

    public void setListener() {
        mVideoPlayer.setOnScreenChangeListener(new OnScreenChangeListener() {
            @Override
            public void onLandScape() {

            }

            @Override
            public void onPortrait() {

            }

            @Override
            public void onFullScreen() {
                fullScreen();
            }

            @Override
            public void onQuitFullScreen() {
                quitFullScreen();
            }
        });
    }

    public void findViewById() {
        mVideoPlayer = findView(R.id.player);
    }

    @Override
    public void initData() {
        setSwipeBackEnable(false);
        setTopBarTitle(getIntent().getStringExtra("name"));
        mVideoPlayer.setDecodeMode(DecodeMode.SOFT);
        mVideoPlayer.setViewType(ViewType.SURFACEVIEW);
        mVideoPlayer.useDefaultPlayControl(true);
        mVideoPlayer.setOnPreparedListener(this);
        mVideoPlayer.setOnPlayerInfoListener(this);
        mVideoPlayer.setOnErrorListener(this);
        mVideoPlayer.setOnSeekCompleteListener(this);
        mVideoPlayer.setOnCompletionListener(this);
        mVideoPlayer.setOnSlideHandleListener(this);
        mVideoPlayer.play(url);
        mVideoPlayer.setLoadingState(true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(mVideoPlayer!=null){
            mVideoPlayer.doConfigChange(newConfig);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mVideoPlayer!=null){
            mVideoPlayer.onDestroy();
            mVideoPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mVideoPlayer!=null){
            mVideoPlayer.onDestroy();
        }
    }

    @Override
    public void onPrepared() {

    }

    @Override
    public void onPlayerInfo(int what, int extra) {

    }

    @Override
    public void onRenderStart() {
        mVideoPlayer.setLoadingState(false);
    }

    @Override
    public void onBufferingStart() {
        mVideoPlayer.setLoadingState(true);
    }

    @Override
    public void onBufferingEnd() {
        mVideoPlayer.setLoadingState(false);
    }

    @Override
    public void onError(int what, int extra) {

    }

    @Override
    public void onSeekComplete() {

    }

    @Override
    public void onCompletion() {

    }

    @Override
    public void onLeftVerticalSlide(float percent) {
        System.out.println("TestSlide onLeftVerticalSlide : " + percent);
    }

    @Override
    public void onRightVerticalSlide(float percent) {
        System.out.println("TestSlide onRightVerticalSlide : " + percent);
    }

    @Override
    public void onHorizontalSlide(float percent) {
        System.out.println("TestSlide onHorizontalSlide : " + percent);
    }

}
