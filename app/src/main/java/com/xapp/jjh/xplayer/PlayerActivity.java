package com.xapp.jjh.xplayer;

import android.content.res.Configuration;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xapp.jjh.base_ijk.XPlayer;
import com.xapp.jjh.base_ijk.config.DecodeMode;
import com.xapp.jjh.base_ijk.config.ViewType;
import com.xapp.jjh.base_ijk.inter.IVideoPlayer;
import com.xapp.jjh.base_ijk.listener.OnCompletionListener;
import com.xapp.jjh.base_ijk.listener.OnErrorListener;
import com.xapp.jjh.base_ijk.listener.OnPlayInfoListener;
import com.xapp.jjh.base_ijk.listener.OnPreparedListener;
import com.xapp.jjh.base_ijk.listener.OnSeekCompleteListener;
import com.xapp.jjh.base_ijk.listener.OnSlideHandleListener;
import java.io.File;

public class PlayerActivity extends AppCompatActivity implements OnPreparedListener, OnPlayInfoListener, OnErrorListener, OnSeekCompleteListener, OnCompletionListener, OnSlideHandleListener {

    private XPlayer mVideoPlayer;
    private TextView tv_start;
    private TextView tv_pause;
    private TextView tv_resume;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        url = getIntent().getStringExtra("path");
        findViewById();
        initPlayer();
        setListener();
    }

    private void setListener() {
        tv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mVideoPlayer.play("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
//                mVideoPlayer.play("http://172.16.218.64:8080/lvyexianzong.mkv");
//                String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/DCIM/Camera/lvyexianzong.mkv";
//                File file = new File(path);
//                Toast.makeText(getApplicationContext(),""+file.exists(),Toast.LENGTH_LONG).show();
                mVideoPlayer.play(url);
                mVideoPlayer.setLoadingState(true);
            }
        });
        tv_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pausePlayer();
            }
        });
        tv_resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumePlayer();
            }
        });
    }

    private void resumePlayer() {
        if(mVideoPlayer.getStatus() == IVideoPlayer.STATUS_PAUSE){
            mVideoPlayer.resume();
        }
    }

    private void pausePlayer() {
        if(mVideoPlayer.isPlaying()){
            mVideoPlayer.pause();
        }
    }

    private void initPlayer() {
        mVideoPlayer.setDecodeMode(DecodeMode.SOFT);
        mVideoPlayer.setViewType(ViewType.SURFACEVIEW);
        mVideoPlayer.useDefaultPlayControl(true);
        mVideoPlayer.setOnPreparedListener(this);
        mVideoPlayer.setOnPlayerInfoListener(this);
        mVideoPlayer.setOnErrorListener(this);
        mVideoPlayer.setOnSeekCompleteListener(this);
        mVideoPlayer.setOnCompletionListener(this);
        mVideoPlayer.setOnSlideHandleListener(this);
    }

    private void findViewById() {
        mVideoPlayer = (XPlayer) findViewById(R.id.player);
        tv_start = (TextView) findViewById(R.id.tv_start);
        tv_pause = (TextView) findViewById(R.id.tv_pause);
        tv_resume = (TextView) findViewById(R.id.tv_resume);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(mVideoPlayer!=null){
            mVideoPlayer.doConfigChange(newConfig);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pausePlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumePlayer();
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
