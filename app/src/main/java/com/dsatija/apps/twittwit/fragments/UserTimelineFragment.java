package com.dsatija.apps.twittwit.fragments;

import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

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

//loading user stream
public class UserTimelineFragment extends TweetsListFragment {
    private static String screen_name;
    private TwitterClient client;
    private ProgressBar progressBarFooter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        populateTimeline(TwitterConstants.NEW_PAGE, 1);
    }

    public static UserTimelineFragment newInstance(String screen_name) {
        UserTimelineFragment userfragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screen_name);
        userfragment.setArguments(args);
        return userfragment;
    }


    @Override
    protected void populateTimeline(int refType, long offset) {
        screen_name = getArguments().getString("screen_name");
        final int refreshType = refType;
        showProgressBar();
        client.getUserTimeline(screen_name, refreshType, offset, new JsonHttpResponseHandler() {
            //Success

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                hideProgressBar();
                Log.d("Debug", json.toString());
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




