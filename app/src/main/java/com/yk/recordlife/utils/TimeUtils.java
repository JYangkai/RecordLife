package com.yk.recordlife.utils;

public class TimeUtils {

    public static String getTimeStr(long time) {
        if (time < 10) {
            return "00:0" + time;
        }
        if (time < 60) {
            return "00:" + time;
        }
        long timeM = time / 60;
        String timeS = getTimeStr(time % 60).replace("00", "");
        if (timeM < 10) {
            return "0" + timeM + timeS;
        }
        if (timeM < 60) {
            return timeM + timeS;
        }
        return "∞";
    }

}
