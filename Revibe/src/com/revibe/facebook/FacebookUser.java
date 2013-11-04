package com.revibe.facebook;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kyeh on 11/4/13.
 */
public class FacebookUser {

    private static final String TAG = "FacebookUser";
    private String id, name;

    public FacebookUser(JSONObject obj) {
        try {
            id =    obj.getString("id");
            name =  obj.getString("name");
        } catch (JSONException je) {
            Log.e(TAG, je.getMessage());
        }
    }

    public String getId() { return id; }
    public String getName() { return name; }

}
