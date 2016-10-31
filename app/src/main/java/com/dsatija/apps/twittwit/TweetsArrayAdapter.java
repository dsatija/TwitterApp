package com.dsatija.apps.twittwit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dsatija.apps.twittwit.models.Tweet;

import java.util.List;

/**
 * Created by Disha on 10/20/2016.
 */
//taking tweet objects and turning them into views
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {
    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, 0, tweets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // return  super.getView(position,convertView,parent);
        //get tweet
        Tweet tweet = getItem(position);
        //find template
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(com.dsatija.apps.twittwit.R.layout.item_tweet, parent, false);

        }
        //find subview to fill data in  template
        ImageView ivProfileImage = (ImageView) convertView.findViewById(com.dsatija.apps.twittwit.R.id.ivProfileImage);
        TextView tvUsername = (TextView) convertView.findViewById(R.id.tvUserName);
        TextView tvBody = (TextView) convertView.findViewById(R.id.tvBody);
        TextView tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
        TextView tvTimeStamp = (TextView) convertView.findViewById(R.id.tvTimeStamp);
        //populate data into subview
        tvUsername.setText("@" + tweet.getUser().getScreenName());
        tvBody.setText(tweet.getBody());
        tvScreenName.setText(tweet.getUser().getName());
        tvTimeStamp.setText(tweet.getRelativeTimeAgo());
        ivProfileImage.setImageResource(android.R.color.transparent);
        Glide.with(getContext()).load(tweet.getUser().getProfileImageUrl()).fitCenter().
                centerCrop().into(ivProfileImage);
        //return view to be inserted
        return convertView;
    }
}
