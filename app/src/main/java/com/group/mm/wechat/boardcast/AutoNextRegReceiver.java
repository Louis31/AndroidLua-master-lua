package com.group.mm.wechat.boardcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.group.mm.wechat.main.MainActivity;
import com.group.mm.wechat.motionClick.SPUtils;


/**
 * Created by LouisZ on 2018/8/16.
 */

public class AutoNextRegReceiver extends BroadcastReceiver {
    private static final String ACTION = "com.wechat.autonext.reg.broadcast";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)){
            if(MainActivity.instance!=null&& SPUtils.getAutoNext(MainActivity.instance)){
               final int auto = intent.getIntExtra("auto",1);
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            Thread.sleep(1000*5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(SPUtils.getAutoHua(MainActivity.instance)){
                            MainActivity.instance.autonext(auto);
                        }else{
                            MainActivity.instance.autonext(0);
                        }
                        return null;
                    }
                }.execute();


            }
        }
    }
}
