package com.revibe.facebook;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.cardsui.Card;
import com.afollestad.cardsui.CardListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.revibe.R;
import com.revibe.utils.UserHelper;
import com.revibe.utils.WebHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kyeh on 11/3/13.
 */
public class FacebookFragment extends Fragment {

    private static final String TAG = "FacebookFragment";
    private CardListView cardListView;
    private FacebookCardAdapter cardAdapter;
    private ArrayList<Card> cards = new ArrayList<Card>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        cardListView = (CardListView) rootView.findViewById(R.id.cards_list);
        cardAdapter = new FacebookCardAdapter(getActivity());

        cardAdapter.set(cards);
        cardListView.setAdapter(cardAdapter);
        cardListView.setOnCardClickListener(new FacebookCardOnClickListener());

        getMoreCards();
        return rootView;
    }

    public void getMoreCards() {
        Log.e(TAG, "getMoreCards");
        WebHelper.get("https://graph.facebook.com/me/home?access_token=" + UserHelper.getAccessToken(), null, new JsonHttpResponseHandler() {

            @Override
            public void onFinish() {
                Log.e(TAG, "onFinish");
                super.onFinish();
            }

            @Override
            public void onFailure(Throwable throwable, JSONObject jsonObject) {
                Log.e(TAG, "failed: " + throwable.getMessage());
                super.onFailure(throwable, jsonObject);
            }

            @Override
            public void onSuccess(int i, JSONObject jsonObject) {
                try {
                    JSONArray array = jsonObject.getJSONArray("data");

                    for (int j = 0; j < array.length(); j++) {
                        JSONObject obj = array.getJSONObject(j);
                        if (obj.has("message")) {
                            cards.add(new FacebookCard(obj).setClickable(true));
                        } else {
                            Log.e(TAG, "item " + j + " No message: " + obj.toString());
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                cardAdapter.set(cards);
                super.onSuccess(i, jsonObject);
            }
        });
    }

}
