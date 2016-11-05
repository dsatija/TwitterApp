package com.dsatija.apps.twittwit.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.dsatija.apps.twittwit.R;
import com.dsatija.apps.twittwit.adapter.SmartFragmentStatePagerAdapter.SmartFragmentStatePagerAdapter;
import com.dsatija.apps.twittwit.fragments.HomeTimelineFragment;
import com.dsatija.apps.twittwit.fragments.MentionsTimelineFragment;
import com.dsatija.apps.twittwit.fragments.TweetsListFragment;
import com.dsatija.apps.twittwit.models.Session;
import com.dsatija.apps.twittwit.models.Tweet;
import com.dsatija.apps.twittwit.models.User;
import com.dsatija.apps.twittwit.network.NetworkConnectivity;
import com.dsatija.apps.twittwit.network.TwitterApplication;
import com.dsatija.apps.twittwit.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private Long minId = Long.MAX_VALUE;
    private Long maxId = 1L;
    private static final int REQUEST_CODE = 10;
    private SharedPreferences prefs;
    private SwipeRefreshLayout swipeContainer;
    private TweetsListFragment fragmentTweetsList;
    private Session session;
    private User currentUser;
    private TwitterClient client;
    TweetsPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        client = TwitterApplication.getRestClient();
        prefs=PreferenceManager.getDefaultSharedPreferences(this);
        populateCurrentUser();
        saveLoginUserProfileData();
        //get viewpager
        ViewPager vpPager = (ViewPager) findViewById(R.id.viewpager);
        adapterViewPager=new TweetsPagerAdapter(getSupportFragmentManager());
        //set view pager adapter for pager
        vpPager.setAdapter(adapterViewPager);
        //find sliding tab strip
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        //Attach tab strip to view pager
        tabStrip.setViewPager(vpPager);

        session = new Session();

    }

    private void saveLoginUserProfileData() {
        client.getUserInfo(new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                Log.d("json from success", json.toString());
                User user = User.fromJson(json);
                prefs.edit().putLong("userId", user.getUid()).commit();
                user.save();
            }

            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

        });

    }

    private void populateCurrentUser() {


      //  prefs = this.getSharedPreferences("com.dsatija.apps.twittwit", Context.MODE_PRIVATE);

        if (!NetworkConnectivity.isNetworkAvailable(this)) {
            // grab current user from Shared Prefs, if there is one


            Long userId = prefs.getLong("user_id", Long.parseLong(""));
            if (!userId.equals(null)) {
                long uId = userId;
                currentUser = User.findById(uId);
                if (currentUser != null) {
                    session.setCurrentUser(currentUser);
                } else {
                    logOut();
                }
                return;
            }
            return; // exit if null, we should not call this endpoint if user isn't online
        }


    client.getUserInfo(new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            // create a user
            currentUser = User.fromJson(response);
            // set a current user
            session.setCurrentUser(currentUser);
            // only make the second request if that use isn't stored locally
            if (session.getCurrentUser() != null) {
                // set shared preferences
                // this should be happening each time!!
                SharedPreferences pref =
                        PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                SharedPreferences.Editor edit = pref.edit();
                edit.putLong("user_id", (session.getCurrentUser().getUid()));
                edit.commit();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            if (errorResponse != null) {
                Log.d("DEBUG", errorResponse.toString());
            } else {
                Log.d("DEBUG", "null error");
            }
        }
    });
}

    private void logOut() {

        Toast.makeText(this,
                "we should be logging you out since there is no user session",
                Toast.LENGTH_LONG).show();
    }

    //get client


       /* saveLoginUserProfileData(); */
       /* populateTimeline(null);
        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);*/
        /*swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);*/
        // Setup refresh listener which triggers new data loading
       /* swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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
        });*/



   /* private void loadNextDataFromApi(int page) {
        if (aTweets.getCount() == 0) {
            populateTimeline(null);
        } else if (aTweets.getCount() >= TwitterClient.TWEETS_PER_PAGE) {
            Tweet oldest = aTweets.getItem(aTweets.getCount() - 1);
            populateTimeline(oldest.getUid());
        }
    }*/

    //send api request n fill lit view by creating tweet objects from json

   /* private void refreshTimeline() {
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
    }*/





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        /*getMenuInflater().inflate(R.menu.compose_tweet, menu);*/
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }
    public void onProfileView(MenuItem mi){
        Intent intent = new Intent(TimelineActivity.this, ProfileActivity.class);
        intent.putExtra("user", session.getCurrentUser());
        startActivity(intent);
    }

    public void composeTweet(MenuItem mi) {
        Intent i = new Intent(TimelineActivity.this, ComposeTweetActivity.class);
        startActivityForResult(i, REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            final String tweetStr = data.getExtras().getString("tweet");
            client.postTweet(tweetStr, new JsonHttpResponseHandler() {
                public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                    Tweet tweet = Tweet.fromJson(json);
                    HomeTimelineFragment homeTimelineFragment= (HomeTimelineFragment) adapterViewPager.getRegisteredFragment(0);
                    homeTimelineFragment.insert(tweet,0);
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

  /*  private void saveLoginUserProfileData() {
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
*/
    private void saveTweets(List<Tweet> newTweets) {
        Tweet.saveAll(newTweets);
    }

   /* private void fetchingSavedTweets() {
        List<Tweet> savedTweets = Tweet.findAll();
        if (savedTweets != null || savedTweets.isEmpty()) {
            aTweets.clear();
            aTweets.addAll(savedTweets);
        }
    } */


    //return oreder of fragments in view pager
    public class TweetsPagerAdapter extends SmartFragmentStatePagerAdapter {

        private String tabTitles[] = {"Home","Mentions"};
        public TweetsPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new HomeTimelineFragment();
            } else if (position == 1) {
                return new MentionsTimelineFragment();
            }else
                return null;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return tabTitles.length ;
        }
    }

}






