package com.xapp.jjh.xplayer;

import android.app.Dialog;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.xapp.jjh.xplayer.adapter.VideoListAdapter;
import com.xapp.jjh.xplayer.bean.PlayerMenu;
import com.xapp.jjh.xplayer.bean.VideoInfo;
import com.xapp.jjh.xplayer.utils.VideoUtils;
import com.xapp.jjh.xui.activity.TopBarActivity;
import com.xapp.jjh.xui.bean.BaseMenuItem;
import com.xapp.jjh.xui.inter.DialogCallBack;
import com.xapp.jjh.xui.inter.MenuType;
import com.xapp.jjh.xui.inter.OnMenuItemClickListener;
import com.xapp.jjh.xui.inter.PageState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Taurus on 16/8/20.
 */
public class HomeActivity extends TopBarActivity implements VideoListAdapter.OnItemClickListener {

    private String TAG = "HomeActivity";
    private RecyclerView mRecycler;
    private List<VideoInfo> mList = new ArrayList<>();

    private int decode_mode = 0;

    private final int MSG_LOAD_OVER = 101;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_LOAD_OVER:
                    setPageState(PageState.SUCCESS);
                    if(mList.size()<=0){
                        showSnackBar("No Videos",null,null);
                        return;
                    }
                    videoListAdapter = new VideoListAdapter(getApplicationContext(),mList);
                    mRecycler.setAdapter(videoListAdapter);
                    videoListAdapter.setOnItemClickListener(HomeActivity.this);
                    break;
            }
        }
    };
    private VideoListAdapter videoListAdapter;

    @Override
    public View getContentView(LayoutInflater layoutInflater, ViewGroup container) {
        return inflate(R.layout.activity_home);
    }

    @Override
    public void findViewById() {
        mRecycler = findView(R.id.recycler);
    }

    @Override
    public void initData() {
        setMenuType(MenuType.TEXT,R.string.decode_mode_soft);
        setNavigationVisible(false);
        setSwipeBackEnable(false);
        setTopBarTitle("视频列表");
        mRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
        setPageState(PageState.LOADING);
        new Thread(){
            @Override
            public void run() {
                super.run();
                VideoUtils.getVideos(mList, Environment.getExternalStorageDirectory());
                Collections.sort(mList,new MCompartor());
                mHandler.sendEmptyMessage(MSG_LOAD_OVER);
            }
        }.start();
    }

    public class MCompartor implements Comparator<VideoInfo>{
        @Override
        public int compare(VideoInfo lhs, VideoInfo rhs) {
            if(lhs.getSize()>rhs.getSize()){
                return -1;
            }
            if(lhs.getSize()<rhs.getSize()){
                return 1;
            }
            return 0;
        }
    }

    @Override
    public void setListener() {
        super.setListener();
//        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                Log.d(TAG,"Home onGlobalLayout ......");
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        showDialog("Sure ? ", new DialogCallBack() {
            @Override
            public void onRightClick(Dialog dialog) {
                backSpace();
            }
        });
    }

    @Override
    public void onMenuClick() {
        super.onMenuClick();
        List<PlayerMenu> menuList = new ArrayList<>();
        menuList.add(new PlayerMenu(PlayerMenu.DECODE_MODE_CODE_SOFT,-1,"软解码"));
        menuList.add(new PlayerMenu(PlayerMenu.DECODE_MODE_CODE_HARD,-1,"硬解码"));
        menuList.add(new PlayerMenu(PlayerMenu.DECODE_MODE_CODE_MEDIA_PLAYER,-1,"原生解码"));
        showMenuList(menuList, new OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(BaseMenuItem menuItem, int position) {
                PlayerMenu menu = (PlayerMenu) menuItem;
                decode_mode = menu.getDecodeModeCode();
                setMenuText(menu.getItemText());
            }
        });
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder holder, int position) {
        String path = mList.get(position).getPath();
        String name = mList.get(position).getDisplayName();
        Intent intent = new Intent(getApplicationContext(),PlayerActivity.class);
        intent.putExtra("path",path);
        intent.putExtra("name",name);
        intent.putExtra("decode_mode",decode_mode);
        startActivity(intent);
    }
}
