package com.revibe;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.cardsui.Card;
import com.afollestad.cardsui.CardHeader;
import com.afollestad.cardsui.CardListView;
import com.revibe.cards.UserCardAdapter;

/**
 * Created by kyeh on 11/2/13.
 */
public class HomeFragment extends Fragment {

    private CardListView cardListView;
    private UserCardAdapter cardAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        cardListView = (CardListView) rootView.findViewById(R.id.cards_list);
        cardAdapter = new UserCardAdapter(getActivity());
        cardAdapter.add(new CardHeader("Test Header", "yeah that's right"));
        cardAdapter.add(new Card("Test", "This is my post I am posting about stuff and racism and sexism wow"));
        cardAdapter.add(new Card("Test"));
        cardAdapter.add(new Card("Test"));
        cardAdapter.add(new Card("Test"));
        cardAdapter.add(new Card("Test"));
        cardAdapter.add(new Card("Test"));
        cardListView.setAdapter(cardAdapter);

        return rootView;
    }

}
