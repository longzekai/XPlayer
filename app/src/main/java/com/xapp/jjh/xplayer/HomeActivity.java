package com.xapp.jjh.xplayer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.xapp.jjh.xplayer.adapter.VideoListAdapter;
import com.xapp.jjh.xplayer.bean.VideoInfo;
import com.xapp.jjh.xplayer.utils.VideoUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Taurus on 16/8/20.
 */
public class HomeActivity extends AppCompatActivity implements VideoListAdapter.OnItemClickListener {

    private RecyclerView mRecycler;
    private List<VideoInfo> mList = new ArrayList<>();

    private final int MSG_LOAD_OVER = 101;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_LOAD_OVER:
                    videoListAdapter = new VideoListAdapter(getApplicationContext(),mList);
                    mRecycler.setAdapter(videoListAdapter);
                    videoListAdapter.setOnItemClickListener(HomeActivity.this);
                    break;
            }
        }
    };
    private VideoListAdapter videoListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mRecycler = (RecyclerView) findViewById(R.id.recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
        new Thread(){
            @Override
            public void run() {
                super.run();
                VideoUtils.getVideos(mList, Environment.getExternalStorageDirectory());
                mHandler.sendEmptyMessage(MSG_LOAD_OVER);
            }
        }.start();
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder holder, int position) {
        String path = mList.get(position).getPath();
        Intent intent = new Intent(getApplicationContext(),PlayerActivity.class);
        intent.putExtra("path",path);
        startActivity(intent);
    }
}
