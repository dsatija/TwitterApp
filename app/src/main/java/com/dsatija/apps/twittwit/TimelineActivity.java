package com.dsatija.apps.twittwit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.dsatija.apps.twittwit.activities.ComposeTweetActivity;
import com.dsatija.apps.twittwit.listeners.EndlessScrollListener;
import com.dsatija.apps.twittwit.models.Tweet;
import com.dsatija.apps.twittwit.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {
    private TwitterClient client;
    private TweetsArrayAdapter aTweets;
    private ArrayList<Tweet> tweets;
    private ListView lvTweets;
    private Long minId = Long.MAX_VALUE;
    private Long maxId = 1L;
    private static final int REQUEST_CODE = 10;
    private SharedPreferences prefs;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        lvTweets = (ListView) findViewById(R.id.lvTweets);

        prefs = this.getSharedPreferences("com.dsatija.apps.twittwit", Context.MODE_PRIVATE);
        //construct array list(data source)
        tweets = new ArrayList<>();
        //create adapter from data source
        aTweets = new TweetsArrayAdapter(this, tweets);
        //connect adapter
        lvTweets.setAdapter(aTweets);
        //get client
        client = TwitterApplication.getRestClient();
        saveLoginUserProfileData();
        populateTimeline(null);
        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                refreshTimeline();

            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                loadNextDataFromApi(page);
                // or loadNextDataFromApi(totalItemsCount);
                // return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });

    }

    private void loadNextDataFromApi(int page) {
        if (aTweets.getCount() == 0) {
            populateTimeline(null);
        } else if (aTweets.getCount() >= TwitterClient.TWEETS_PER_PAGE) {
            Tweet oldest = aTweets.getItem(aTweets.getCount() - 1);
            populateTimeline(oldest.getUid());
        }
    }

    //send api request n fill lit view by creating tweet objects from json
    private void populateTimeline(Long maxid) {
        if (!isOnline()) {
            Snackbar.make(this.getCurrentFocus(), "No internet connection!", Snackbar.LENGTH_LONG)
                    .setAction("RETRY", null).show();
            fetchingSavedTweets();
        } else {
            client.getHomeTimeline(maxid, new JsonHttpResponseHandler() {
                //Success

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                    Log.d("Debug", json.toString());
                    ArrayList<Tweet> tweets = Tweet.fromJSONArray(json);
                    saveTweets(tweets);
                    aTweets.addAll(tweets);


                }
                //failure

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                }
            });
        }
    }

    private void refreshTimeline() {
        if (aTweets.getCount() > 0) {
            minId = aTweets.getItem(aTweets.getCount() - 1).getUid();
            maxId = aTweets.getItem(0).getUid();
        } else {
            minId = Long.MAX_VALUE;
            maxId = 1L;
        }
        client.getHomeTimeline(maxId, new JsonHttpResponseHandler() {
            //Success

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                Log.d("Debug", json.toString());
                aTweets.clear();
                ArrayList<Tweet> tweets = Tweet.fromJSONArray(json);
                saveTweets(tweets);
                aTweets.addAll(tweets);
                swipeContainer.setRefreshing(false);

            }
            //failure

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.compose_tweet, menu);
        return true;
    }

    public void composeTweet(MenuItem mi) {
        Intent i = new Intent(TimelineActivity.this, ComposeTweetActivity.class);
        startActivityForResult(i, REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            String tweetStr = data.getExtras().getString("tweet");
            client.postTweet(tweetStr, new JsonHttpResponseHandler() {
                public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                    Tweet tweet = Tweet.fromJson(json);
                    aTweets.insert(tweet, 0);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG_error_during_post", errorResponse.toString());
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.aiCompose) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveLoginUserProfileData() {
        client.getUserProfile(new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                Log.d("json from succes", json.toString());
                User user = User.fromJson(json);

                prefs.edit().putLong("userId", user.getUid()).commit();
                user.save();
            }

            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

        });
    }

    private void saveTweets(List<Tweet> newTweets) {
        Tweet.saveAll(newTweets);
    }

    private void fetchingSavedTweets() {
        List<Tweet> savedTweets = Tweet.findAll();
        if (savedTweets != null || savedTweets.isEmpty()) {
            aTweets.clear();
            aTweets.addAll(savedTweets);
        }
    }

    private Boolean isOnline() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

}






