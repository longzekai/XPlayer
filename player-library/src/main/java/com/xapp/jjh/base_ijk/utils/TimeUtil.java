package com.xapp.jjh.base_ijk.utils;

/**
 * ------------------------------------
 * Created by Taurus on 2016/8/3.
 * ------------------------------------
 */
public class TimeUtil {
    /**
     * return time format , for example 00:08:19
     * @param time
     * @return
     */
    public static String getTime(long time){
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

}
