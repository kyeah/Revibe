package com.revibe.facebook;

import android.util.Log;

import com.afollestad.cardsui.Card;
import com.revibe.utils.DateHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kyeh on 11/3/13.
 */
public class FacebookCard extends Card {

    private static enum PostType {
        GROUP_CREATED(11), EVENT_CREATED(12), STATUS_UPDATE(46), WALL_POST(56), NOTE_CREATED(66), LINK_POSTED(80), VIDEO_POSTED(128), PHOTOS_POSTED(247), APP_STORY(237), COMMENT_CREATED(257), APP_STORY_ALT(272), CHECKIN(285), GROUP_POST(308);

        private final int val;
        PostType(int val) { this.val = val; }
        public int getValue() { return val; }
        public static PostType itoPT(int i) {
            switch (i) {
                case 11: return GROUP_CREATED;
                case 12: return EVENT_CREATED;
                case 46: return STATUS_UPDATE;
                case 56: return WALL_POST;
                case 66: return NOTE_CREATED;
                case 80: return LINK_POSTED;
                case 128: return VIDEO_POSTED;
                case 247: return PHOTOS_POSTED;
                case 237: return APP_STORY;
                case 257: return COMMENT_CREATED;
                case 272: return APP_STORY_ALT;
                case 285: return CHECKIN;
                case 308: return GROUP_POST;
                default: return null;
            }
        }
    };

    private static final String TAG = "FacebookCard";
    private String id;
    private PostType type;
    private String message;

    private FacebookActor from;
    private FacebookActor source, target;

    private String timeCreated;

    private String actorId, sourceId, targetId;

    private boolean canLike, liked, canComment;
    private int likeCount, commentCount;
    private String commentOrder;

    public FacebookCard(JSONObject obj) {
        try {
            if (obj.has("post_id")) {
                id = obj.getString("post_id");
            } if (obj.has("actor_id")) {
                actorId = obj.getString("actor_id");
                from = FacebookActor.findFromId(actorId);
            } if (obj.has("source_id")) {
                sourceId = obj.getString("source_id");
                source = FacebookActor.findFromId(sourceId);
            } if (obj.has("target_id")) {
                targetId = obj.getString("target_id");
                target = FacebookActor.findFromId(targetId);
            } if (obj.has("type")) {
                type = PostType.itoPT(obj.getInt("type"));
            } if (obj.has("message")) {
                message = obj.getString("message");
            } if (obj.has("created_time")) {
                timeCreated = DateHelper.timeSince(obj.getDouble("created_time") * 1000);
            } if (obj.has("like_info")) {
                JSONObject likeObj = obj.getJSONObject("like_info");

                if (likeObj != null) {
                    canLike = likeObj.getBoolean("can_like");
                    liked = likeObj.getBoolean("user_likes");
                    likeCount = likeObj.getInt("like_count");
                }
            } if (obj.has("comment_info")) {
                JSONObject commentObj = obj.getJSONObject("comment_info");

                if (commentObj != null) {
                    canComment = commentObj.getBoolean("can_comment");
                    commentCount = commentObj.getInt("comment_count");
                    commentOrder = commentObj.getString("comment_order");
                }

            } if (obj.has("attachment")) {

            }
        } catch (JSONException je) {
            Log.e(TAG, je.getMessage());
        }
    }

    public String getId() { return id; }
    public String getTitle() {
        if (from == null) return actorId;
        return from.getName() + (target == null ? "" : " >> " + target.getName()); }
    public String getContent() { return message; }
    public FacebookActor getFacebookActorFrom() { return from; }
    public FacebookActor getFacebookuserTo() { return target; }
    public String getTimeCreated() { return timeCreated; }
    public int getLikeCount() { return likeCount; }
    public int getCommentCount() { return commentCount; }

}
