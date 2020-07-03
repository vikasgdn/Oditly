package com.oditly.audit.inspection.util;

import android.util.Log;

/**
 * Created by Vikas on 4/1/2018.
 */

public class AppLogger {

    private static boolean DEBUG = true;

    public static void d(String tag, String msg){
        if (DEBUG){
            Log.d(tag, ""+msg);
        }
    }
    public static void e(String tag, String msg){
        if (DEBUG){
            Log.e(tag, ""+msg);
        }
    }
    public static void v(String tag, String msg){
        if (DEBUG){
            Log.v(tag, ""+msg);
        }
    }
    public static void i(String tag, String msg){
        if (DEBUG){
            Log.i(tag, ""+msg);
        }
    }
}
