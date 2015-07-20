package com.yukidev.ammocan.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by James on 6/14/2015.
 */
public class DateHelper {

    public static final String TAG = DateHelper.class.getSimpleName();

    public String DateChangerThreeCharMonth() {
        Calendar c = Calendar.getInstance();
        int Year = c.get(Calendar.YEAR);
        int Month = c.get(Calendar.MONTH);
        int Day = c.get(Calendar.DATE);

        String newDate = Day + "/" + (Month + 1) + "/" + Year;

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date dateObject;
        String date = "";

        try {
            dateObject = formatter.parse(newDate);
            date = new SimpleDateFormat("d MMM yyyy").format(dateObject);
        }
        catch (java.text.ParseException e) {
            Log.d(TAG, "Error: " + e);
        }
        return date;
    }


}
