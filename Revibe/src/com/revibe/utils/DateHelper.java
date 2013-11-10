package com.revibe.utils;

import android.util.Log;

import java.util.Date;

/**
 * Created by kyeh on 11/9/13.
 */
public class DateHelper {

    public static String timeSince(double date) {
        double seconds = Math.floor(System.currentTimeMillis() - date) / 1000;

        double interval = Math.floor(seconds / 86400);
        if (interval >= 1)
            return new Date((int)date).toString();

        interval = Math.floor(seconds / 3600);
        if (interval == 1)
            return "about an hour ago";
        if (interval > 1)
            return "about " + (int)interval + " hours ago";

        interval = Math.floor(seconds / 60);
        if (interval == 1)
            return "a minute ago";
        if (interval > 1)
            return (int)interval + " minutes ago";

        seconds = Math.floor(seconds);
        if (seconds == 1)
            return "a second ago";

        return (int)seconds + " seconds ago";
    }

}
