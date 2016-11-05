package com.dsatija.apps.twittwit.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dsatija.apps.twittwit.R;
import com.dsatija.apps.twittwit.fragments.UserTimelineFragment;
import com.dsatija.apps.twittwit.models.User;
import com.dsatija.apps.twittwit.network.NetworkConnectivity;
import com.dsatija.apps.twittwit.network.TwitterApplication;
import com.dsatija.apps.twittwit.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity {
    TwitterClient client ;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setUserInfo();


                //populateProfileHeader(user);
             //get screen name
        //String screenName = getIntent().getStringExtra("screen_name");
        if (savedInstanceState == null) {
            //create user timeline fragment
            UserTimelineFragment fragmentUserTimeline = UserTimelineFragment.newInstance(user.getScreenName());

            //Display User fragment  within this activity(dynamivally)
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, fragmentUserTimeline);
            ft.commit();

        }
    }

    private void setUserInfo() {

        user = (User)getIntent().getSerializableExtra("user");

        if (user == null && !NetworkConnectivity.isNetworkAvailable(this)) {
            Toast.makeText(this, "you are offline, we can't access your user data", Toast.LENGTH_LONG).show();
            return;
        }

        if (user == null) {
            client = TwitterApplication.getRestClient();
            client.getUserInfo(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    user = User.fromJson(response);

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.i("FAILED USER REQUEST", responseString);
                }
            });
        }

        if (user == null) {
            Toast.makeText(this, "this app sucks and can't set a proper user!", Toast.LENGTH_LONG).show();
            return;
        }
        getSupportActionBar().setTitle("@" + user.getScreenName());
        populateProfileHeader(user);
    }

    private void populateProfileHeader(User user) {
        TextView tvname = (TextView) findViewById(R.id.tvName);
        TextView tvTagline = (TextView) findViewById(R.id.tvTagline);
        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        ImageView ivDisplaypic = (ImageView) findViewById(R.id.ivDisplayPic);
        tvname.setText(user.getName());
        tvTagline.setText(user.getTagLine());
        tvFollowers.setText(user.getFollowersCount()+" Followers");
        tvFollowing.setText(user.getFriendsCount()+" Following");
        Glide.with(this).load(user.getProfileImageUrl()).fitCenter().centerCrop().into(ivDisplaypic);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }
}

