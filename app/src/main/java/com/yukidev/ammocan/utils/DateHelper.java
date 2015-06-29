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

        String newDate = (Day + 1) + "/" + (Month + 1) + "/" + Year;

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

    public int DateYearStripper (String stringDate) {
        int year = 0;
        // format like this becuase you are pulling it from an editText where it's formatted like that
        DateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        Date dateObject;

        try {
            dateObject = formatter.parse(stringDate);
            year = dateObject.getYear() + 1900;
        }
        catch (java.text.ParseException e) {
            Log.d(TAG, "Error: " + e);
        }
        return year;
    }

    public int DateMonthStripper (String stringDate) {
        int month = 0;
        // format like this becuase you are pulling it from an editText where it's formatted like that
        DateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        Date dateObject;

        try {
            dateObject = formatter.parse(stringDate);
            month = dateObject.getMonth();
        }
        catch (java.text.ParseException e) {
            Log.d(TAG, "Error: " + e);
        }
        return month;
    }

    public int DateDayStripper (String stringDate) {
        int day = 0;
        // format like this becuase you are pulling it from an editText where it's formatted like that
        DateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        Date dateObject;

        try {
            dateObject = formatter.parse(stringDate);
            day = dateObject.getDate();
        }
        catch (java.text.ParseException e) {
            Log.d(TAG, "Error: " + e);
        }
        return day;
    }

    public String AddMonths (String stringDate) {
        DateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        Date oldDate;
        String date = "";

        try {
            oldDate = formatter.parse(stringDate);
            oldDate.setMonth(oldDate.getMonth() + 3);
            date = new SimpleDateFormat("d MMM yyyy").format(oldDate);
        }
        catch (java.text.ParseException e) {
            Log.d(TAG, "Error: " + e);
        }
        return date;
    }
}
