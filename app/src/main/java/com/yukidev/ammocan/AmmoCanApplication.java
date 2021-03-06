package com.yukidev.ammocan;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yukidev.ammocan.utils.ParseConstants;

/**
 * AmmoCan, created by YukiDev on 6/18/2015.  All rights reserved.
 */
public class AmmoCanApplication extends Application{
// Enable Local Datastore.

    @Override
    public void onCreate() {
        // Enable Local Datastore.
        super.onCreate();
        // enable crash reporting
        ParseCrashReporting.enable(this);

        // enable local datat storage
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "M1VXE37uOtP4z4P66nDFkDZK1FjkSF2Q3KbPOqKo", "PGYVQUZflvPv114MqirAhbdlm8U4qzHMLADiRUXI");

        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });

        ParseInstallation.getCurrentInstallation().saveInBackground();


    }

    public static void updateParseInstallation(ParseUser user) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
        installation.saveInBackground();
    }

}
