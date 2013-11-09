package com.revibe.facebook;

import android.content.Context;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.cardsui.CardAdapter;
import com.afollestad.cardsui.CardBase;
import com.facebook.widget.ProfilePictureView;
import com.revibe.R;
import com.revibe.utils.BitmapManager;

/**
 * Created by kyeh on 11/3/13.
 */
public class FacebookCardAdapter extends CardAdapter {

    Context context;

    public FacebookCardAdapter(Context context) {
        super(context, R.layout.card_facebook);
        this.context = context;
    }

    @Override
    protected boolean onProcessContent(TextView content, CardBase card) {
        content.setText(card.getContent());
        boolean linksPresent = Linkify.addLinks(content, Linkify.WEB_URLS);
        if (linksPresent)
            content.setLinkTextColor(context.getResources().getColor(R.color.com_facebook_blue));
        else
            content.setMovementMethod(null);

        return false;
    }

    @Override
    protected boolean onProcessThumbnail(ImageView icon, CardBase card) {
        return super.onProcessThumbnail(icon, card);
    }

    @Override
    protected boolean onProcessTitle(TextView title, CardBase card, int accentColor) {
        return super.onProcessTitle(title, card, accentColor);
    }

    @Override
    public View onViewCreated(int index, View recycled, CardBase item) {
        if (item instanceof FacebookCard) {
            if (recycled == null)
                recycled = View.inflate(context, R.layout.card_progress_bar, null);

            FacebookViewHolder holder;
            Object tag = recycled.getTag();
            if (tag == null) {
                holder = new FacebookViewHolder();
                holder.icon =              (ProfilePictureView) recycled.findViewById(R.id.icon);
                holder.subtitle =       (TextView) recycled.findViewById(R.id.subtitle);
                holder.likes_comments = (TextView) recycled.findViewById(R.id.likes_comments);
                recycled.setTag(holder);
            } else {
                holder = (FacebookViewHolder) tag;
            }

            FacebookCard card = (FacebookCard) item;
            ProfilePictureView icon =   holder.icon;
            TextView subtitle =         holder.subtitle;
            TextView likes_comments =   holder.likes_comments;

            if (icon != null) {
                if (card.getFacebookActorFrom() != null)
                    icon.setProfileId(card.getFacebookActorFrom().getId());
            } if (subtitle != null) {
                subtitle.setText(card.getTimeCreated());
            } if (likes_comments != null) {
                int likes = card.getLikeCount();
                int comments = card.getCommentCount();

                String likeStr = likes + " like";
                String commentStr = comments + " comment";
                if (likes != 1) likeStr += "s";
                if (comments != 1) commentStr += "s";

                likes_comments.setText(likeStr + "\n" + commentStr);
            }
        }

        return super.onViewCreated(index, recycled, item);
    }

    public class FacebookViewHolder {
        public ProfilePictureView icon;
        //public ProfilePictureView p;
        public TextView subtitle, likes_comments;

    }
}
