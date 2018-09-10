package com.group.mm.wechat.main;

import android.app.Application;

import org.xutils.x;

/**
 * Created by LouisZ on 2018/8/10.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
    }
}
