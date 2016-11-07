package com.dsatija.apps.twittwit.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dsatija.apps.twittwit.R;
import com.dsatija.apps.twittwit.adapter.TweetsArrayAdapter;
import com.dsatija.apps.twittwit.listeners.EndlessScrollListener;
import com.dsatija.apps.twittwit.models.Tweet;
import com.dsatija.apps.twittwit.network.NetworkConnectivity;
import com.dsatija.apps.twittwit.network.TwitterApplication;
import com.dsatija.apps.twittwit.network.TwitterClient;
import com.dsatija.apps.twittwit.utilities.TwitterConstants;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Disha on 10/20/2016.
 */
public abstract class TweetsListFragment extends Fragment {

    public TweetsArrayAdapter getAdapter() {
        return aTweets;
    }

    private TweetsArrayAdapter aTweets;
    private ArrayList<Tweet> tweets;
    private ListView lvTweets;
    protected SwipeRefreshLayout swipeContainer;
    private View v;
    TwitterClient client;
    private ProgressBar progressBarFooter;
    protected static long earliestID = -1;


    //inflation logic

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_tweets_list, parent, false);
        lvTweets = (ListView) v.findViewById(R.id.lvTweets);
        tweets = new ArrayList<>();
        setupListWithFooter();
        aTweets = new TweetsArrayAdapter(getActivity(), tweets);
        client = TwitterApplication.getRestClient();
        lvTweets.setAdapter(aTweets);
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);

        setUpView();
        setUpFavFragmentListener();
        return v;
    }

    private void setUpFavFragmentListener() {

        aTweets.setFragmentListener(new TweetsArrayAdapter.TAFragmentListener() {
            @Override
            public void onClickToFavorite(final int position) {
                if (!NetworkConnectivity.isNetworkAvailable(getActivity())) {
                    Log.e("ERROR", "no network");
                    Toast.makeText(getActivity(), "No network", Toast.LENGTH_LONG).show();
                } else {

                    Tweet tweet = (Tweet) aTweets.getItem(position);
                    long tweetID = tweet.getUid();
                    if (!tweet.isFavorited()) {//it is not favorited yet, go and favorite

                        client.makeFavorite(tweetID, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                                Log.d("DEBUG", jsonObject.toString());
                                Tweet tweet = (Tweet) aTweets.getItem(position);
                                int newFC = tweet.getFavCount() + 1;
                                tweet.setFavorited(true);
                                tweet.setFavCount(newFC);
                                aTweets.notifyDataSetChanged();
                                Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                Log.e("ERROR", errorResponse.toString());
                                Toast.makeText(getActivity(), "failed", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {//unfavorite

                        client.removeFavorite(tweetID, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                                Log.d("DEBUG", jsonObject.toString());
                                Tweet tweet = (Tweet) aTweets.getItem(position);
                                int newFC = tweet.getFavCount() - 1;
                                tweet.setFavorited(false);
                                tweet.setFavCount(newFC);
                                aTweets.notifyDataSetChanged();
                                Toast.makeText(getActivity(), "unfav success", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                Log.e("ERROR", errorResponse.toString());
                                Toast.makeText(getActivity(), "unfav failed", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }


            @Override
            public void onClickToRetweet(final int position) {
                if (!NetworkConnectivity.isNetworkAvailable(getActivity())) {
                    Log.e("ERROR", "no network");
                    Toast.makeText(getActivity(), "No Network", Toast.LENGTH_LONG).show();
                } else {

                    Tweet tweet = (Tweet) aTweets.getItem(position);
                    long tweetID = tweet.getUid();
                    String retweetIDstr = tweet.getCurrent_user_retweet_id_str();

                    if (!tweet.isRetweeted()) {//not retweeted before, retweet now

                        client.retweet(tweetID, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                                Log.d("DEBUG", jsonObject.toString());

                                Tweet tweet = (Tweet) aTweets.getItem(position);
                                int newRC = tweet.getRetweetCount() + 1;
                                tweet.setRetweeted(true);
                                tweet.setRetweetCount(newRC);
                                Tweet updatedTweet = Tweet.fromJson(jsonObject);
                                //tweet.setCurrent_user_retweet_id_str(updatedTweet.getCurrent_user_retweet_id_str());
                                tweet.setCurrent_user_retweet_id_str(updatedTweet.getId_str_x());
                                aTweets.notifyDataSetChanged();
                                Toast.makeText(getActivity(), "success_on_retweet", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                Log.e("ERROR", errorResponse.toString());
                                Toast.makeText(getActivity(), "failed_to_retweet", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else if (retweetIDstr != null) {//was already retweeted, now destroy retweet

                        Log.d("DEBUG", "retweet id str: " + retweetIDstr);
                        client.destroyRetweet(retweetIDstr, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                                Log.d("DEBUG", jsonObject.toString());
                                Tweet tweet = (Tweet) aTweets.getItem(position);
                                int newRC = tweet.getRetweetCount() - 1;
                                tweet.setRetweeted(false);
                                tweet.setRetweetCount(newRC);
                                aTweets.notifyDataSetChanged();
                                Toast.makeText(getActivity(), "success_on_unretweet", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                Log.e("ERROR", errorResponse.toString());
                                Toast.makeText(getActivity(), "failed_to_unretweet", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Log.d("DEBUG", "retweet id was null");
                        Toast.makeText(getActivity(), "failed_to_unretweet", Toast.LENGTH_LONG).show();
                    }
                }
            }

        });
    }


    //creation lifecycle event
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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
                populateTimeline(TwitterConstants.SWIPE, 1);
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

                customLoadMoreDataFromApi();
                return true;
            }
        });


    }

    public void customLoadMoreDataFromApi() {

        //make sure to put -1, bc twitter sends back those equal or less
        populateTimeline(TwitterConstants.SCROLL, earliestID - 1);
    }


    protected abstract void populateTimeline(int refType, long offset);


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


    public void addAll(List<Tweet> tweets) {
        aTweets.addAll(tweets);


    }

    public void insert(Tweet tweet, int index) {
        aTweets.insert(tweet, index);


    }

    // Adds footer to the list default hidden progress
    public void setupListWithFooter() {
        // Find the ListView
        // Inflate the footer

        if (progressBarFooter == null) {
            View footer = getActivity().getLayoutInflater().inflate(
                    R.layout.footer_progress, null);
            // Find the progressbar within footer
            progressBarFooter = (ProgressBar)
                    footer.findViewById(R.id.pbFooterLoading);
            // Add footer to ListView before setting adapter
            lvTweets.addFooterView(footer);
        }

    }

    // Show progress
    public void showProgressBar() {
        if (progressBarFooter != null) {
            progressBarFooter.setVisibility(View.VISIBLE);
        }

    }

    // Hide progress
    public void hideProgressBar() {
        if (progressBarFooter != null) {
            progressBarFooter.setVisibility(View.GONE);
        }
    }

    public void updateItem(Tweet tweetChanged) {
        //find and update the item
        Tweet tweetToBeUpdated = Tweet.findTweet(tweets, tweetChanged.getUid());
        if (tweetToBeUpdated != null) {
            tweetToBeUpdated.setFavCount(tweetChanged.getFavCount());
            tweetToBeUpdated.setFavorited(tweetChanged.isFavorited());
            tweetToBeUpdated.setRetweetCount(tweetChanged.getRetweetCount());
            tweetToBeUpdated.setRetweeted(tweetChanged.isRetweeted());
            tweetToBeUpdated.setCurrent_user_retweet_id_str(tweetChanged.getCurrent_user_retweet_id_str());
            aTweets.notifyDataSetChanged();
        }
    }


}
