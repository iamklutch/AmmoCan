package com.yukidev.ammocan;

import android.app.Application;
import com.parse.Parse;

/**
 * Created by James on 6/18/2015.
 */
public class AmmoCanApplication extends Application{
// Enable Local Datastore.

    @Override
    public void onCreate() {
        // Enable Local Datastore.
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "M1VXE37uOtP4z4P66nDFkDZK1FjkSF2Q3KbPOqKo", "PGYVQUZflvPv114MqirAhbdlm8U4qzHMLADiRUXI");
    }

}
