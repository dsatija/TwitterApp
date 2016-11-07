package com.dsatija.apps.twittwit.activities;

import android.content.Context;
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
import com.dsatija.apps.twittwit.adapter.SmartFragmentStatePagerAdapter;
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
        prefs = this.getSharedPreferences("com.dsatija.apps.twittwit", Context.MODE_PRIVATE);
        populateCurrentUser();
        saveLoginUserProfileData();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_twit);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        //get viewpager
        ViewPager vpPager = (ViewPager) findViewById(R.id.viewpager);
        adapterViewPager = new TweetsPagerAdapter(getSupportFragmentManager());
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
            if (userId != null) {
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




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        /*getMenuInflater().inflate(R.menu.compose_tweet, menu);*/
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    public void onProfileView(MenuItem mi) {
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
                    HomeTimelineFragment homeTimelineFragment = (HomeTimelineFragment) adapterViewPager.getRegisteredFragment(0);
                    homeTimelineFragment.insert(tweet, 0);
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


    private void saveTweets(List<Tweet> newTweets) {
        Tweet.saveAll(newTweets);
    }

    public class TweetsPagerAdapter extends SmartFragmentStatePagerAdapter {

        private String tabTitles[] = {"Home", "Mentions"};

        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new HomeTimelineFragment();
            } else if (position == 1) {
                return new MentionsTimelineFragment();
            } else
                return null;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }

}






