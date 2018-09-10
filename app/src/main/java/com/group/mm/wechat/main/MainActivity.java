package com.group.mm.wechat.main;

import android.app.Activity;
import android.app.UiAutomation;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.net.vpn.AuthenticationActor;
import android.net.vpn.PptpProfile;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.android.uiautomator.core.InstrumentationUiAutomatorBridge;
import com.android.uiautomator.core.UiDevice;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.group.mm.wechat.eventBusUtil.EventBusConstants;

import com.group.mm.wechat.motionClick.GPhoneBean;
import com.group.mm.wechat.motionClick.RandomString;
import com.group.mm.wechat.motionClick.SPUtils;
import com.group.mm.wechat.motionClick.TipUtils;
import com.group.mm.wechat.motionClick.Utils;
import com.group.mm.wechat.windows.Helper;
import com.group.mm.wechat.windows.TopTipService;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.keplerproject.luajava.LuaException;
import org.keplerproject.luajava.LuaState;
import org.keplerproject.luajava.LuaStateFactory;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

import sk.kottman.androlua.R;
import sk.kottman.androlua.service.RootVpnService;
import sk.kottman.androlua.utils.RootPermission;
import us.shandian.vpn.MainActivity1;
import us.shandian.vpn.manager.VpnManager;
import us.shandian.vpn.manager.VpnProfile;
import us.shandian.vpn.util.RunCommand;

public class MainActivity extends Activity {
    public static  MainActivity instance;
    private EditText etIpSet,main_shouji,main_1;
    private Button btIpSet;
    private CheckBox main_auto_next,main_auto_hua,main_auto_timeline;
    private Button main_jiaoben,main_auto_device,main_auto_reg,main_no_hua_reg;

    private LuaState mLuaState;//Lua解析和执行由此对象完成
    final StringBuilder output = new StringBuilder();

    private ArrayList<String> cmds = new ArrayList<>();

    private AuthenticationActor authenticationActor;

    private  ContentResolver cr;

    private int wid = 720;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mm);
        instance = this;
        etIpSet = (EditText) findViewById(R.id.main_ip_set_et);
        main_shouji = (EditText) findViewById(R.id.main_shouji);
        main_1 = (EditText) findViewById(R.id.main_1);
        btIpSet = (Button) findViewById(R.id.main_ip_set_bt);
        main_jiaoben = (Button) findViewById(R.id.main_jiaoben);
        main_auto_device = (Button) findViewById(R.id.main_auto_device);
        main_no_hua_reg = (Button) findViewById(R.id.main_no_hua_reg);
        main_auto_reg = (Button) findViewById(R.id.main_auto_reg);
        main_auto_next = (CheckBox) findViewById(R.id.main_auto_next);
        main_auto_hua = (CheckBox) findViewById(R.id.main_auto_hua);
        main_auto_timeline = (CheckBox) findViewById(R.id.main_auto_timeline);
        wid = Width();

        Intent intent = new Intent(MainActivity.this, TopTipService.class);
        startService(intent);
        //Utils.getInstance(MainActivity1.instance).execShellCmd("");
        main_1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(main_1.getText().toString().length()==1){
                    SPUtils.setType(MainActivity.this,Integer.valueOf(main_1.getText().toString()));
                }
            }
        });
        main_auto_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent("com.wechat.autodevice.broadcast");
                sendBroadcast(intent1);
            }
        });

        main_no_hua_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(main_shouji.getText()!=null&&main_shouji.getText().toString().length()>10){
                    final String tel1 = main_shouji.getText().toString();
                    EventBus.getDefault().post(new EventBusConstants.TipChangeEvent(tel1,1));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String cmds=readStream(getResources().openRawResource(R.raw.wechat_zhuce_no_hua));
                            try {
                                cmds = cmds.replaceAll("18613141314",tel1);
                                RandomString rs = new RandomString();
                                String sdf = rs.generateString(new Random(),RandomString.SOURCES,6);
                                cmds = cmds.replaceAll("18000000000",sdf);
                                sdf = rs.generateString(new Random(),RandomString.SOURCES1,12);
                                //cmds = cmds.replaceAll("mmabbcj1314",sdf);
                                cmds = cmds.replaceAll("--ei ty 0","--ei ty "+SPUtils.getType(MainActivity.this));
                                JSONArray jsonArray = new JSONArray(cmds);
                                MainActivity.instance.setVpn();
                                Thread.sleep(5000);
                                for (int i=0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    int delay = jsonObject.getInt("delay");
                                    String cmd = jsonObject.getString("cmd");
                                    if(delay>0){
                                        Thread.sleep(delay);
                                    }
                                    Utils.getInstance(MainActivity.this).execShellCmd(cmd);
                                    if(cmd.equals("input tap 680 280")){
                                        if(MainActivity.isVpnUsed()){
                                            if(delay>0){
                                                Thread.sleep(delay);
                                            }
                                            Utils.getInstance(MainActivity.this).execShellCmd(cmd);
                                        }
                                    }


                                }
                            }catch (Exception e){

                            }
                        }
                    }).start();
                }else{

                    if(SPUtils.getType(MainActivity.this)==1){
                        getExp1(1);
                    }else if(SPUtils.getType(MainActivity.this)==2){
                        TipUtils.showTip(MainActivity.this, " sp"+SPUtils.getType(MainActivity.this));
                        getExp2(1);
                    }else if(SPUtils.getType(MainActivity.this)==5){
                        getExp5(0);
                    }else{
                        getExp(1);
                    }
                }
            }
        });

        main_auto_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(main_shouji.getText()!=null&&main_shouji.getText().toString().length()>10){
                   final String tel1 = main_shouji.getText().toString();
                    EventBus.getDefault().post(new EventBusConstants.TipChangeEvent(tel1,1));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String cmds=readStream(getResources().openRawResource(R.raw.wechat_zhuce));
                            try {
                                cmds = cmds.replaceAll("18613141314",tel1);
                                RandomString rs = new RandomString();
                                String sdf = rs.generateString(new Random(),RandomString.SOURCES,6);
                                cmds = cmds.replaceAll("18000000000",sdf);
                                sdf = rs.generateString(new Random(),RandomString.SOURCES1,12);
                                //cmds = cmds.replaceAll("mmabbcj1314",sdf);
                                cmds = cmds.replaceAll("--ei ty 0","--ei ty "+SPUtils.getType(MainActivity.this));
                                JSONArray jsonArray = new JSONArray(cmds);
                                MainActivity.instance.setVpn();
                                Thread.sleep(5000);
                                for (int i=0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    int delay = jsonObject.getInt("delay");
                                    String cmd = jsonObject.getString("cmd");
                                    if(delay>0){
                                        Thread.sleep(delay);
                                    }
                                    Utils.getInstance(MainActivity.this).execShellCmd(cmd);
                                    if(cmd.equals("input tap 680 280")){
                                        if(MainActivity.isVpnUsed()){
                                            if(delay>0){
                                                Thread.sleep(delay);
                                            }
                                            Utils.getInstance(MainActivity.this).execShellCmd(cmd);
                                        }
                                    }
                                }
                            }catch (Exception e){

                            }
                        }
                    }).start();
                }else{

                    if(SPUtils.getType(MainActivity.this)==1){
                        getExp1(0);
                    }else if(SPUtils.getType(MainActivity.this)==2){
                        TipUtils.showTip(MainActivity.this, " sp"+SPUtils.getType(MainActivity.this));
                        getExp2(0);
                    }else if(SPUtils.getType(MainActivity.this)==5){
                        getExp5(0);
                    }else{
                        getExp(0);
                    }

                }
            }
        });
        //VpnManager.startVpn(new VpnProfile());
        PptpProfile sd = new PptpProfile();
        sd.setId("100");
        sd.setServerName("b.vpn.cn");
        sd.setEncryptionEnabled(false);
        sd.setName("XP");
        authenticationActor = new AuthenticationActor(MainActivity.this, sd);
