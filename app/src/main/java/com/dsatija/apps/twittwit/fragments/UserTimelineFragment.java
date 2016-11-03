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

//loading user stream
public class UserTimelineFragment extends  TweetsListFragment{
    private TwitterClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        populateTimeline(null);
    }

    public static UserTimelineFragment newInstance(String screen_name) {
        UserTimelineFragment userfragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screen_name);
        userfragment.setArguments(args);
        return userfragment;
    }

    private void populateTimeline(Long maxid) {
        String screenName = getArguments().getString("screen_name");

        /*if (!isOnline()) {
            Snackbar.make(this.getCurrentFocus(), "No internet connection!", Snackbar.LENGTH_LONG)
                    .setAction("RETRY", null).show();
            fetchingSavedTweets();
        } else {*/
        client.getUserTimeline(screenName,new JsonHttpResponseHandler() {
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
