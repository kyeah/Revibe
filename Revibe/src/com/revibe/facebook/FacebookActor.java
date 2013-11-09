package com.revibe.facebook;

/**
 * Created by kyeh on 11/9/13.
 */
public abstract class FacebookActor {

    public abstract String getId();
    public abstract String getName();
    public abstract String getProfilePicUrl();

    public static FacebookActor findFromId(String id) {
        FacebookActor actor = FacebookUser.getFromCache(id);
        if (actor == null)
            actor = FacebookPage.getFromCache(id);

        return actor;
    }

}
