package com.dsatija.apps.twittwit.fragments;

import android.os.Bundle;
import android.util.Log;

import com.dsatija.apps.twittwit.models.Tweet;
import com.dsatija.apps.twittwit.network.TwitterApplication;
import com.dsatija.apps.twittwit.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MentionsTimelineFragment extends TweetsListFragment {
    private TwitterClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        populateTimeline(null);
    }

    private void populateTimeline(Long maxid) {
        /*if (!isOnline()) {
            Snackbar.make(this.getCurrentFocus(), "No internet connection!", Snackbar.LENGTH_LONG)
                    .setAction("RETRY", null).show();
            fetchingSavedTweets();
        } else {*/
        client.getMentionsTimeline(new JsonHttpResponseHandler() {
            //Success

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                Log.d("Debug", json.toString());
                ArrayList<Tweet> tweets = Tweet.fromJSONArray(json);
                /*saveTweets(tweets);*/
                   /* aTweets.addAll(tweets);*/
                addAll(tweets);


            }
            //failure

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }


}
