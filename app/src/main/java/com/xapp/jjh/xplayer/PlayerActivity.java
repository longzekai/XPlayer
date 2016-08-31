package com.xapp.jjh.xplayer;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.xapp.jjh.base_ijk.config.ViewType;
import com.xapp.jjh.base_ijk.inter.OnErrorListener;
import com.xapp.jjh.base_ijk.inter.OnPlayerEventListener;
import com.xapp.jjh.base_ijk.widget.XPlayer;
import com.xapp.jjh.xplayer.bean.PlayerMenu;
import com.xapp.jjh.xui.activity.TopBarActivity;
import com.xapp.jjh.xui.bean.BaseMenuItem;
import com.xapp.jjh.xui.inter.MenuType;
import com.xapp.jjh.xui.inter.OnMenuItemClickListener;
import java.util.ArrayList;
import java.util.List;


public class PlayerActivity extends TopBarActivity implements OnErrorListener, OnPlayerEventListener {

    private String TAG = "PlayerActivity";
    private XPlayer mXPlayer;
    private String url;

    @Override
    public void parseIntent() {
        super.parseIntent();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        url = getIntent().getStringExtra("path");
        if(TextUtils.isEmpty(url)){
            url = getIntent().getDataString();
        }
    }

    @Override
    public View getContentView(LayoutInflater layoutInflater, ViewGroup container) {
        return inflate(R.layout.activity_main);
    }

    public void setListener() {

    }

    public void findViewById() {
        mXPlayer = findView(R.id.player);
    }

    @Override
    public void initData() {
        setSwipeBackEnable(false);
        setTopBarTitle(getIntent().getStringExtra("name"));
        int decodeMode = getIntent().getIntExtra("decode_mode",0);
        /** 设置解码模式*/
        mXPlayer.setDecodeMode(new PlayerMenu().getDecodeMode(decodeMode));
        /** 设置渲染视图类型*/
        mXPlayer.setViewType(ViewType.SURFACEVIEW);
        /** 是否显示播放帧率等信息*/
        mXPlayer.showTableLayout();
        /** 播放事件监听*/
        mXPlayer.setOnPlayerEventListener(this);
        /** 播放错误监听*/
        mXPlayer.setOnErrorListener(this);
        /** 播放指定的资源*/
        mXPlayer.setData(url);
        /** 启动播放*/
        mXPlayer.start();
        setMenuType(MenuType.TEXT,R.string.setting);
    }

    @Override
    public void onMenuClick() {
        super.onMenuClick();
        List<PlayerMenu> list = new ArrayList<>();
        list.add(new PlayerMenu(-1, mXPlayer.isTableLayoutShow()?getString(R.string.play_info_hidden):getString(R.string.play_info_show)));
        list.add(new PlayerMenu(-1,getString(R.string.video_info)));
        showMenuList(list, new OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(BaseMenuItem menuItem, int position) {
                if(position == 0){
                    if(mXPlayer.isTableLayoutShow()){
                        mXPlayer.setTableLayoutState(false);
                    }else{
                        mXPlayer.setTableLayoutState(true);
                    }
                }else if(position == 1){
                    mXPlayer.showMediaInfo();
                }
            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(mXPlayer !=null){
            Log.d(TAG,"doConfigChanged ... ... ...");
            mXPlayer.doConfigChange(newConfig);
        }
    }

    @Override
    public void onBackPressed() {
        if(mXPlayer.isFullScreen()){
            mXPlayer.toggleFullScreen();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mXPlayer !=null){
            mXPlayer.destroy();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mXPlayer !=null){
            mXPlayer.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mXPlayer !=null){
            mXPlayer.pause();
        }
    }

    @Override
    public void onError(int errorCode) {
        Log.d(TAG,"-------------------ERROR !!!--------------------");
    }

    @Override
    public void onPlayerEvent(int eventCode) {
        switch (eventCode){
            case OnPlayerEventListener.EVENT_CODE_ON_INTENT_TO_START:
                Log.d(TAG,"EVENT_CODE_ON_INTENT_TO_START");
                break;

            case OnPlayerEventListener.EVENT_CODE_PREPARED:
                Log.d(TAG,"EVENT_CODE_PREPARED");
                break;

            case OnPlayerEventListener.EVENT_CODE_RENDER_START:
                Log.d(TAG,"EVENT_CODE_RENDER_START");
                break;

            case OnPlayerEventListener.EVENT_CODE_BUFFERING_START:
                Log.d(TAG,"EVENT_CODE_BUFFERING_START");
                break;

            case OnPlayerEventListener.EVENT_CODE_BUFFERING_END:
                Log.d(TAG,"EVENT_CODE_BUFFERING_END");
                break;

            case OnPlayerEventListener.EVENT_CODE_SEEK_COMPLETE:
                Log.d(TAG,"EVENT_CODE_SEEK_COMPLETE");
                break;

            case OnPlayerEventListener.EVENT_CODE_PLAY_PAUSE:
                Log.d(TAG,"EVENT_CODE_PLAY_PAUSE");
                break;

            case OnPlayerEventListener.EVENT_CODE_PLAY_RESUME:
                Log.d(TAG,"EVENT_CODE_PLAY_RESUME");
                break;

            case OnPlayerEventListener.EVENT_CODE_PLAY_COMPLETE:
                Log.d(TAG,"EVENT_CODE_PLAY_COMPLETE");
                break;

            case OnPlayerEventListener.EVENT_CODE_PLAYER_DESTROY:
                Log.d(TAG,"EVENT_CODE_PLAYER_DESTROY");
                break;
        }
    }
}
