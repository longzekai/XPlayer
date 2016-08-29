package com.xapp.jjh.xplayer;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.jiajunhui.xapp.medialoader.bean.VideoItem;
import com.jiajunhui.xapp.medialoader.callback.OnVideoLoaderCallBack;
import com.jiajunhui.xapp.medialoader.loader.MediaLoader;
import com.xapp.jjh.xplayer.adapter.VideoListAdapter;
import com.xapp.jjh.xplayer.bean.PlayerMenu;
import com.xapp.jjh.xui.activity.TopBarActivity;
import com.xapp.jjh.xui.bean.BaseMenuItem;
import com.xapp.jjh.xui.inter.DialogCallBack;
import com.xapp.jjh.xui.inter.MenuType;
import com.xapp.jjh.xui.inter.OnMenuItemClickListener;
import com.xapp.jjh.xui.inter.PageState;
import com.xapp.jjh.xui.lib.permissiongen.PermissionFail;
import com.xapp.jjh.xui.lib.permissiongen.PermissionGen;
import com.xapp.jjh.xui.lib.permissiongen.PermissionSuccess;
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
    private List<VideoItem> mList = new ArrayList<>();

    private View ll_input;
    private EditText et_url;
    private TextView tv_play;

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
        ll_input = findView(R.id.ll_input);
        et_url = findView(R.id.et_url);
        tv_play = findView(R.id.tv_play);
    }

    @Override
    public void initData() {
        setMenuType(MenuType.TEXT,R.string.decode_mode_soft);
        setNavigationVisible(false);
        setSwipeBackEnable(false);
        setTopBarTitle("视频列表");
        mRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
        PermissionGen.with(HomeActivity.this)
                .addRequestCode(100)
                .permissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = 100)
    public void doSomething(){
        setPageState(PageState.LOADING);
        MediaLoader.loadVideos(this, new OnVideoLoaderCallBack() {
            @Override
            public void onResultList(List<VideoItem> list) {
                mList = list;
                Collections.sort(mList,new MCompartor());
                mHandler.sendEmptyMessage(MSG_LOAD_OVER);
            }
        });
    }

    @PermissionFail(requestCode = 100)
    public void doFailSomething(){
        showSnackBar("Permission Deny !",null,null);
    }

    public class MCompartor implements Comparator<VideoItem>{
        @Override
        public int compare(VideoItem lhs, VideoItem rhs) {
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
        tv_play.setOnClickListener(this);
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
        menuList.add(new PlayerMenu(PlayerMenu.DECODE_MODE_NOT_SETTING,-1,ll_input.getVisibility()==View.VISIBLE?"隐藏输入框":"显示输入框"));
        showMenuList(menuList, new OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(BaseMenuItem menuItem, int position) {
                PlayerMenu menu = (PlayerMenu) menuItem;
                if(menu.getDecodeModeCode()!=-1){
                    decode_mode = menu.getDecodeModeCode();
                    setMenuText(menu.getItemText());
                }else{
                    int state = ll_input.getVisibility() == View.VISIBLE?View.GONE:View.VISIBLE;
                    ll_input.setVisibility(state);
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.tv_play:
                String url = et_url.getText().toString();
                if(TextUtils.isEmpty(url))
                    return;
                Intent intent = new Intent(getApplicationContext(),PlayerActivity.class);
                intent.putExtra("path",url);
                intent.putExtra("name",url);
                intent.putExtra("decode_mode",decode_mode);
                startActivity(intent);
                break;
        }
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
