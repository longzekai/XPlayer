package com.xapp.jjh.xplayer.bean;

import java.io.Serializable;

/**
 * Created by Taurus on 16/8/20.
 */
public class VideoInfo implements Serializable{
    private String displayName;
    private String path;
    private long size;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String disPlayName) {
        this.displayName = disPlayName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

}
