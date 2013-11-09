package com.revibe.facebook;

import android.os.Bundle;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Session;

/**
 * Created by kyeh on 11/9/13.
 */
public class FQLHelper {

    public static void request(String query, Request.Callback callback) {
        Bundle params = new Bundle();
        params.putString("q", query);
        Session session = Session.getActiveSession();
        Request request = new Request(session,
                "/fql",
                params,
                HttpMethod.GET,
                callback);

        Request.executeBatchAsync(request);
    }

    public static void request(Request request, Request.Callback callback) {
        request.setCallback(callback);
        Request.executeBatchAsync(request);
    }

}
