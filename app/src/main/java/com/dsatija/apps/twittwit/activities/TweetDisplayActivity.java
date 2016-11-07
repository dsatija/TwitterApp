package com.dsatija.apps.twittwit.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dsatija.apps.twittwit.R;
import com.dsatija.apps.twittwit.models.Tweet;
import com.dsatija.apps.twittwit.network.TwitterApplication;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class TweetDisplayActivity extends AppCompatActivity {
    private ImageView ivDetailProfileImage;
    private TextView tvDetailName;
    private TextView tvDetailUserName;
    private TextView tvDetailBody;
    //  private TextView tvDetailDesc;
    private TextView tvDetailDate;
    private Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_display);


        tweet = (Tweet) getIntent().getParcelableExtra("tweet_selected");

        getSupportActionBar().setTitle("Twittwit");
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
        //        .getColor(R.color.blue)));

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_twit);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ivDetailProfileImage = (ImageView) findViewById(R.id.ivDetailProfileImage);
        tvDetailName = (TextView) findViewById(R.id.tvDetailName);
        tvDetailBody = (TextView) findViewById(R.id.tvDetailBody);
        tvDetailUserName = (TextView) findViewById(R.id.tvDetailUserName);
        tvDetailDate = (TextView) findViewById(R.id.tvDetailDate);

        // tvDetailDesc = (TextView) findViewById(R.id.tvDetailDesc);

        getDetails();


    }

    private void getDetails() {

        TwitterApplication.getRestClient().showTweet(tweet.getUid(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // ...the data has come back, add new items to your adapter...
                tvDetailUserName.setText("@" + tweet.getUser().getScreenName());
                tvDetailDate.setText(tweet.getRelativeTimeAgo());
                tvDetailName.setText(tweet.getUser().getName());


                try {
                    // tvDetailDesc.setText(response.getString("description"));
                    tvDetailBody.setText(response.getString("text"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Glide.with(getBaseContext()).load(tweet.getUser().getProfileImageUrl()).into(ivDetailProfileImage);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                Toast.makeText(getBaseContext(), "No network available", Toast.LENGTH_SHORT).show();
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tweet_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
