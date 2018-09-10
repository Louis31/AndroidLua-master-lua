package sk.kottman.androlua.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by LouisZ on 2018/7/30.
 */

public class FileAssetsUtil {

    public static String getAssetsW1(Context context, String filename){
        try {
            StringBuffer result = new StringBuffer();
            InputStream inputStream =   context.getAssets().open(filename);
            inputStream.read();
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            boolean fg = true;
            while (fg){
                String line = br.readLine();
                result.append(line+" ");
            }

            return result.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> getAssetsByList(Context context, String filename){
        try {
            List<String> result = new ArrayList<>();
            InputStream inputStream =   context.getAssets().open(filename);
            inputStream.read();
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            boolean fg = true;
            while (fg){
                String line = br.readLine();
                if(line==null){
                    fg = false;
                    break;
                }
                result.add(line);
            }

            return result;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String,String> getAssetspByList(Context context, int filename){
        try {
           // List<String> result = new ArrayList<>();
            HashMap<String,String> pp = new HashMap<>();


            InputStream inputStream =   context.getAssets().open("getprop_"+filename);
            inputStream.read();
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            boolean fg = true;
            while (fg){
                String line = br.readLine();
                if(line==null){
                    fg = false;
                    break;
                }
                line=line.replaceAll("\\[","");
                line=line.replaceAll("\\]","");
                String[] cmds=line.split(": ");

                if(cmds!=null&&cmds.length==2){
                    pp.put(cmds[0].trim(),cmds[1].trim());
                }
                //result.add(line);
            }

            return pp;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
