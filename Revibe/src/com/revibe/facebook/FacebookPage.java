package com.revibe.facebook;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by kyeh on 11/4/13.
 */
public class FacebookPage extends FacebookActor {

    private static HashMap<String, FacebookPage> userLRUCache = new HashMap<String, FacebookPage>();

    private static final String TAG = "FacebookPage";
    private String id, name, profilePicUrl;

    public static FacebookPage addToCache(FacebookPage user) {
        return userLRUCache.put(user.getId(), user);
    }

    public static FacebookPage getFromCache(String id) {
        return userLRUCache.get(id);
    }

    public static boolean contains(String id) {
        return userLRUCache.containsKey(id);
    }

    public FacebookPage(JSONObject obj) {
        try {
            id =            obj.getString("page_id");
            name =          obj.getString("name");
            profilePicUrl = obj.getString("pic_square");
        } catch (JSONException je) {
            Log.e(TAG, je.getMessage());
        }
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getProfilePicUrl() { return profilePicUrl; }

}
