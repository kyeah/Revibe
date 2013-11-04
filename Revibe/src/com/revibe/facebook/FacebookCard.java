package com.revibe.facebook;

import android.util.Log;

import com.afollestad.cardsui.Card;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kyeh on 11/3/13.
 */
public class FacebookCard extends Card {

    private static final String TAG = "FacebookCard";
    private String id, type, subtype;
    private String message;

    private FacebookUser from, to;
    private ArrayList<FacebookUser> likes = new ArrayList<FacebookUser>();
    private String timeCreated;

    public FacebookCard(JSONObject obj) {
        try {
            if (obj.has("from")) {
                JSONObject fromObj = obj.getJSONObject("from");
                from = new FacebookUser(fromObj);
            } if (obj.has("to")) {
                JSONArray toObj = obj.getJSONObject("to").getJSONArray("data");
                to = new FacebookUser(toObj.getJSONObject(0));
            } if (obj.has("message")) {
                message = obj.getString("message");
            } if (obj.has("created_time")) {
                timeCreated = obj.getString("created_time");
            } if (obj.has("type")) {
                type = obj.getString("type");
            } if (obj.has("status_type")) {
                subtype = obj.getString("status_type");
            } if (obj.has("likes")) {
                JSONArray array = obj.getJSONObject("likes").getJSONArray("data");
                    for (int i = 0; i < array.length(); i++)
                        likes.add(new FacebookUser(array.getJSONObject(i)));
            } if (obj.has("id")) {
                id = obj.getString("id");
            }
        } catch (JSONException je) {
            Log.e(TAG, je.getMessage());
        }
    }

    public String getId() { return id; }
    public String getTitle() { return from.getName() + (to == null ? "" : " >> " + to.getName()); }
    public String getContent() { return message; }
    public FacebookUser getFacebookUserFrom() { return from; }
    public FacebookUser getFacebookuserTo() { return to; }
    public String getTimeCreated() { return timeCreated; }
    public ArrayList<FacebookUser> getLikes() { return likes; }

}
