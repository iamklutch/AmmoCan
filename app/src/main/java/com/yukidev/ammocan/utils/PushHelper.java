package com.yukidev.ammocan.utils;

import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.yukidev.ammocan.R;

/**
 * Created by James on 7/12/2015.
 */
public class PushHelper {

    public void sendPushNotification(String targetId, String message) {
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.whereContains(ParseConstants.KEY_USER_ID, targetId);

        ParsePush push = new ParsePush();
        push.setQuery(query);
        push.setMessage(message);
        push.sendInBackground();

    }
}
