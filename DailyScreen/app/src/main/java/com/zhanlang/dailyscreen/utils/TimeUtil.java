package com.zhanlang.dailyscreen.utils;

/**
 * Created by SX on 2017/10/17.
 */

public class TimeUtil {

    //秒转化为 时分秒的字符串

    public String secToHMS(long seconds){
        String  timeStr = null;
        int hour = 0;
        int minute =0;
        int second = 0;
        if (seconds <= 0) return "00:00";
        else {
            minute = (int)seconds/60;
            if (minute<60){//不满一小时
                second = (int) seconds%60;
                 timeStr = unitFormat(minute) +":"+unitFormat(second);
            }else {//超过一小时
                 hour = minute/60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = (int)seconds - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }

        }

        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }


}
