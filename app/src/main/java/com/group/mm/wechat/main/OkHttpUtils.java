package com.group.mm.wechat.main;

/**
 * Created by LouisZ on 2018/8/11.
 */
import android.os.Handler;
import android.os.Message;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpUtils {

    private OKHttpGetListener onOKHttpGetListener;
    private MyHandler myHandler=new MyHandler();

    public void get(String url){
        OkHttpClient client = new OkHttpClient();
        //创建请求对象
        Request request = new Request.Builder().url(url).build();
        //创建Call请求队列
        Call call = client.newCall(request);
        //开始请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message=myHandler.obtainMessage();
                message.obj="请求失败";
                message.what=0;
                myHandler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message message=myHandler.obtainMessage();
                String json = response.body().string();
                message.obj=json;
                message.what=1;
                myHandler.sendMessage(message);
            }
        });
    }
    //使用接口回调,将数据返回
    public interface OKHttpGetListener{
        void error(String error);
        void success(String json);
    }
    //给外部调用的方法
    public void setOnOKHttpGetListener(OKHttpGetListener onOKHttpGetListener){
        this.onOKHttpGetListener=onOKHttpGetListener;
    }
    //使用Handler，将数据在主线程返回
    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            int w=msg.what;
            if(w==0){
                String error = (String) msg.obj;
                onOKHttpGetListener.error(error);
            }
            if(w==1){
                String json = (String) msg.obj;
                onOKHttpGetListener.success(json);
            }
        }
    }
}

