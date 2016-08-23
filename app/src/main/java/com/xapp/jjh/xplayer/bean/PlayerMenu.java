package com.xapp.jjh.xplayer.bean;

import android.graphics.Color;

import com.xapp.jjh.xui.bean.BaseMenuItem;

/**
 * Created by Taurus on 2016/8/23.
 */
public class PlayerMenu extends BaseMenuItem {
    public PlayerMenu() {
    }

    public PlayerMenu(int iconId, String itemText) {
        super(iconId, itemText);
    }

    @Override
    public int getItemPartLineColor() {
        return Color.parseColor("#22000000");
    }

    @Override
    public int getItemTextColor() {
        return Color.parseColor("#444444");
    }
}
