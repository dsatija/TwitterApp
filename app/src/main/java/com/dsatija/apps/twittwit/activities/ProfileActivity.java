package com.dsatija.apps.twittwit.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dsatija.apps.twittwit.R;
import com.dsatija.apps.twittwit.fragments.UserTimelineFragment;
import com.dsatija.apps.twittwit.models.User;
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
        client = TwitterApplication.getRestClient();
        client.getUserInfo(new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                user = User.fromJson(response);
                //current user info

                getSupportActionBar().setTitle("@" + user.getScreenName());
                populateProfileHeader(user);
            }
        });
        //get screen name
        String screenName = getIntent().getStringExtra("screen_name");
        if (savedInstanceState == null) {
            //create user timeline fragment
            UserTimelineFragment fragmentUserTimeline = UserTimelineFragment.newInstance(screenName);
            //Display User fragment  within this activity(dynamivally)
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, fragmentUserTimeline);
            ft.commit();

        }
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
}
