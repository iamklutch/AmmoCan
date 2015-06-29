package com.yukidev.ammocan.utils;

import android.util.Log;

/**
 * Created by James on 6/29/2015.
 */
public class ExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {

    private static final String TAG = ExceptionHandler.class.getSimpleName();

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.d(TAG, ex.getMessage());
    }
}
