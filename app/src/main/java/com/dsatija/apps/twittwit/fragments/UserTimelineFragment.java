package com.dsatija.apps.twittwit.fragments;

import android.os.Bundle;
import android.util.Log;

import com.dsatija.apps.twittwit.models.Tweet;
import com.dsatija.apps.twittwit.network.NetworkConnectivity;
import com.dsatija.apps.twittwit.network.TwitterApplication;
import com.dsatija.apps.twittwit.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

//loading user stream
public class UserTimelineFragment extends  TweetsListFragment{
    private static String screen_name;
    private TwitterClient client;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        populateTimeline((long)0);
    }

    public static UserTimelineFragment newInstance(String screen_name) {
        UserTimelineFragment userfragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screen_name);
        userfragment.setArguments(args);
        return userfragment;
    }


    @Override
    protected void populateTimeline(long offset) {
        screen_name = getArguments().getString("screen_name");

        /*if (!isOnline()) {
            Snackbar.make(this.getCurrentFocus(), "No internet connection!", Snackbar.LENGTH_LONG)
                    .setAction("RETRY", null).show();
            fetchingSavedTweets();
        } else {*/
        client.getUserTimeline(screen_name,new JsonHttpResponseHandler() {
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



    protected void initialOrRefreshPopulateTimeline(long page) {

       // clear();

        if (NetworkConnectivity.isNetworkAvailable(getContext()) != true) {
            addAll(Tweet.findAll());
            return;
        }

        populateTimeline(page);
    }
}




