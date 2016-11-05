package com.dsatija.apps.twittwit.fragments;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.dsatija.apps.twittwit.models.Tweet;
import com.dsatija.apps.twittwit.network.NetworkConnectivity;
import com.dsatija.apps.twittwit.network.TwitterApplication;
import com.dsatija.apps.twittwit.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class HomeTimelineFragment extends TweetsListFragment {
    TwitterClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get the client
        client = TwitterApplication.getRestClient(); // singleton client
        populateTimeline((long) 0);
    }


    protected void populateTimeline(long offset) {
        if (NetworkConnectivity.isNetworkAvailable(getContext()) != true) {
            Toast.makeText(getContext(), "you are offline, there are no new tweets", Toast.LENGTH_LONG).show();
            return;
        }

        client.getHomeTimeline(offset, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray jsonResponse) {
                        addAll(Tweet.fromJSONArray(jsonResponse));
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        if (errorResponse != null) {
                            Log.d("DEBUG", errorResponse.toString());
                        }
                    }
                }
        );
    }
}

