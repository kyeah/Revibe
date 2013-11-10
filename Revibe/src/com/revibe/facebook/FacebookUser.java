package com.revibe.facebook;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by kyeh on 11/4/13.
 */
public class FacebookUser extends FacebookActor {

    private static HashMap<String, FacebookUser> userLRUCache = new HashMap<String, FacebookUser>();

    private static final String TAG = "FacebookUser";
    private String id, name, profilePicUrl;

    public static FacebookUser addToCache(FacebookUser user) {
        return userLRUCache.put(user.getId(), user);
    }

    public static FacebookUser getFromCache(String id) {
        return userLRUCache.get(id);
    }

    public static boolean contains(String id) {
        return userLRUCache.containsKey(id);
    }

    public FacebookUser(JSONObject obj) {
        try {
            id =            obj.getString("uid");
            name =          obj.getString("name");
            profilePicUrl = obj.getString("pic");
        } catch (JSONException je) {
            Log.e(TAG, je.getMessage());
        }
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getProfilePicUrl() { return profilePicUrl; }

}
