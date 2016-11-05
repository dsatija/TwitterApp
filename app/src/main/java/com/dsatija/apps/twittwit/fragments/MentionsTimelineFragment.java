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

    private long previousOffSet=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        populateTimeline(0);
    }


    protected void populateTimeline(long offset) {




            client.getMentionsTimeline(offset, new JsonHttpResponseHandler() {
                //Success

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray json) {

                    Log.d("Debug", json.toString());
                    ArrayList<Tweet> tweets = Tweet.fromJSONArray(json);
                    Tweet tweet=tweets.get(tweets.size()-1);
                    previousOffSet=tweet.getUid();
                /*saveTweets(tweets);*/
                   /* aTweets.addAll(tweets);*/
                    Log.d("tweet size", String.valueOf(tweets.size()));
                    addAll(tweets);


                }
                //failure

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                }
            });

        previousOffSet=offset;

    }


}