//        btIpSet.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            VpnManager.open_fly();
//                            String sff = VpnManager.get_vpn_enable();
//                            try {
//                                Thread.sleep(1000*2);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            VpnManager.close_fly();
//                            Thread.sleep(1000*10);
//                            MainActivity.instance.setVpn();
//                            Thread.sleep(1000*5);
//
//                            VpnManager.click_vpn();
//                            if("1".equals(sff)){
//                                Thread.sleep(1000*5);
//                                VpnManager.click_vpn();
//
//                            }
//                            Thread.sleep(1000*5);
//                           String sffw = VpnManager.get_vpn_enable();
//                           Log.d("LLL","sffw:"+sffw);
//                            if("0".equals(sffw.trim())){
//                                Thread.sleep(1000*3);
//                                Log.d("LLL","sffw:   sleep");
//                                VpnManager.click_vpn();
//                            }else{
//                                Log.d("LLL","sffw:   none");
//                            }
//
//
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();
//            }
//        });

        btIpSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentResolver cr = getContentResolver();
                boolean on = Settings.System.getInt(cr, Settings.System.AIRPLANE_MODE_ON, 0) != 0;
                if(on){
                    Toast.makeText(MainActivity.this,"on",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(MainActivity.this,"false",Toast.LENGTH_LONG).show();
                }
            }
        });
        //Save();
        //CPU();

        main_jiaoben.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setVpn(MainActivity1.this);
                Intent vpnIntent = new Intent();
                vpnIntent.setAction("android.net.vpn.SETTINGS");
                startActivity(vpnIntent);
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        String cmds=readStream(getResources().openRawResource(R.raw.wechat_zhuce));
//                        try {
//
//                            JSONArray jsonArray = new JSONArray(cmds);
//                            for (int i=0; i < jsonArray.length(); i++) {
//                                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                                int delay = jsonObject.getInt("delay");
//                                String cmd = jsonObject.getString("cmd");
//                                if(delay>0){
//                                    Thread.sleep(delay);
//                                }
//                                Utils.getInstance(MainActivity1.this).execShellCmd(cmd);
//                            }
//                        }catch (Exception e){
//
//                        }
//                    }
//                }).start();

            }
        });
        main_auto_next.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtils.setAutoNext(MainActivity.instance,isChecked);
            }
        });
        main_auto_timeline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtils.setAutoTimeLine(MainActivity.this,isChecked);
            }
        });
        main_auto_hua.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtils.setAutoHua(MainActivity.instance,isChecked);
            }
        });
        initView();
