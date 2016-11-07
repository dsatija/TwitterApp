package com.dsatija.apps.twittwit.fragments;

import android.os.Bundle;
import android.util.Log;

import com.dsatija.apps.twittwit.models.Tweet;
import com.dsatija.apps.twittwit.models.User;
import com.dsatija.apps.twittwit.network.TwitterApplication;
import com.dsatija.apps.twittwit.network.TwitterClient;
import com.dsatija.apps.twittwit.utilities.TwitterConstants;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.Delete;

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
        populateTimeline(TwitterConstants.NEW_PAGE, 1);
    }


    protected void populateTimeline(int refType, long offset) {


        final int refreshType = refType;
        showProgressBar();

        client.getMentionsTimeline(refreshType, offset, new JsonHttpResponseHandler() {
            //Success

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {

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

                ArrayList<Tweet> temps = Tweet.fromJSONArray(json);
                if (temps.size() > 0) {
                    earliestID = temps.get(temps.size() - 1).getUid();
                    getAdapter().addAll(temps);
                }
                Log.d("DEBUG adapter", getAdapter().toString());

            }
            //failure

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });

    }


}
