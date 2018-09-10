package com.group.mm.wechat.boardcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import com.group.mm.wechat.main.MainActivity;
import com.group.mm.wechat.motionClick.SPUtils;
import com.group.mm.wechat.motionClick.Utils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import sk.kottman.androlua.R;

/**
 * Created by LouisZ on 2018/8/17.
 */

public class AutoAddFriendReceiver extends BroadcastReceiver {
    private static final String ACTION = "com.wechat.auto.add.friend";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)){
           if(SPUtils.getAutoTimeLine(context)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String cmds=readStream(MainActivity.instance.getResources().openRawResource(R.raw.wechat_add_timline));
                    try {
                        JSONArray jsonArray = new JSONArray(cmds);
                        for (int i=0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            int delay = jsonObject.getInt("delay");
                            String cmd = jsonObject.getString("cmd");
                            if(delay>0){
                                try {
                                    Thread.sleep(delay);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            Utils.getInstance(MainActivity.instance).execShellCmd(cmd);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        }else{
            if(MainActivity.instance!=null&& SPUtils.getAutoNext(MainActivity.instance)){
                int auto = intent.getIntExtra("auto",1);
                if(SPUtils.getAutoHua(MainActivity.instance)){
                    MainActivity.instance.autonext(auto);
                }else{
                    MainActivity.instance.autonext(0);
                }
            }
        }
    }

    private String readStream(InputStream is)
    {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while (i != -1)
            {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            Log.e("ReadStream", "读取文件流失败");
            return "";
        }
    }
}
