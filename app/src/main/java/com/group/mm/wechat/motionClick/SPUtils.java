package com.group.mm.wechat.motionClick;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 90678 on 2017/8/24.
 */

public class SPUtils {

    public static final String IP = "ip";
    public static final String RType = "r_type";
    public static final String AutoNext = "auto_next";
    public static final String AutoHua = "auto_hua";
    public static final String AutoTimeLine = "auto_timeline";
    private static SharedPreferences getSp (Context context) {
        SharedPreferences sp = context.getSharedPreferences("GCSP", Context.MODE_PRIVATE);
        return sp;
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        SharedPreferences.Editor edit = getSp(context).edit();
        return edit;
    }

    public static void setIp (Context context, String ip) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(IP, ip);
        editor.apply();
    }

    public static void setAutoNext (Context context, boolean type) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(AutoNext, type);
        editor.apply();
    }

    public static void setAutoHua (Context context, boolean hua) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(AutoHua, hua);
        editor.apply();
    }

    public static void setAutoTimeLine (Context context, boolean hua) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(AutoTimeLine, hua);
        editor.apply();
    }



    public static void setType (Context context, int type) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putInt(RType, type);
        editor.apply();
    }

    public static int getType (Context context) {
        int ip = getSp(context).getInt(RType, 0);
        return ip;
    }

    public static boolean getAutoNext (Context context) {
        boolean ip = getSp(context).getBoolean(AutoNext, false);
        return ip;
    }

    public static boolean getAutoHua (Context context) {
        boolean ip = getSp(context).getBoolean(AutoHua, false);
        return ip;
    }

    public static boolean getAutoTimeLine (Context context) {
        boolean ip = getSp(context).getBoolean(AutoTimeLine, false);
        return ip;
    }

    public static String getIp (Context context) {
        String ip = getSp(context).getString(IP, "");
        return ip;
    }
}
