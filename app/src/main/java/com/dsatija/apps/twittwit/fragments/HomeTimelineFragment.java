package com.dsatija.apps.twittwit.fragments;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.dsatija.apps.twittwit.models.Tweet;
import com.dsatija.apps.twittwit.models.User;
import com.dsatija.apps.twittwit.network.NetworkConnectivity;
import com.dsatija.apps.twittwit.network.TwitterApplication;
import com.dsatija.apps.twittwit.network.TwitterClient;
import com.dsatija.apps.twittwit.utilities.TwitterConstants;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.Delete;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class HomeTimelineFragment extends TweetsListFragment {
    TwitterClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get the client
        client = TwitterApplication.getRestClient(); // singleton client
        populateTimeline(TwitterConstants.NEW_PAGE, 1);
    }


    protected void populateTimeline(int refType, long offset) {
        if (NetworkConnectivity.isNetworkAvailable(getContext()) != true) {
            Toast.makeText(getContext(), "you are offline, there are no new tweets", Toast.LENGTH_LONG).show();
            return;
        }

        final int refreshType = refType;
        showProgressBar();
        client.getHomeTimeline(refreshType, offset, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray jsonResponse) {

                        hideProgressBar();
                        if (refreshType == TwitterConstants.SWIPE) {
                            // Now we call setRefreshing(false) to signal refresh has finished
                            swipeContainer.setRefreshing(false);
                        }

                        if (refreshType != TwitterConstants.SCROLL) {
                            //clear adapter
                            getAdapter().clear();
                            //clear dbs
                            new Delete().from(Tweet.class).execute();
                            new Delete().from(User.class).execute();
                        }

                        ArrayList<Tweet> temps = Tweet.fromJSONArray(jsonResponse);
                        if (temps.size() > 0) {
                            earliestID = temps.get(temps.size() - 1).getUid();
                            getAdapter().addAll(temps);
                        }


                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        if (errorResponse != null) {
                            Log.d("DEBUG", errorResponse.toString());
                        }

                        if (refreshType == TwitterConstants.SWIPE) {
                            swipeContainer.setRefreshing(false);
                        }

                    }
                }
        );
    }
}

