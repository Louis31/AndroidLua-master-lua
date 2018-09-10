package sk.kottman.androlua;

import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.group.mm.wechat.main.MainActivity;
import com.group.mm.wechat.motionClick.SPUtils;

public class AutoHuaBoardCast extends BroadcastReceiver {
    private static final String ACTION = "com.wechat.autohua.broadcast_lua";
    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
           // Toast.makeText(MainActivity.instance.getApplicationContext(),"sd",Toast.LENGTH_LONG).show();
            final int sc = intent.getIntExtra("type",494);

           // List<String> cmds =FileAssetsUtil.getAssetsByList(MainActivity1.instance,"w1");
//            if(MainActivity.instance!=null){
               new AsyncTask<Void,Void,Void>(){

                   @Override
                   protected Void doInBackground(Void... voids) {

                       if(SPUtils.getAutoHua(context)){
                        MainActivity.instance.executeLuaStatemanetSudu2(sc);
                       }
                       return null;
                   }
               }.execute();

            Log.d("LLL", "广播 自动滑块："+sc);
//            }

        }
    }


}
