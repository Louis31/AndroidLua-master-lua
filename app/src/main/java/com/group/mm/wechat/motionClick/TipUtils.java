package com.group.mm.wechat.motionClick;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by 90678 on 2017/8/24.
 */

public class TipUtils {

    public static void showTip (Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }


}
