package com.group.mm.wechat.boardcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonObject;
import com.group.mm.wechat.motionClick.MouseBean;
import com.group.mm.wechat.motionClick.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LouisZ on 2018/8/6.
 */

public class HuaBroadReceiver extends BroadcastReceiver {

    int deviceWidth=720, deviceHeight=1280;
    private static final String ACTION = "com.wechat.autohua.broadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        final Utils utils = Utils.getInstance(context);
        if (intent.getAction().equals(ACTION)){
            Log.d("xposed","广播 自动滑块");



        }
    }

    private void dohua(String jsonv,Utils utils){
        try {
            JSONObject touch =  new JSONObject(jsonv);

            float x = Float.parseFloat(touch.getString(MouseBean.X_DP)) * deviceWidth;
            float y = Float.parseFloat(touch.getString(MouseBean.Y_DP)) * deviceHeight;
            String xdp2 = touch.getString(MouseBean.X_DP2);
            float x2 = 0;
            if (!TextUtils.isEmpty(xdp2)) {
                x2 = Float.parseFloat(xdp2) * deviceWidth;
            }
            String ydp2 = touch.getString(MouseBean.Y_DP2);
            float y2 = 0;
            if (!TextUtils.isEmpty(ydp2)) {
                y2 = Float.parseFloat(ydp2) * deviceHeight;
            }
            utils.execShellSwipeCmd(x, y, x2, y2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
