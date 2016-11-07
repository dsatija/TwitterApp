package com.dsatija.apps.twittwit.network;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.dsatija.apps.twittwit.utilities.TwitterConstants;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
    public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
    public static final String REST_CONSUMER_KEY = "JGHoGptQ9R1c695BIWN1O3Fe5";       // Change this
    public static final String REST_CONSUMER_SECRET = "uqvSrUhLKLq7ifAdZw8zvqtw9odTSayGdTmLZvaSaaEQg6GK0F"; // Change this
    public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets"; // Change this (here and in manifest)
    public static final int TWEETS_PER_PAGE = 25;

    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }

    // CHANGE THIS

    //Home Timeline

    public void getHomeTimeline(int refType, long max_id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        //specify params
        RequestParams params = new RequestParams();
        params.put("count", TWEETS_PER_PAGE);
        if (refType == TwitterConstants.SCROLL) {
            //get the 25 tweets before current earliest
            params.put("max_id", max_id);// offset: earliest -1
        } else {
            params.put("since_id", 1);// get the most recent 25
        }
        params.put("include_my_retweet", true);
        //execute
        getClient().get(apiUrl, params, handler);

    }


    public void postTweet(String tweetStr, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();
        params.put("status", tweetStr);
        getClient().post(apiUrl, params, handler);
    }


    public void getMentionsTimeline(int refType, long max_id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", TWEETS_PER_PAGE);
        if (refType == TwitterConstants.SCROLL) {

            params.put("max_id", max_id);
        } else {
            params.put("since_id", 1);// get the most recent 25
        }
        params.put("include_my_retweet", true);

        getClient().get(apiUrl, params, handler);
    }

    public void getUserTimeline(String screenName, int refType, long max_id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 25);
        if (refType == TwitterConstants.SCROLL) {
            params.put("max_id", max_id);
        } else {

            params.put("since_id", 1);
        }
        params.put("include_my_retweet", true);
        params.put("screen_name", screenName);


        getClient().get(apiUrl, params, handler);
    }


    public void getUserInfo(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");
        RequestParams params = new RequestParams();
        params.put("count", 25);
        getClient().get(apiUrl, params, handler);
    }

    public void showTweet(long tweetId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/show.json");
        RequestParams params = new RequestParams();
        params.put("id", tweetId);
        getClient().get(apiUrl, params, handler);
    }

    public void removeFavorite(long tweetID, AsyncHttpResponseHandler handler) {

        String apiUrl = getApiUrl("favorites/destroy.json");
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();
        params.put("id", tweetID);

        //Execute the request
        getClient().post(apiUrl, params, handler);
    }

    public void makeFavorite(long tweetID, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("favorites/create.json");
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();
        params.put("id", tweetID);

        //Execute the request
        getClient().post(apiUrl, params, handler);
    }

    public void retweet(long originalTweetID, AsyncHttpResponseHandler handler) {

        String apiUrl = getApiUrl("statuses/retweet/" + originalTweetID + ".json");
        RequestParams params = new RequestParams();
        getClient().post(apiUrl, params, handler);
    }

    public void destroyRetweet(String retweetIDstr, AsyncHttpResponseHandler handler) {

        String apiUrl = getApiUrl("statuses/destroy/" + retweetIDstr + ".json");
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();

        //Execute the request
        getClient().post(apiUrl, params, handler);
    }



	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
     * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}