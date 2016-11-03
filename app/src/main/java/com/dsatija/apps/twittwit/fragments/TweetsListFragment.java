package com.dsatija.apps.twittwit.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.dsatija.apps.twittwit.R;
import com.dsatija.apps.twittwit.adapter.TweetsArrayAdapter;
import com.dsatija.apps.twittwit.models.Tweet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Disha on 10/20/2016.
 */
public class TweetsListFragment extends Fragment {

    private TweetsArrayAdapter aTweets;
    private ArrayList<Tweet> tweets;
    private ListView lvTweets;


    //inflation logic

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list,parent,false);
        lvTweets = (ListView) v.findViewById(R.id.lvTweets);
        //connect adapter
        lvTweets.setAdapter(aTweets);
        return v;
    }
    //creation lifecycle event
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //construct array list(data source)
        tweets = new ArrayList<>();
        //create adapter from data source
        aTweets = new TweetsArrayAdapter(getActivity(), tweets);

    }
    public void addAll(List<Tweet> tweets){
        aTweets.addAll(tweets);


    }
}
