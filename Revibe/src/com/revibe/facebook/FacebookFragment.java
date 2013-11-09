package com.revibe.facebook;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.afollestad.cardsui.Card;
import com.afollestad.cardsui.CardListView;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
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

    private Card footer;

    private boolean hasMoreToShow = true;

    private Request request = null;
    private Request.Callback addCardsToAdapterCallback = new Request.Callback() {
        @Override
        public void onCompleted(Response response) {
            cardAdapter.remove(cardAdapter.getCount() - 1);  // Remove the loading footer
            Log.e(TAG, response.toString());
            GraphObject grobby = response.getGraphObject();
            request = response.getRequestForPagedResults(Response.PagingDirection.NEXT);
            if (grobby != null) {
                Log.e(TAG, "grobby=" + grobby.toString());
                try {
                    JSONObject obj = grobby.getInnerJSONObject();
                    if (obj != null && obj.has("data")) {
                        JSONArray data = obj.getJSONArray("data");

                        // Extract each query from data array
                        JSONObject postObj, userObj, pageObj;
                        postObj = userObj = pageObj = null;

                        JSONObject temp;
                        for (int i = 0; i < data.length(); i++) {
                            temp = data.getJSONObject(i);
                            String name = temp.getString("name");
                            if (name.equals("posts")) {
                                postObj = temp;
                            } else if (name.equals("users")) {
                                userObj = temp;
                            } else {
                                pageObj = temp;
                            }
                        }

                        JSONArray posts = postObj.getJSONArray("fql_result_set");
                        JSONArray users = userObj.getJSONArray("fql_result_set");
                        JSONArray pages = pageObj.getJSONArray("fql_result_set");

                        // Add userID-User pairs to cache
                        for (int i = 0; i < users.length(); i++) {
                            Log.e(TAG, "i=" + i + " Actor: " + users.get(i).toString());
                            JSONObject user = users.getJSONObject(i);
                            if (FacebookUser.contains(user.getString("uid"))) continue;
                            FacebookUser.addToCache(new FacebookUser(user));
                        }

                        // Add pageID-Page pairs to cache
                        for (int i = 0; i < pages.length(); i++) {
                            Log.e(TAG, "i=" + i + "Page: " + pages.get(i).toString());
                            JSONObject page = pages.getJSONObject(i);
                            if (FacebookPage.contains(page.getString("page_id"))) continue;
                            FacebookPage.addToCache(new FacebookPage(page));
                        }

                        // Add each new post to the card adapter
                        for (int i = 0; i < posts.length(); i++) {
                            JSONObject post = posts.getJSONObject(i);
                            Log.e(TAG, "i=" + i + ": " + post.toString());
                            FacebookCard card = new FacebookCard(post);
                            cardAdapter.add(card.setClickable(true));
                        }
                    }
                } catch (JSONException je) {
                    Log.e(TAG, je.getMessage());
                }
            }

            if (request != null)
                hasMoreToShow = true;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        cardListView = (CardListView) rootView.findViewById(R.id.cards_list);
        cardAdapter = new FacebookCardAdapter(getActivity());

        footer = new Card("").setLayout(R.layout.card_progress_bar);
        cardAdapter.registerLayout(R.layout.card_progress_bar);

        cardListView.setAdapter(cardAdapter);
        cardListView.setOnCardClickListener(new FacebookCardOnClickListener());
        cardListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView absListView, int i) {}
            public void onScroll(AbsListView view, int first, int visible, int total) {
                if (first + visible >= total && hasMoreToShow) {
                    cardAdapter.add(footer);
                    hasMoreToShow = false;
                    getMoreCards();
                }
            }
        });

        return rootView;
    }

    public void getMoreCards() {
        if (cardAdapter.getCount() < 2) {
            JSONObject query = new JSONObject();
            try {
                query.put("posts", "SELECT post_id, actor_id, source_id, target_id, via_id, type, created_time, like_info, comment_info, message, attachment "
                        + "FROM stream WHERE filter_key = 'nf' "
                        + "ORDER BY created_time DESC");
                query.put("users", "SELECT name, uid, pic_square FROM user WHERE uid IN (SELECT actor_id, source_id, target_id, via_id FROM #posts)");
                query.put("pages", "SELECT name, page_id, pic_square FROM page WHERE page_id IN (SELECT actor_id, source_id, target_id, via_id FROM #posts)");
                String q = query.toString();
                Log.e(TAG, "q=" + q);
                FQLHelper.request(q, addCardsToAdapterCallback);
            } catch (JSONException je) {
                Log.e(TAG, je.getMessage());
            }
        }
        else if (request != null) {
            FQLHelper.request(request, addCardsToAdapterCallback);
        }
    }

}
