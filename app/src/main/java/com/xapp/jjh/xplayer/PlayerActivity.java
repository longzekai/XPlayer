package com.xapp.jjh.xplayer;

import android.content.res.Configuration;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

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

    private String TAG = "PlayerActivity";
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
//        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                Log.d(TAG,"onGlobalLayout");
//            }
//        });

        /** 设置准备完成的监听器*/
        mVideoPlayer.setOnPreparedListener(this);
        /** 设置播放信息监听器*/
        mVideoPlayer.setOnPlayerInfoListener(this);
        /** 设置错误信息监听器*/
        mVideoPlayer.setOnErrorListener(this);
        /** 设置定位完成的监听器*/
        mVideoPlayer.setOnSeekCompleteListener(this);
        /** 设置播放完成的监听器*/
        mVideoPlayer.setOnCompletionListener(this);
        /** 设置滑动手势监听器*/
        mVideoPlayer.setOnSlideHandleListener(this);
    }

    public void findViewById() {
        mVideoPlayer = findView(R.id.player);
    }

    @Override
    public void initData() {
        setSwipeBackEnable(false);
        setTopBarTitle(getIntent().getStringExtra("name"));
        int decodeMode = getIntent().getIntExtra("decode_mode",0);
        /** 设置解码模式*/
        mVideoPlayer.setDecodeMode(decodeMode == 0?DecodeMode.SOFT:DecodeMode.HARD);
        /** 设置渲染视图类型*/
        mVideoPlayer.setViewType(ViewType.SURFACEVIEW);
        /** 是否使用默认的播放控制器*/
        mVideoPlayer.useDefaultPlayControl(true);
        /** 是否显示播放帧率等信息*/
        mVideoPlayer.showTableLayout();
        /** 是否使用默认的加载样式*/
        mVideoPlayer.setUseDefaultLoadingStyle(true);
        /** 播放指定的资源*/
        mVideoPlayer.play(url);
//        mVideoPlayer.play("http://172.16.218.64:8080/batamu.mp4");
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
    protected void onResume() {
        super.onResume();
        if(mVideoPlayer!=null){
            mVideoPlayer.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mVideoPlayer!=null){
            mVideoPlayer.pause();
        }
    }

    @Override
    public void onPrepared() {
        Log.d(TAG,"onPrepared");
    }

    @Override
    public void onPlayerInfo(int what, int extra) {
        Log.d(TAG,"onPlayerInfo" + what);
    }

    @Override
    public void onRenderStart() {

    }

    @Override
    public void onBufferingStart() {

    }

    @Override
    public void onBufferingEnd() {

    }

    @Override
    public void onError(int what, int extra) {
        Log.d(TAG,"onError" + what);
        showSnackBar("Error", Snackbar.LENGTH_LONG,"OK",null);
    }

    @Override
    public void onSeekComplete() {
        Log.d(TAG,"onSeekComplete");
    }

    @Override
    public void onCompletion() {
        Log.d(TAG,"onCompletion");
        showSnackBar("Play Complete",null,null);
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
