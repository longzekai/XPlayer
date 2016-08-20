package com.xapp.jjh.xplayer.application;

import com.xapp.jjh.xui.application.XUIApplication;
import com.xapp.jjh.xui.config.XUIConfig;

/**
 * Created by Taurus on 16/8/20.
 */
public class XPlayerApplication extends XUIApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        XUIConfig.setXUIRedStyle();
    }
}
