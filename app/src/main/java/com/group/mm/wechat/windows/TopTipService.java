package com.group.mm.wechat.windows;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
//
//import com.group.mm.wechat.R;
//import com.group.mm.wechat.eventBusUtil.EventBusConstants;

import com.group.mm.wechat.eventBusUtil.EventBusConstants;
import com.group.mm.wechat.main.MainActivity;
import com.group.mm.wechat.motionClick.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import sk.kottman.androlua.R;

/**
 * Created by LouisZ on 2018/7/29.
 */

public class TopTipService extends Service {

    private WindowManager wm;

    private View view;
    private LinearLayout top;
    private TextView tip,wx;
    private int type;
    private String tipV;
    private Button btn_error;
    private String tel;
    private ImageView operation;
    //布局参数.
    WindowManager.LayoutParams params;

    @Override
    public void onCreate() {
        super.onCreate();
        wm = (WindowManager) getApplicationContext().getSystemService(
                WINDOW_SERVICE);
        EventBus.getDefault().register(this);
        showWindow();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void tipchangeHandel(EventBusConstants.TipChangeEvent change){
        if(change!=null&&change.tip!=null){
            tel = change.tip;
            tip.setText("注册号:"+change.tip);
            if(change.type!=1){
                tip.setText("注册号(暂停 vpn未启动):"+change.tip);
            }
            type = change.type;
            tipV = change.tip;
        }
    }

    private void showWindow(){
        view = LayoutInflater.from(TopTipService.this).inflate(R.layout.flow_wechat_layout,null);
        tip = (TextView) view.findViewById(R.id.wx_tip);
        wx = (TextView) view.findViewById(R.id.wx);
        operation = (ImageView) view.findViewById(R.id.operation);
        btn_error = (Button)view.findViewById(R.id.btn_error);
        btn_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] items = new String[] { "暂停", "继续", "重制网络", "取消" };
                Utils.getInstance(MainActivity.instance).execShellCmd("am broadcast -a com.wechat.autodevicep.broadcast_1 --ei type 0 ");
                // 创建对话框构建器
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.instance);
                // 设置参数
                builder.setTitle("提示")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                Toast.makeText(getApplicationContext(), items[which],
                                        Toast.LENGTH_SHORT).show();
                                switch (which){
                                    case 0:{
                                        Utils.getInstance(MainActivity.instance).execShellCmd("am broadcast -a com.wechat.autodevicep.broadcast_1 --ei type 1 ");
                                    }
                                    break;
                                    case 1:{
                                        if(MainActivity.instance!=null&&tel!=null){
                                            MainActivity.instance.restartRG(tel);
                                        }
                                    }
                                    break;
                                    case 2:{
                                        if(MainActivity.instance!=null){
                                            MainActivity.instance.restartNetwork();
                                        }
                                    }
                                    break;
                                    default:{
                                        Utils.getInstance(MainActivity.instance).execShellCmd("am broadcast -a com.wechat.autodevicep.broadcast_1 --ei type 0 ");

                                        break;
                                    }
                                }
                            }
                        });
                builder.create().show();
            }
        });
        operation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wx.getVisibility()==View.GONE){
                    wx.setVisibility(View.VISIBLE);
                }else{
                    wx.setVisibility(View.GONE);
                }
            }
        });

        params = new WindowManager.LayoutParams();

        //设置type.系统提示型窗口，一般都在应用程序窗口之上.
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        //设置效果为背景透明.
        params.format = PixelFormat.RGBA_8888;
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        //设置窗口初始停靠位置.
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.x = 0;
        params.y = 0;

        //设置悬浮窗口长宽数据.
        params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.height = 50;

        wm.addView(view,params);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
