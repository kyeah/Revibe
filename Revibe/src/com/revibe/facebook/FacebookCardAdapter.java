package com.revibe.facebook;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.cardsui.CardAdapter;
import com.afollestad.cardsui.CardBase;
import com.facebook.widget.ProfilePictureView;
import com.revibe.R;

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
        Linkify.addLinks(content, Linkify.WEB_URLS);
        content.setMovementMethod(LinkMovementMethod.getInstance());
        content.setLinkTextColor(context.getResources().getColor(R.color.com_facebook_blue));
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
            FacebookCard card =         (FacebookCard) item;
            ProfilePictureView p =      (ProfilePictureView) recycled.findViewById(R.id.icon);
            TextView subtitle =         (TextView) recycled.findViewById(R.id.subtitle);
            TextView likes_comments =   (TextView) recycled.findViewById(R.id.likes_comments);

            p.setProfileId(card.getFacebookUserFrom().getId());
            subtitle.setText(card.getTimeCreated());
            likes_comments.setText(card.getLikes().size() + " likes");
        }
        return super.onViewCreated(index, recycled, item);
    }
}
