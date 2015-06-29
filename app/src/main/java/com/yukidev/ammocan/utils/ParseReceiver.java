package com.yukidev.ammocan.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

/**
 * Created by James on 6/27/2015.
 */
public class ParseReceiver extends ParsePushBroadcastReceiver {

    //  this class is to prevent the notification from
    // opening a new instance of the app if its already running.

    @Override
    public void onPushOpen(Context context, Intent intent) {
        Log.e("Push", "Clicked");
        Intent notificationIntent = new Intent();
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
    }
}
