package com.group.mm.wechat.boardcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.group.mm.wechat.main.MainActivity;


/**
 * Created by LouisZ on 2018/8/14.
 */

public class AutoHuaRe extends BroadcastReceiver {

    private static final String ACTION = "com.wechat.autodevicep0.broadcast";

    @Override
    public void onReceive(Context context, final Intent intent) {
        if (intent.getAction().equals(ACTION)){
            if(MainActivity.instance!=null){
                int type = intent.getIntExtra("type",494);
                MainActivity.instance.executeLuaStatemanetSudu(type);
                Toast.makeText(context,""+ACTION+" success",Toast.LENGTH_LONG);
            }else{
                Toast.makeText(context,""+ACTION+" none",Toast.LENGTH_LONG);
            }

        }
    }
}
