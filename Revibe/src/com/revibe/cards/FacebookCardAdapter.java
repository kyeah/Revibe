package com.revibe.cards;

import android.content.Context;

import com.afollestad.cardsui.Card;
import com.afollestad.cardsui.CardAdapter;
import com.revibe.R;

/**
 * Created by kyeh on 11/2/13.
 */
public class FacebookCardAdapter extends CardAdapter<Card> {
    public FacebookCardAdapter(Context context) {
        super(context, R.layout.card_facebook);
    }

}
