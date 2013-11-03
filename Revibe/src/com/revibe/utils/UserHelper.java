package com.revibe.utils;

import com.facebook.model.GraphUser;

/**
 * Created by kyeh on 11/2/13.
 */
public class UserHelper {
    private static GraphUser currentUser = null;
    private static String accessToken = null;

    public static void setCurrentUser(GraphUser user, String token) { currentUser = user; accessToken = token; }
    public static GraphUser getCurrentUser() { return currentUser; }
    public static String getAccessToken() { return (accessToken == null ? "" : accessToken); }
}
