package com.app.download.utils;

import android.util.Log;

public class Logger {

    public static final boolean DEBUG = true;

    public static void debug(String tag, String message) {
        if (DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void error(String tag, String message) {
        if (DEBUG) {
            Log.e(tag, message);
        }
    }


    public static void info(String tag, String message) {
        if (DEBUG) {
            Log.i(tag, message);
        }
    }

    public static void warn(String tag, String message) {
        if (DEBUG) {
            Log.w(tag, message);
        }
    }
}
