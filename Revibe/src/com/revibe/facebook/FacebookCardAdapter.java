package com.revibe.facebook;

import android.content.Context;
import android.text.util.Linkify;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
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
    int mLastPosition = -1;

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
        /** TODO: Change ProfilePictureView to CircularImageView and use color filter to add click effect
            Ex: ImageView v = (ImageView) findViewById(R.id.pic);
                case MotionEvent.ACTION_DOWN:
                  v.setColorFilter(0x55<Color>, PorterDuff.Mode.MULTIPLY);
                  break;
                case MotionEvent.ACTION_CANCEL:
                  button.setColorFilter(null);
                  break;
         **/

        if (item instanceof FacebookCard) {
            if (recycled == null)
                recycled = View.inflate(context, R.layout.card_progress_bar, null);

            FacebookViewHolder holder;
            Object tag = recycled.getTag();
            if (tag == null) {
                holder = new FacebookViewHolder();
                holder.icon =           (ProfilePictureView) recycled.findViewById(R.id.icon);
                holder.subtitle =       (TextView) recycled.findViewById(R.id.subtitle);
                holder.likes_comments = (TextView) recycled.findViewById(R.id.likes_comments);

                recycled.setTag(holder);
            } else {
                holder = (FacebookViewHolder) tag;
            }

            FacebookCard card =         (FacebookCard) item;
            ProfilePictureView icon =   holder.icon;
            TextView subtitle =         holder.subtitle;
            TextView likes_comments =   holder.likes_comments;

            if (icon != null) {
                icon.setProfileId(card.getActorFrom().getId());
            } if (subtitle != null) {
                subtitle.setText(card.getTimeCreated());
            } if (likes_comments != null) {
                int likes = card.getLikeCount();
                int comments = card.getCommentCount();

                String likeStr = likes + " like" + (likes == 1 ? "" : "s");
                String commentStr = comments + " comment" + (comments == 1 ? "" : "s");
                likes_comments.setText(likeStr + "\n" + commentStr);
            }
        }

        TranslateAnimation animation;
        if (index > mLastPosition) {
            animation = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f);

            animation.setDuration(400);
            recycled.startAnimation(animation);
            mLastPosition = index;
        }

        return super.onViewCreated(index, recycled, item);
    }

    public class FacebookViewHolder {
        public ProfilePictureView icon;
        public TextView subtitle, likes_comments;

    }
}
