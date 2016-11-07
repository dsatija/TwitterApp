package com.dsatija.apps.twittwit.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dsatija.apps.twittwit.R;
import com.dsatija.apps.twittwit.models.User;
import com.dsatija.apps.twittwit.network.TwitterApplication;
import com.dsatija.apps.twittwit.network.TwitterClient;

public class ComposeTweetActivity extends AppCompatActivity {
    private SharedPreferences prefs;
    private ImageView ivProfileImage;
    private TextView tvScreenName;
    private TextView tvUserName;
    private EditText etTweet;
    private Menu menu;
    private User user;
    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_tweet);

        getSupportActionBar().setTitle("Compose");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_twit);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        prefs = this.getSharedPreferences("com.dsatija.apps.twittwit", Context.MODE_PRIVATE);
        client = TwitterApplication.getRestClient();
        setupviews();

        setupHandlers();
        loadLoginUserData();
    }

    private void setupviews() {
        ivProfileImage = (ImageView) findViewById(R.id.ivComposeProfileImage);
        tvScreenName = (TextView) findViewById(R.id.tvComposeScreenName);
        tvUserName = (TextView) findViewById(R.id.tvComposeUsername);
        etTweet = (EditText) findViewById(R.id.etcomposeTweet);
    }

    public void postTweet(MenuItem mi) {
        String tweetBody = etTweet.getText().toString();
        // Prepare data intent
        Intent data = new Intent();
        // Pass relevant data back as a result
        data.putExtra("tweet", tweetBody);
        // Activity finished ok, return the data
        setResult(RESULT_OK, data); // set result code and bundle data for
        // response
        finish(); // closes the activity, pass data to parent
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_tweet, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    private void setupHandlers() {
        etTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                //get remaining number of characters allowed and display it in the action bar
                Integer remainCount = 140 - s.length();
                MenuItem miCount = menu.findItem(R.id.remain_count);
                miCount.setTitle(remainCount.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void loadLoginUserData() {
        Long userId = prefs.getLong("userId", -1L);
        if (userId == null || userId.equals(-1L)) {
            Log.e("ERROR", "profile data not availabel");
        } else {
            user = User.findById(userId);
            if (user == null) {
                Log.e("ERROR", "Can't find user with userId=" + userId);
            }
            ivProfileImage.setImageResource(android.R.color.transparent);
            Glide.with(getApplicationContext())
                    .load(user.getProfileImageUrl()).fitCenter().centerCrop()
                    .into(ivProfileImage);
            tvScreenName.setText(user.getName());
            tvUserName.setText("@" + user.getScreenName());
        }
    }
}
