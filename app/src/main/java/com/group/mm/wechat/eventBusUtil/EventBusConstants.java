package com.group.mm.wechat.eventBusUtil;

/**
 * Created by 90678 on 2017/8/4.
 */

public class EventBusConstants {

    public static class IsOpenScreenRecode {
    }
    public static class ScreenRecodeStart {
    }
    public static class ScreenRecodeStop {
    }
    public static class StopAllService {
    }
    public static class StartAllService {
    }
    public static class SocketError {
    }

    public static class TipChangeEvent{
        public String tip;

        public int type;

        public TipChangeEvent(String mtip,int mtype){
            tip = mtip;
            type = mtype;
        }


    }

}
