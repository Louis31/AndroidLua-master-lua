package com.group.mm.wechat.boardcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.group.mm.wechat.main.ShellUtils;

public class AutoClickReciever extends BroadcastReceiver {
    private static final String ACTION = "com.wechat.auto.click.cmd";


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)){
            final String auto = intent.getStringExtra("auto");
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    ShellUtils.execCommand("input tap "+auto,true,false);
                    return null;
                }
            }.execute();
        }
    }
}
