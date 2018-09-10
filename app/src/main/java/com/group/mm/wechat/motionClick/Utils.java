package com.group.mm.wechat.motionClick;

/**
 * Created by 90678 on 2017/7/24.
 */

import android.app.Instrumentation;
import android.content.Context;
import android.os.SystemClock;
import android.view.MotionEvent;

import com.group.mm.wechat.main.MainActivity;
import com.group.mm.wechat.main.ShellUtils;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Utils {
    private static Utils instance;
    private static Context mContext;

    private Utils(Context context) {
        super();
        // TODO Auto-generated constructor stub
        try {
            Runtime.getRuntime().exec("su");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static Utils getInstance(Context context) {
        mContext = context;
        if (instance == null)
            instance = new Utils(context);
        return instance;
    }

//    public void screenshot() {
//        exec("chmod 777 /dev/graphics/fb0 \n cat /dev/graphics/fb0 > /mnt/sdcard/fb0",
//                null);
//    }
//
//    public void sendClick(float x, float y) {
//        String[] orders = {
//                "sendevent /dev/input/event4 0 0 0",
//                "sendevent /dev/input/event4 1 330 1",
//                "sendevent /dev/input/event4 3 53 " + x,
//                "sendevent /dev/input/event4 3 54 " + y,
//                "sendevent /dev/input/event4 0 0 0",
//                "sendevent /dev/input/event4 1 330 0",
//                "sendevent /dev/input/event4 0 0 0",
//                "sendevent /dev/input/event4 0 0 0" };
//        exec("su", orders);
//    }
//
//    public void sendHome() {
//        String[] orders = {
//                "sendevent /dev/input/event1 0 0 0",
//                "sendevent /dev/input/event1 1 102 1",
//                "sendevent /dev/input/event1 0 0 0",
//                "sendevent /dev/input/event1 1 102 0",
//                "sendevent /dev/input/event1 0 0 0",
//                "sendevent /dev/input/event1 0 0 0" };
//        exec("su", orders);
//    }
//
//    private void exec(String cmd, String[] orders) {
//        try {
//            Process process = Runtime.getRuntime().exec(cmd);
//            DataOutputStream dataOut = new DataOutputStream(
//                    process.getOutputStream());
//            if (orders != null) {
//                for (String order : orders)
//                    dataOut.writeBytes(order + ";");
//            }
//            dataOut.flush();
//            dataOut.close();
//            process.waitFor();
//            InputStream in = process.getInputStream();
//            BufferedReader bufferReader = new BufferedReader(
//                    new InputStreamReader(in));
//            BufferedReader err = new BufferedReader(new InputStreamReader(
//                    process.getErrorStream()));
//            String line = null;
//            while ((line = err.readLine()) != null)
//                L.d("1.>>>" + line);
//            while ((line = bufferReader.readLine()) != null)
//                L.d("2.>>>" + line);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//
//        }
//    }

//    execShellCmd("getevent -p");
//    execShellCmd("sendevent /dev/input/event0 1 158 1");
//    execShellCmd("sendevent /dev/input/event0 1 158 0");
//    execShellCmd("input keyevent 3");//home
//    execShellCmd("input text  'helloworld!' ");
//    execShellCmd("input tap 168 252");
//    execShellCmd("input swipe 100 250 200 280");

    /**
     * 执行shell命令
     *
     * @param cmd
     */
//    http://blog.csdn.net/mad1989/article/details/38109689/


    public String excCMD(String cmd){
        try {
            // 申请获取root权限，这一步很重要，不然会没有作用
            //cmd = new String(cmd.getBytes("gb2312"),"gbk" );

            Process process = Runtime.getRuntime().exec("su");
            // 获取输出流
            OutputStream outputStream = process.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(
                    outputStream);

            //dataOutputStream.writeChars(cmd);
            dataOutputStream.write(cmd.getBytes());
            //dataOutputStream.writeBytes(cmd);
            //dataOutputStream.writeUTF(cmd);
            dataOutputStream.flush();
            dataOutputStream.close();
            outputStream.close();
            DataInputStream dis = new DataInputStream(process.getInputStream());

            StringBuilder s = new StringBuilder();
            String str;
            while ((str = dis.readLine()) != null) {
                s.append(str).append("\n");
            }

            return s.toString();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    public void execShellCmd(String cmd) {

        ShellUtils.execCommand(cmd,true);

//        try {
//            // 申请获取root权限，这一步很重要，不然会没有作用
//            //cmd = new String(cmd.getBytes("gb2312"),"gbk" );
//
//            Process process = Runtime.getRuntime().exec("su");
//            // 获取输出流
//            OutputStream outputStream = process.getOutputStream();
//            DataOutputStream dataOutputStream = new DataOutputStream(
//                    outputStream);
//            //dataOutputStream.writeChars(cmd);
//            dataOutputStream.write(cmd.getBytes());
//            //dataOutputStream.writeBytes(cmd);
//            //dataOutputStream.writeUTF(cmd);
//            dataOutputStream.flush();
//            dataOutputStream.close();
//            outputStream.close();
//
//            if(process!=null){
//                process.destroy();
//            }
//        } catch (Throwable t) {
//            t.printStackTrace();
//        }
    }

    public void execShellCmd(int keyCode) {
        execShellCmd("input keyevent " + keyCode);
    }

    public void execShellClickCmd(float x, float y) {
//        execShellCmd("input tap 168 252");
        execShellCmd("input tap " + x + " " + y);
    }

    public void execShellSwipeCmd(float x1, float y1, float x2, float y2) {
//        execShellCmd("input swipe 100 250 200 280");
       // execShellCmd("input touchscreen swipe " + x1 + " " + y1 + " " + x2 + " " + y2+" 2100");

        drag(x1,y1,x2,y2,60);
    }

    public void execShellTouchCmd2(float x, float y) {
        execShellCmd("sendevent  /dev/input/event3 3 50 5 "
                + "sendevent  /dev/input/event3 3 53 " + x
                + "sendevent  /dev/input/event3 3 54 " + y);
    }


    public void execShellTouchCmd(float x, float y) {
//        execShellCmd("input touchscreen "+x+" "+y);
        execShellCmd("sendevent  /dev/input/event3 3 50 5 "
                        + "sendevent  /dev/input/event3 3 53 " + x
                        + "sendevent  /dev/input/event3 3 54 " + y
                        + "sendevent  /dev/input/event3 0 2 0 "//    点击还未进入长点击状态
                        + "sendevent  /dev/input/event3 0 0 0 "
                        + "sendevent  /dev/input/event3 0 2 0 "); //    点击后进入长点击状态

    }

//    http://www.jb51.net/article/88649.htm
//    http://blog.csdn.net/wzystal/article/details/26088987

    public void reboot() {
        try {
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot"}); //重启
            proc.waitFor();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void drag(float fromX, float toX, float fromY, float toY,
                     int stepCount) {
        Instrumentation inst=new Instrumentation();
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        float y = fromY;
        float x = fromX;
        float yStep = (toY - fromY) / stepCount;
        float xStep = (toX - fromX) / stepCount;
        MotionEvent event = MotionEvent.obtain(downTime, eventTime,MotionEvent.ACTION_DOWN, fromX, fromY, 0);
        try {
            inst.sendPointerSync(event);
        } catch (SecurityException ignored) {}
        for (int i = 0; i < stepCount; ++i) {
            y += yStep;
            x += xStep;
            eventTime = SystemClock.uptimeMillis();
            event = MotionEvent.obtain(downTime, eventTime,MotionEvent.ACTION_MOVE, x, y, 0);
            try {
                inst.sendPointerSync(event);
            } catch (SecurityException ignored) {}
        }
        eventTime = SystemClock.uptimeMillis();
        event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP,toX, toY, 0);
        try {
            inst.sendPointerSync(event);
        } catch (SecurityException ignored) {}
    }


    public void shutdown() {
        try {
//Process proc =Runtime.getRuntime().exec(new String[]{"su","-c","shutdown"}); //关机
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot -p"}); //关机
            proc.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    public void reboot () {
//        PowerManager pManager=(PowerManager) mContext.getSystemService(Context.POWER_SERVICE); //重启到fastboot模式
//        pManager.reboot("");
//    }
//
//    public void shutDown () {
//        try {
////获得ServiceManager类
//            Class ServiceManager = Class
//                    .forName("android.os.ServiceManager");
////获得ServiceManager的getService方法
//            Method getService = ServiceManager.getMethod("getService", java.lang.String.class);
////调用getService获取RemoteService
//            Object oRemoteService = getService.invoke(null,Context.POWER_SERVICE);
////获得IPowerManager.Stub类
//            Class cStub = Class
//                    .forName("android.os.IPowerManager$Stub");
////获得asInterface方法
//            Method asInterface = cStub.getMethod("asInterface", android.os.IBinder.class);
////调用asInterface方法获取IPowerManager对象
//            Object oIPowerManager = asInterface.invoke(null, oRemoteService);
////获得shutdown()方法
//            Method shutdown = oIPowerManager.getClass().getMethod("shutdown",boolean.class,boolean.class);
////调用shutdown()方法
//            shutdown.invoke(oIPowerManager,false,true);
//        } catch (Exception e) {
//            Log.e(TAG, e.toString(), e);
//        }
//    }
}