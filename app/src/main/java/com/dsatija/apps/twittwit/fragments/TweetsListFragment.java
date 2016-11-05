package com.dsatija.apps.twittwit.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.dsatija.apps.twittwit.R;
import com.dsatija.apps.twittwit.adapter.TweetsArrayAdapter;
import com.dsatija.apps.twittwit.listeners.EndlessScrollListener;
import com.dsatija.apps.twittwit.models.Tweet;
import com.dsatija.apps.twittwit.network.NetworkConnectivity;
import com.dsatija.apps.twittwit.network.TwitterApplication;
import com.dsatija.apps.twittwit.network.TwitterClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Disha on 10/20/2016.
 */
public class TweetsListFragment extends Fragment {

    private TweetsArrayAdapter aTweets;
    private ArrayList<Tweet> tweets;
    private ListView lvTweets;
    private long max_id = 1;
    private SwipeRefreshLayout swipeContainer;
    private View v;
    TwitterClient client;







    //inflation logic

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_tweets_list,parent,false);
        lvTweets = (ListView) v.findViewById(R.id.lvTweets);
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        //connect adapter
        lvTweets.setAdapter(aTweets);
        setUpView();
        initialOrRefreshPopulateTimeline((long) 0);
        return v;
    }
    //creation lifecycle event
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //construct array list(data source)
        tweets = new ArrayList<>();
        //create adapter from data source
        aTweets = new TweetsArrayAdapter(getActivity(), tweets);
        client = TwitterApplication.getRestClient();

    }

    private void setUpView() {

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkConnectivity.isNetworkAvailable(v.getContext()) != true) {
                    Toast.makeText(v.getContext(), "to get new tweets please go online", Toast.LENGTH_LONG).show();
                    swipeContainer.setRefreshing(false);
                    return;
                }
                initialOrRefreshPopulateTimeline((long) 0);
                swipeContainer.setRefreshing(false);
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        // Setup refresh listener which triggers new data loading

        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            protected boolean onLoadMore(int page, int totalItemsCount) {
                int position = aTweets.getCount() - 1;
                max_id = aTweets.getItem(position).getUid();
                populateTimeline(max_id);
                return true;
            }
        });

    }

    protected void populateTimeline(long offset) {
        /*if (NetworkConnectivity.isNetworkAvailable(getContext()) != true) {
            Snackbar.make(this.getView(), "No internet connection!", Snackbar.LENGTH_LONG)
                    .setAction("RETRY", null).show();
            fetchingSavedTweets();
        } else {


            client.getHomeTimeline(offset, new JsonHttpResponseHandler() {
                //Success

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                    Log.d("Debug", json.toString());
                  /* ArrayList<Tweet> tweets = Tweet.fromJSONArray(json);
                    saveTweets(tweets);
                    addAll(tweets);


                }
                //failure

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                }
            });
        }*/

    }


        private void saveTweets(List<Tweet> newTweets) {
            Tweet.saveAll(newTweets);
        }


    private void fetchingSavedTweets() {

        List<Tweet> savedTweets = Tweet.findAll();
        if (savedTweets != null || savedTweets.isEmpty()) {
            aTweets.clear();
            addAll(savedTweets);
        }
    }

    protected void initialOrRefreshPopulateTimeline(long page) {
        aTweets.clear();

        if (!NetworkConnectivity.isNetworkAvailable(getContext())) {
            addAll(tweets);
            return;
        }

        populateTimeline(page);
    }





    public void addAll(List<Tweet> tweets){
        aTweets.addAll(tweets);


    }

    public void insert(Tweet tweet, int index){
        aTweets.insert(tweet,index);


    }


}
