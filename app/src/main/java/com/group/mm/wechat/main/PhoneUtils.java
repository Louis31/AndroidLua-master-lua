package com.group.mm.wechat.main;

import java.util.Random;

/**
 * Created by LouisZ on 2018/7/26.
 */

public class PhoneUtils {
    // 拦截 xposed  等软件检测
    public static String CM = "qwertyuioplkjhgfdsazxcvbnm123456789";
    public static String getIMEI(){
        Random random = new Random();
        String result="";
        for (int i = 0;i<15;i++){
            int d=random.nextInt(9);
            result=result+""+d;

        }
        return result;
    }

    // 拦截 xposed  等软件检测
    public static String getIM(int ld){
        Random random = new Random();
        String result="";
        for (int i = 0;i<ld;i++){
            int d=random.nextInt(9);
            result=result+""+d;

        }
        return result;
    }

    // 拦截 xposed  等软件检测
    public static String getS(int ld){
        Random random = new Random();
        String result="";
        for (int i = 0;i<ld;i++){
            int d=random.nextInt(CM.length()-1);
            result=result+""+CM.charAt(d);

        }
        return result;
    }

    private static String SEPARATOR_OF_MAC = ":";

    /**
     * Generate a random MAC address for qemu/kvm
     * 52-54-00 used by qemu/kvm
     * The remaining 3 fields are random,  range from 0 to 255
     *
     * @return MAC address string
     */
    public static String randomMac4Qemu() {
        Random random = new Random();
        String[] mac = {
                String.format("%02x", 0x52),
                String.format("%02x", 0x54),
                String.format("%02x", 0x00),
                String.format("%02x", random.nextInt(0xff)),
                String.format("%02x", random.nextInt(0xff)),
                String.format("%02x", random.nextInt(0xff))
        };

        //String.join(SEPARATOR_OF_MAC, mac);
        return mac[0]+SEPARATOR_OF_MAC+mac[1]+SEPARATOR_OF_MAC+mac[2]+SEPARATOR_OF_MAC+mac[3]+SEPARATOR_OF_MAC+mac[4]+SEPARATOR_OF_MAC+mac[5];
    }
}