//        requestDevPermission();
        requestDevPermission();
        initLua();

        isVpnUsed();


    }

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_OK) {
//            String prefix = getPackageName();
//            Intent intent = new Intent(this, RootVpnService.class)
//                    .putExtra(prefix + ".ADDRESS", "b.vpn.cn")
//                    .putExtra(prefix + ".PORT", "1732")
//                    .putExtra(prefix + ".SECRET", "louis:123456");
//            startService(intent);
//        }
//    }
    private int auto;
    public void getExp(final int auto){
        RequestParams requestParams = new RequestParams("http://merchant.shangyipin.net/openapi.action?method=GetPhone&account=a3562031&pwd=3562031&pId=288&carr=&q=1");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Type type = new TypeToken<GPhoneBean>(){}.getType();
                Gson gson = new Gson();
                GPhoneBean gp  =gson.fromJson(result,type);
                if(gp!=null&&gp.getC()==1&&gp.getPhones()!=null&&gp.getPhones().length==1){
                    //
                    final String teld = gp.getPhones()[0];
                    EventBus.getDefault().post(new EventBusConstants.TipChangeEvent(teld,1));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String cmds=readStream( MainActivity.instance.getResources().openRawResource(R.raw.wechat_zhuce));
                            if(auto!=0){
                                cmds=readStream( MainActivity.instance.getResources().openRawResource(R.raw.wechat_zhuce_no_hua));
                            }
                            try {
                                cmds = cmds.replaceAll("18613141314",teld);
                                RandomString rs = new RandomString();
                                String sdf = rs.generateString(new Random(),RandomString.SOURCES,6);
                                cmds = cmds.replaceAll("18000000000",sdf);
                                String mpass = rs.generatePString(new Random(),RandomString.SOURCES1,12);
                                cmds = cmds.replaceAll("mmabbcj1314",mpass);
                                cmds = cmds.replaceAll("--ei ty 0","--ei ty "+SPUtils.getType(MainActivity.this)+" --es mps "+mpass);
                                JSONArray jsonArray = new JSONArray(cmds);
                                MainActivity.instance.setVpn();
                                Thread.sleep(5000);
                                for (int i=0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    int delay = jsonObject.getInt("delay");
                                    String cmd = jsonObject.getString("cmd");
                                    if(delay>0){
                                        Thread.sleep(delay);
                                    }
                                    //Utils.getInstance(MainActivity1.this).execShellCmd(cmd);

                                    Utils.getInstance(MainActivity.instance).execShellCmd(cmd);
                                    if(cmd.equals("input tap 680 280")){
                                        if(MainActivity.isVpnUsed()){
                                            if(delay>0){
                                                Thread.sleep(delay);
                                            }
                                            Utils.getInstance(MainActivity.instance).execShellCmd(cmd);
                                        }
                                    }
                                }
                            }catch (Exception e){

                            }
                        }
                    }).start();
                }else{
                    Toast.makeText(MainActivity.instance,"获取失败",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(MainActivity.instance,"获取失败",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }



    private void reback(String tel){
        OkHttpUtils okHttpUtils = new OkHttpUtils();
        okHttpUtils.get("http://119.28.14.154/wxrebot/api/rebackVPN.php?wxphone="+tel);
        okHttpUtils.setOnOKHttpGetListener(new OkHttpUtils.OKHttpGetListener() {
            @Override
            public void error(String error) {

            }

            @Override
            public void success(String json) {

            }
        });
    }



    public void getExp2(final int auto){
        Log.d("xposed","getExp2");
        // http://47.106.253.197:9600/wxapi/cust/getActiveTelephone?cpid=0c2e836cca7a4dd796103bdcfb859a95&province=%E5%B9%BF%E4%B8%9C     %E6%B9%96%E5%8D%97
        RequestParams requestParams = new RequestParams("http://47.106.253.197:9600/wxapi/cust/getActiveTelephone?cpid=0c2e836cca7a4dd796103bdcfb859a95&province=%E6%B9%96%E5%8D%97");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if(object.has("code")){
                        if("00".equals(object.getString("code"))){
                            JSONObject  data =  object.getJSONObject("data");
                            if(data!=null){
                                if(data.has("telephone")){
                                    final String teld = data.getString("telephone");
                                    EventBus.getDefault().post(new EventBusConstants.TipChangeEvent(teld,1));
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String cmds=readStream(MainActivity.instance.getResources().openRawResource(R.raw.wechat_zhuce));
                                            if(auto!=0){
                                                cmds=readStream(MainActivity.instance.getResources().openRawResource(R.raw.wechat_zhuce_no_hua));
                                            }
                                            try {
                                                cmds = cmds.replaceAll("18613141314",teld);
                                                RandomString rs = new RandomString();
                                                String sdf = rs.generateString(new Random(),RandomString.SOURCES,6);
                                                cmds = cmds.replaceAll("18000000000",sdf);
                                                String mpass = rs.generatePString(new Random(),RandomString.SOURCES1,12);
                                                cmds = cmds.replaceAll("mmabbcj1314",mpass);
                                                cmds = cmds.replaceAll("--ei ty 0","--ei ty 2 --es mps "+mpass);
                                                JSONArray jsonArray = new JSONArray(cmds);
                                                MainActivity.instance.setVpn();
                                                Thread.sleep(5000);
                                                for (int i=0; i < jsonArray.length(); i++) {
                                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                                    int delay = jsonObject.getInt("delay");
                                                    String cmd = jsonObject.getString("cmd");
                                                    if(delay>0){
                                                        Thread.sleep(delay);
                                                    }
                                                    //Utils.getInstance(MainActivity1.this).execShellCmd(cmd);
                                                    if(cmd.startsWith("am start -n com.tencent.mm")){
                                                        if(!MainActivity.isVpnUsed()){
                                                            Toast.makeText(MainActivity.this,"VPN 未启动 暂停注册",Toast.LENGTH_LONG).show();
                                                            EventBus.getDefault().post(new EventBusConstants.TipChangeEvent(teld,2));
                                                            reback(teld);
                                                            break;
                                                        }
                                                    }
                                                    Utils.getInstance(MainActivity.this).execShellCmd(cmd);
                                                    if(cmd.equals("input tap 900 360")){
                                                        if(MainActivity.isVpnUsed()){
                                                            if(delay>0){
                                                                Thread.sleep(delay);
                                                            }
                                                            Utils.getInstance(MainActivity.instance).execShellCmd(cmd);
                                                        }
                                                    }
                                                }
                                            }catch (Exception e){

                                            }
                                        }
                                    }).start();
                                }
                            }else{
                                Toast.makeText(MainActivity.instance,"获取失败 3",Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(MainActivity.instance,"获取失败 3",Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(MainActivity.instance,"获取失败 3"+ex.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    public void getExp1(final int auto){
        RequestParams requestParams = new RequestParams("http://119.28.14.154/wxrebot/api/gettel.php");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Type type = new TypeToken<GPhoneBean>(){}.getType();
                Gson gson = new Gson();
                GPhoneBean gp  =gson.fromJson(result,type);
                if(gp!=null&&gp.getC()==1&&gp.getPhones()!=null&&gp.getPhones().length==1){
                    //
                    final String teld = gp.getPhones()[0];
                    EventBus.getDefault().post(new EventBusConstants.TipChangeEvent(teld,1));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String cmds=readStream(MainActivity.instance.getResources().openRawResource(R.raw.wechat_zhuce));
                            if(auto!=0){
                                cmds=readStream(MainActivity.instance.getResources().openRawResource(R.raw.wechat_zhuce_no_hua));
                            }
                            try {
                                cmds = cmds.replaceAll("18613141314",teld);
                                RandomString rs = new RandomString();
                                String sdf = rs.generateString(new Random(),RandomString.SOURCES,6);
                                String mpass = rs.generatePString(new Random(),RandomString.SOURCES1,12);
                                cmds = cmds.replaceAll("18000000000",sdf);
                                cmds = cmds.replaceAll("--ei ty 0","--ei ty "+SPUtils.getType(MainActivity.instance)+" --es mps "+mpass);
                                cmds = cmds.replaceAll("mmabbcj1314",mpass);
                                JSONArray jsonArray = new JSONArray(cmds);

                                MainActivity.instance.setVpn();
                                Thread.sleep(5000);
                                for (int i=0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    int delay = jsonObject.getInt("delay");
                                    String cmd = jsonObject.getString("cmd");
                                    if(delay>0){
                                        Thread.sleep(delay);
                                    }
                                    //Utils.getInstance(MainActivity1.this).execShellCmd(cmd);
                                    if(cmd.startsWith("am start -n com.tencent.mm")){
                                        if(!MainActivity.isVpnUsed()){
                                            Toast.makeText(MainActivity.this,"VPN 未启动 暂停注册",Toast.LENGTH_LONG).show();
                                            EventBus.getDefault().post(new EventBusConstants.TipChangeEvent(teld,2));
                                            break;
                                        }
                                    }
                                    Utils.getInstance(MainActivity.this).execShellCmd(cmd);
                                    if(cmd.equals("input tap 680 280")){
                                        if(MainActivity.isVpnUsed()){
                                            if(delay>0){
                                                Thread.sleep(delay);
                                            }
                                            Utils.getInstance(MainActivity.instance).execShellCmd(cmd);
                                        }
                                    }
                                }
                            }catch (Exception e){

                            }
                        }
                    }).start();
                }else{
                    Toast.makeText(MainActivity.instance,"获取失败",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(MainActivity.instance,"获取失败",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    public void getExp15(final int auto){
        Log.d("LLL","getExp15");
        this.auto=auto;

        RequestParams requestParams = new RequestParams("http://119.28.14.154/wxrebot/api/gettel5.php");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Type type = new TypeToken<GPhoneBean>(){}.getType();
                Gson gson = new Gson();
                GPhoneBean gp  =gson.fromJson(result,type);
                if(gp!=null&&gp.getC()==5&&gp.getPhones()!=null&&gp.getPhones().length==1){
                    //
                    final String teld = gp.getPhones()[0];
                    EventBus.getDefault().post(new EventBusConstants.TipChangeEvent(teld,1));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String cmds=readStream(MainActivity.instance.getResources().openRawResource(R.raw.wechat_zhuce_1080));
                            if(auto!=0){
                                cmds=readStream(MainActivity.instance.getResources().openRawResource(R.raw.wechat_zhuce_no_hua_1080));
                            }
                            try {
                                cmds = cmds.replaceAll("18613141314",teld);
                                RandomString rs = new RandomString();
                                String sdf = rs.generateString(new Random(),RandomString.SOURCES,6);
                                String mpass = rs.generatePString(new Random(),RandomString.SOURCES1,12);
                                cmds = cmds.replaceAll("18000000000",sdf);
                                cmds = cmds.replaceAll("--ei ty 0","--ei ty 5 --es mps "+mpass);
                                cmds = cmds.replaceAll("mmabbcj1314",mpass);
                                JSONArray jsonArray = new JSONArray(cmds);
                                VpnManager.open_fly();
                                String sff = VpnManager.get_vpn_enable();
                                try {
                                    Thread.sleep(1000*2);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                VpnManager.close_fly();
                                Thread.sleep(1000*10);
                                MainActivity.instance.setVpn();
                                Thread.sleep(1000*5);

                                VpnManager.click_vpn(true);
                                if("1".equals(sff)){
                                    Thread.sleep(1000*5);
                                    VpnManager.click_vpn(true);
                                }
                                Thread.sleep(1000*5);
                                String sffw = VpnManager.get_vpn_enable();
                                Log.d("LLL","sffw:"+sffw);
                                if("0".equals(sffw.trim())){
                                    Thread.sleep(1000*3);
                                    Log.d("LLL","sffw:   sleep");
                                    VpnManager.click_vpn(true);
                                }else{
                                    Log.d("LLL","sffw:   none");
                                }
                                for (int i=0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    int delay = jsonObject.getInt("delay");
                                    String cmd = jsonObject.getString("cmd");
                                    if(delay>0){
                                        Thread.sleep(delay);
                                    }
                                    //Utils.getInstance(MainActivity1.this).execShellCmd(cmd);
                                    if(cmd.startsWith("am start -n com.tencent.mm")){
                                        if(!VpnManager.isVpnRunning()){
                                            Log.d("LLL","isVpnUsed not");
                                            Toast.makeText(MainActivity.this,"VPN 未启动 暂停注册",Toast.LENGTH_LONG).show();
                                            EventBus.getDefault().post(new EventBusConstants.TipChangeEvent(teld,2));
                                            reback(teld);
                                            break;
                                        }else{
                                            Log.d("LLL","isVpnUsed");
                                        }
                                    }

                                    if(cmd.equals("input tap 900 360")){
//                                        if(VpnManager.isVpnRunning()){
//                                            VpnManager.stopVpn();
//                                        }
                                        //VpnManager.startVpn(new VpnProfile());
//                                        if(MainActivity2.isVpnUsed()){
//                                            if(delay>0){
//                                                Thread.sleep(delay);
//                                            }
//                                            Utils.getInstance(MainActivity2.instance).execShellCmd(cmd);
//                                        }
                                    }else{
                                        Utils.getInstance(MainActivity.this).execShellCmd(cmd);
                                    }
                                }
                            }catch (Exception e){

                            }
                        }
                    }).start();
                }else{
                    Toast.makeText(MainActivity.instance,"获取失败",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(MainActivity.instance,"获取失败",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    public void getExp5(final int auto){
        if(wid!=720){
            getExp15(auto);
            return;
        }
        this.auto=auto;
        RequestParams requestParams = new RequestParams("http://119.28.14.154/wxrebot/api/gettel5.php");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Type type = new TypeToken<GPhoneBean>(){}.getType();
                Gson gson = new Gson();
                GPhoneBean gp  =gson.fromJson(result,type);
                if(gp!=null&&gp.getC()==5&&gp.getPhones()!=null&&gp.getPhones().length==1){
                    //
                    final String teld = gp.getPhones()[0];
                    EventBus.getDefault().post(new EventBusConstants.TipChangeEvent(teld,1));
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            String cmds=readStream(MainActivity.instance.getResources().openRawResource(R.raw.wechat_zhuce));
                            if(auto!=0){
                                cmds=readStream(MainActivity.instance.getResources().openRawResource(R.raw.wechat_zhuce_no_hua));
                            }
                            try {
                                cmds = cmds.replaceAll("18613141314",teld);
                                RandomString rs = new RandomString();
                                String sdf = rs.generateString(new Random(),RandomString.SOURCES,6);
                                String mpass = rs.generatePString(new Random(),RandomString.SOURCES1,12);
                                cmds = cmds.replaceAll("18000000000",sdf);
                                cmds = cmds.replaceAll("--ei ty 0","--ei ty 5 --es mps "+mpass);
                                cmds = cmds.replaceAll("mmabbcj1314",mpass);
                                JSONArray jsonArray = new JSONArray(cmds);
                                MainActivity.instance.setVpn();
                                Thread.sleep(5000);

                                VpnManager.click_vpn();
                                Thread.sleep(10000);
                                if(!VpnManager.isVpnRunning()){
                                    VpnManager.click_vpn();
                                    Thread.sleep(3000);
                                }
                                ShellUtils.execCommand("input keyevent 4",true);
                                for (int i=0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    int delay = jsonObject.getInt("delay");
                                    String cmd = jsonObject.getString("cmd");
                                    if(delay>0){
                                        Thread.sleep(delay);
                                    }

//                                    if(cmd.startsWith("am start -n com.tencent.mm")){
//                                        if(isVpnUsed()){
//                                            Log.d("LLL","!isVpnRunning:"+cmd);
//                                            //Toast.makeText(MainActivity.this,"VPN 未启动 暂停注册",Toast.LENGTH_LONG).show();
//                                            //EventBus.getDefault().post(new EventBusConstants.TipChangeEvent(teld,2));
//                                           // reback(teld);
//                                           // break;
//                                        }
//                                    }

                                    if(cmd.equals("input tap 680 280")){
                                        //Log.d("LLL","input tap 680 280");

                                    }else{
                                        Log.d("LLL","PP:"+cmd);
                                        //RunCommand.run(cmd);
                                        // com.group.mm.wechat.main.RootPermission.rootPermission(cmd);
                                        //Utils.getInstance(MainActivity.this).execShellCmd(cmd);

                                        ShellUtils.execCommand(cmd,true
                                                ,true);
                                    }
                                }
                            }catch (Exception e){
                                Log.d("LLL","exception :"+e.getLocalizedMessage());
                            }
                            return null;
                        }
                    }.execute();
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                        }
//                    }).start();
                }else{
                    Toast.makeText(MainActivity.instance,"获取失败",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(MainActivity.instance,"获取失败",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    public void autonext(int auto){

        if(SPUtils.getType(MainActivity.instance)==1){
            getExp5(auto);
        }else if(SPUtils.getType(MainActivity.this)==2){
            getExp5(auto);
        }else if(SPUtils.getType(MainActivity.this)==5){
            getExp5(auto);
        }else{
            getExp5(auto);
        }
    }

    public static boolean isVpnUsed() {
        try {
            Enumeration<NetworkInterface> niList = NetworkInterface.getNetworkInterfaces();
            if(niList != null) {
                for (NetworkInterface intf : Collections.list(niList)) {
                    if(!intf.isUp() || intf.getInterfaceAddresses().size() == 0) {
                        continue;
                    }
                    Log.d("LLL:xposed", "isVpnUsed() NetworkInterface Name: " + intf.getName());
                    if ("tun0".equals(intf.getName()) || "ppp0".equals(intf.getName())|| "ppp100".equals(intf.getName())){
                        return true; // The VPN is up
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setFLY(){
        Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
        startActivity(intent);
    }

    public void setVpn(){
        try {
            Intent vpnIntent = new Intent();
            vpnIntent.setAction("android.net.vpn.SETTINGS");
            startActivity(vpnIntent);

//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(2000);
//                        Utils.getInstance(MainActivity.instance).execShellClickCmd(910, 206);
//                        Thread.sleep(2000);
//                        ContentResolver cr = getContentResolver();
//                        boolean on = Settings.System.getInt(cr, Settings.System.AIRPLANE_MODE_ON, 0) != 0;
//                        if(on){
                           // Utils.getInstance(MainActivity.instance).execShellClickCmd(910, 206);
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }).start();

//            Intent intent = null;
//            if(android.os.Build.VERSION.SDK_INT>10){
//                intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS );
//            }else{
//                intent = new Intent();
//                ComponentName component = new ComponentName("com.android.settings","com.android.settings.WirelessSettings");
//                intent.setComponent(component);
//                intent.setAction("android.intent.action.VIEW");
//            }
//            MainActivity1.instance.startActivity(intent);
        } catch (Exception e) {

            return ;
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

    private void initView() {
        String ip = SPUtils.getIp(this);
        if (TextUtils.isEmpty(ip)) {
            return;
        }
        etIpSet.setText(ip);
        SPUtils.setType(this,5);
        main_1.setText(""+5);
        main_auto_next.setChecked(SPUtils.getAutoNext(this));
        main_auto_hua.setChecked(SPUtils.getAutoHua(this));
        //startServices();
    }

    private void startServices() {
       // startService(new Intent(this, MainService.class));
    }









    public void executeLuaStatemanetSudu(int t){
        int x0=84;
        int y0=704;

        int x1 = t;//494  // 514  //544
        int y1=712;

        int timess = (int)1000*3;
        int sf = 50;

        String luaStr1 = "system.init()\n" +
                "print(os.time())\n" +
                "system.sleep(500)\n" +
                "system.touchDown("+x0+", "+y0+")\n" +
                "system.sleep(20)\n";
        StringBuffer sbf = new StringBuffer();
        Random rd = new Random();
        int yy = x0;
        int yi = 0;

        int step = 130;
        int Qt = (int)(step*0.6);
        int Mt = (int)(step*0.27);
        int st = step - Qt - Mt;
        int sd = 1;

        int mm = x1 - x0;
        int mm_st = mm/step;
        int tc = 0;
        while (sd<step){
            int mmmm = 60;
            if(tc<=Qt){
                mmmm = 7;
            }else if(tc>Qt&&tc<(Qt+Mt)){
                mmmm = 7*2;
            }else{
                mmmm = 7*5;
            }
            sd++;
            tc += mm_st;

            sbf.append("system.touchScroll("+(x0+tc)+", "+(704+rd.nextInt(30))+")\n");
            sbf.append("system.sleep("+mmmm+")");
        }
        sbf.append("system.touchScroll("+(x1)+", "+(704+rd.nextInt(30))+")\n");
        sbf.append("system.sleep(20)");

        final String luaStr=luaStr1+sbf.toString()+"system.sleep(10)\n" +
                "system.touchUp("+x1+", 714)\n" +
                "system.close()\n";// 定义一个Lua变量

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String res = evalLua(luaStr);
                } catch (LuaException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void restartRG(final String teld){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String cmds=readStream(MainActivity.instance.getResources().openRawResource(R.raw.wechat_zhuce));
                if(auto!=0){
                    cmds=readStream(MainActivity.instance.getResources().openRawResource(R.raw.wechat_zhuce_no_hua));
                }
                try {
                    cmds = cmds.replaceAll("18613141314",teld);
                    RandomString rs = new RandomString();
                    String sdf = rs.generateString(new Random(),RandomString.SOURCES,6);
                    String mpass = rs.generatePString(new Random(),RandomString.SOURCES1,12);
                    cmds = cmds.replaceAll("18000000000",sdf);
                    cmds = cmds.replaceAll("--ei ty 0","--ei ty 5 --es mps "+mpass);
                    cmds = cmds.replaceAll("mmabbcj1314",mpass);
                    JSONArray jsonArray = new JSONArray(cmds);
//                                MainActivity2.instance.setVpn();
//                                Thread.sleep(5000);

                    for (int i=0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int delay = jsonObject.getInt("delay");
                        String cmd = jsonObject.getString("cmd");
                        if(delay>0){
                            Thread.sleep(delay);
                        }
                        //Utils.getInstance(MainActivity1.this).execShellCmd(cmd);
                        if(cmd.startsWith("am start -n com.tencent.mm")){
                            if(!VpnManager.isVpnRunning()){
//                                Log.d("LLL","isVpnUsed not");
//                                Toast.makeText(MainActivity.this,"VPN 未启动 暂停注册",Toast.LENGTH_LONG).show();
//                                EventBus.getDefault().post(new EventBusConstants.TipChangeEvent(teld,2));
//                                reback(teld);
//                                break;
                            }else{
                                Log.d("LLL","isVpnUsed");
                            }
                        }

                        if(cmd.equals("input tap 900 360")){
//                                        if(VpnManager.isVpnRunning()){
//                                            VpnManager.stopVpn();
//                                        }
                            //VpnManager.startVpn(new VpnProfile());
//                                        if(MainActivity2.isVpnUsed()){
//                                            if(delay>0){
//                                                Thread.sleep(delay);
//                                            }
//                                            Utils.getInstance(MainActivity2.instance).execShellCmd(cmd);
//                                        }
                        }else{
                            Utils.getInstance(MainActivity.this).execShellCmd(cmd);
                        }
                    }
                }catch (Exception e){

                }
            }
        }).start();
    }

    public void restartNetwork(){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        VpnManager.open_fly();
                        String sff = VpnManager.get_vpn_enable();
                        try {
                            Thread.sleep(1000*2);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        VpnManager.close_fly();
                        Thread.sleep(1000*10);
                        MainActivity.instance.setVpn();
                        Thread.sleep(1000*5);

                        VpnManager.click_vpn();
                        if("1".equals(sff)){
                            Thread.sleep(1000*5);
                            VpnManager.click_vpn();
                        }
                        Thread.sleep(1000*5);

                        Utils.getInstance(MainActivity.this).execShellCmd("input keyevent 4");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
    }

    /**
     * 只是在第一次调用，如果升级脚本也不需要重复初始化
     */
    private void initLua(){
        if(mLuaState==null) {
            mLuaState = LuaStateFactory.newLuaState();
            mLuaState.openLibs();
            //为了lua能使用系统日志，传入Log
            try {
                //push一个对象到对象到栈中
                mLuaState.pushObjectValue(Log.class);
                //设置为全局变量
                mLuaState.setGlobal("Log");
            } catch (LuaException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    String evalLua(final String src) throws LuaException {
        mLuaState.setTop(0);

        int ok = mLuaState.LloadString(src);
        if (ok == 0) {
            mLuaState.getGlobal("debug");
            mLuaState.getField(-1, "traceback");
            mLuaState.remove(-2);
            mLuaState.insert(-2);
            ok = mLuaState.pcall(0, 0, -2);
            if (ok == 0) {
                String res = output.toString();
                output.setLength(0);
                Log.e("test", res);
                return res;
            }
        }
        throw new LuaException(errorReason(ok) + ": " + mLuaState.toString(-1));
    }

    private String errorReason(int error) {
        switch (error) {
            case 4:
                return "Out of memory";
            case 3:
                return "Syntax error";
            case 2:
                return "Runtime error";
            case 1:
                return "Yield error";
        }
        return "Unknown error " + error;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLuaState != null && !mLuaState.isClosed()) {
            //只能在退出应用时才调用
            mLuaState.close();
        }
    }

    private void requestDevPermission() {

        String[] commands = new String[12];
        commands[0] = "setenforce 0" + "\n";
        for (int i = 1; i < 10; i++) {
            commands[i] = "chmod 777 /dev/input/event" + i + "\n";
        }

        commands[10] = "chmod 777 /dev/uinput" + "\n";
        commands[11] = "chmod 777 /dev/graphics/fb0" + "\n";
        RootPermission.rootPermission(commands);

    }

    void startVPN(String name) {
        Intent i=new Intent("doenter.onevpn.ACTION_CONNECT");
        i.putExtra("name",name);
        i.putExtra("force", true);
        i.putExtra("force_same", false);
        startActivity(i);
    }

    void restartVPN(String name) {
        Intent i=new Intent("doenter.onevpn.ACTION_CONNECT");
        i.putExtra("name",name);
        i.putExtra("force", true);
        i.putExtra("force_same", true);
        startActivity(i);
    }

    void stopVPN() {
        Intent i=new Intent("doenter.onevpn.ACTION_DISCONNECT");
        // Stops any VPN regardless of name
        startActivity(i);
    }

    public void executeLuaStatemanetSudu1080( int t){
        int x0=203;//
        int y0=1100;

        int x1 = t;//494  // 514  //544
        int y1=y0+60;// 710  1100
        int timess = (int)1000*3;
        int sf = 50;

        String luaStr1 = "system.init()\n" +
                "print(os.time())\n" +
//                "system.back()\n" +
                "system.sleep(600)\n" +
                "system.touchDown("+x0+", "+y0+")\n" +
                "system.sleep(10)\n";
        StringBuffer sbf = new StringBuffer();
        Random rd = new Random();
        int yy = x0;
        int yi = 0;

        int step = 110;
        Random sdf = new Random();
        int sfg=sdf.nextInt(5)+66;
        int Qt = (int)(step*(sfg*0.01));
        int Mt = (int)(step*0.17);
        int st = step - Qt - Mt;
        int sd = 1;

        int mm = x1 - x0;
        int mm_st = mm/step;
        int tc = 0;
        while (sd<step){
            int mmmm = 60;
            if(tc<=Qt){
                mmmm = rd.nextInt(4)+1;
            }else if(tc>Qt&&tc<(Qt+Mt)){
                mmmm = 7*3;
            }else{
                mmmm = 10;
            }
            sd++;
            tc += mm_st;
            //mmmm = 0;
            sbf.append("system.touchScroll("+(x0+tc)+", "+(y0+rd.nextInt(30))+","+sd+")\n");

            sbf.append("system.sleep("+mmmm+")\n");
        }
        sbf.append("system.sleep("+100+")\n");
        sbf.append("system.touchScroll("+(x1+30)+", "+(y0+rd.nextInt(30))+",0)\n");
        sbf.append("system.sleep("+300+")\n");
        sbf.append("system.touchScroll("+(x1+20)+", "+(y0+rd.nextInt(30))+",0)\n");
        sbf.append("system.sleep("+200+")\n");
        sbf.append("system.touchScroll("+(x1+10)+", "+(y0+rd.nextInt(30))+",0)\n");
        sbf.append("system.sleep("+80+")\n");
        sbf.append("system.touchScroll("+(x1)+", "+(y0+rd.nextInt(30))+",0)\n");


        final String luaStr=luaStr1+sbf.toString()+"system.sleep(10)\n" +
                "system.touchUp("+x1+", 714)\n" +
                "system.sleep(10)\n" +
                "system.close()\n";// 定义一个Lua变量
        try {
            String res = evalLua(luaStr);
        } catch (LuaException e) {
            e.printStackTrace();
        }

    }

    public void executeLuaStatemanetSudu2( int t){
        if(wid==1080){
            executeLuaStatemanetSudu1080(t);
            return;
        }
        int x0=154;//  203
        int y0=704;
        if(t==800){
            t = 494+70;
        }
        if(t==840){
            t = 514+70;
        }
        if(t==890){
            t = 544+70;
        }
        if(t==780){
            t = 474+70;
        }
        int x1 = t;//494  // 514  //544
        int y1=y0+60;// 710  1100
        int timess = (int)1000*3;
        int sf = 50;

        String luaStr1 = "system.init()\n" +
                "print(os.time())\n" +
//                "system.back()\n" +
                "system.sleep(600)\n" +
                "system.touchDown("+x0+", "+y0+")\n" +
                "system.sleep(10)\n";
        StringBuffer sbf = new StringBuffer();
        Random rd = new Random();
        int yy = x0;
        int yi = 0;

        int step = 110;
        Random sdf = new Random();
        int sfg=sdf.nextInt(5)+66;
        int Qt = (int)(step*(sfg*0.01));
        int Mt = (int)(step*0.17);
        int st = step - Qt - Mt;
        int sd = 1;

        int mm = x1 - x0;
        int mm_st = mm/step;
        int tc = 0;
        while (sd<step){
            int mmmm = 60;
            if(tc<=Qt){
                mmmm = rd.nextInt(4)+1;
            }else if(tc>Qt&&tc<(Qt+Mt)){
                mmmm = 7*3;
            }else{
                mmmm = 10;
            }
            sd++;
            tc += mm_st;
            //mmmm = 0;
            sbf.append("system.touchScroll("+(x0+tc)+", "+(y0+rd.nextInt(30))+","+sd+")\n");

            sbf.append("system.sleep("+mmmm+")\n");
        }
        sbf.append("system.sleep("+100+")\n");
        sbf.append("system.touchScroll("+(x1+30)+", "+(y0+rd.nextInt(30))+",0)\n");
        sbf.append("system.sleep("+300+")\n");
        sbf.append("system.touchScroll("+(x1+20)+", "+(y0+rd.nextInt(30))+",0)\n");
        sbf.append("system.sleep("+200+")\n");
        sbf.append("system.touchScroll("+(x1+10)+", "+(y0+rd.nextInt(30))+",0)\n");
        sbf.append("system.sleep("+80+")\n");
        sbf.append("system.touchScroll("+(x1)+", "+(y0+rd.nextInt(30))+",0)\n");


        final String luaStr=luaStr1+sbf.toString()+"system.sleep(10)\n" +
                "system.touchUp("+x1+", 714)\n" +
                "system.sleep(10)\n" +
                "system.close()\n";// 定义一个Lua变量
        try {
            String res = evalLua(luaStr);
        } catch (LuaException e) {
            e.printStackTrace();
        }

    }

    private void open_fly(){
        Settings.System.putString(cr,Settings.System.AIRPLANE_MODE_ON, "1");
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        sendBroadcast(intent);
    }

    private void close_fly(){
        Settings.System.putString(cr,Settings.System.AIRPLANE_MODE_ON, "0");
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        sendBroadcast(intent);
    }

    /**
     * 返回系统屏幕的高度（像素单位）
     */
    public int Height() {
        int height = 0;
        DisplayMetrics dm = getResources().getDisplayMetrics();
        height = dm.heightPixels;
        return height;
    }

    /**
     * 返回系统屏幕的宽度（像素单位）
     */
    public int Width() {
        int width = 0;
        DisplayMetrics dm = getResources().getDisplayMetrics();
        width = dm.widthPixels;
        return width;
    }
}
