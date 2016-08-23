package com.xapp.jjh.xplayer;

import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xapp.jjh.xui.activity.LoadStateActivity;

/**
 * Created by Taurus on 16/8/23.
 */
public class SplashActivity extends LoadStateActivity {
    @Override
    public View getContentView(LayoutInflater layoutInflater, ViewGroup container) {
        return inflate(R.layout.activity_splash);
    }

    @Override
    public void findViewById() {

    }

    @Override
    public void initData() {
        fullScreen();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(intent);
                backSpace();
            }
        }, 2000);
    }
}
