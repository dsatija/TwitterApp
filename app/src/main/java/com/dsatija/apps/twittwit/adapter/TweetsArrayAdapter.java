package com.dsatija.apps.twittwit.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dsatija.apps.twittwit.R;
import com.dsatija.apps.twittwit.activities.ProfileActivity;
import com.dsatija.apps.twittwit.activities.TweetDisplayActivity;
import com.dsatija.apps.twittwit.models.Tweet;

import java.util.List;

/**
 * Created by Disha on 10/20/2016.
 */
//taking tweet objects and turning them into views
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {
    Tweet tweet;


    private TAFragmentListener fragmentListener;

    private static class ViewHolder {
        ImageView ivProfileImage;
        TextView tvUserName;
        TextView tvBody;
        TextView tvTimeStamp;
        TextView tvScreenName;
        TextView tvRetweetCount;
        TextView tvFavCount;


    }

    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, 0, tweets);
        this.fragmentListener = null;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // return  super.getView(position,convertView,parent);
        //get tweet
        tweet = getItem(position);
        //find template
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(com.dsatija.apps.twittwit.R.layout.item_tweet, parent, false);
            viewHolder.ivProfileImage = (ImageView) convertView.findViewById(com.dsatija.apps.twittwit.R.id.ivProfileImage);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            viewHolder.tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
            viewHolder.tvTimeStamp = (TextView) convertView.findViewById(R.id.tvTimeStamp);
            viewHolder.tvRetweetCount = (TextView) convertView.findViewById(R.id.tvRetweetCount);
            viewHolder.tvFavCount = (TextView) convertView.findViewById(R.id.tvFavCount);

            convertView.setTag(viewHolder);


        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //populate data into subview
        viewHolder.tvUserName.setText("@" + tweet.getUser().getScreenName());
        viewHolder.tvBody.setText(tweet.getBody());
        viewHolder.tvScreenName.setText(tweet.getUser().getName());
        viewHolder.tvTimeStamp.setText(tweet.getRelativeTimeAgo());
        viewHolder.ivProfileImage.setImageResource(android.R.color.transparent);
        Glide.with(getContext()).load(tweet.getUser().getProfileImageUrl()).fitCenter().
                centerCrop().into(viewHolder.ivProfileImage);
        viewHolder.tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
        if (tweet.isRetweeted()) {//if retweeted by the current user
            viewHolder.tvRetweetCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_retweet_green, 0, 0, 0);
        } else {
            viewHolder.tvRetweetCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_retweet, 0, 0, 0);
        }
        viewHolder.tvRetweetCount.setText("" + tweet.getRetweetCount());


        if (tweet.isFavorited()) {//if favorited by the current user
            viewHolder.tvFavCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_red, 0, 0, 0);
        } else {
            viewHolder.tvFavCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite, 0, 0, 0);
        }
        viewHolder.tvFavCount.setText(String.valueOf(tweet.getFavCount()));

        viewHolder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setTag(tweet.getUser());
                Intent i = new Intent(getContext(), ProfileActivity.class);
                i.putExtra("user", (tweet.getUser()));
                getContext().startActivity(i);
            }
        });

        viewHolder.tvScreenName.setOnClickListener(new View.OnClickListener() {
            Tweet s = getItem(position);

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), TweetDisplayActivity.class);
                i.putExtra("tweet_selected", s);   //either be serializable or parcelable
                getContext().startActivity(i);
            }
        });

        setUpRetweetButtonListener(viewHolder, position);
        setUpFavoriteButtonListener(viewHolder, position);
        //return view to be inserted
        return convertView;
    }


    private void setUpRetweetButtonListener(final ViewHolder viewHolder, final int position) {

        viewHolder.tvRetweetCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DEBUG", "Clicked to retweet");
                if (fragmentListener != null) {
                    fragmentListener.onClickToRetweet(position);
                }
            }
        });
    }

    private void setUpFavoriteButtonListener(ViewHolder viewHolder, final int position) {
        viewHolder.tvFavCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DEBUG", "Clicked to star icon");
                if (fragmentListener != null) {
                    fragmentListener.onClickToFavorite(position);
                }
            }
        });
    }

    public interface TAFragmentListener {
        public void onClickToFavorite(int position);

        public void onClickToRetweet(int position);
    }

    public void setFragmentListener(TAFragmentListener fragmentListener) {
        this.fragmentListener = fragmentListener;
    }

}
