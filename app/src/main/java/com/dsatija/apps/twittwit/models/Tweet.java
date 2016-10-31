package com.dsatija.apps.twittwit.models;

import android.text.format.DateUtils;

import com.dsatija.apps.twittwit.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Disha on 10/20/2016.
 */
//parse json + encapsulate logic
@Table(database = MyDatabase.class)
public class Tweet extends BaseModel {
    public void setBody(String body) {
        this.body = body;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    //list out attribute
    @Column
    private String body;
    @PrimaryKey
    private long uid;//db id for tweet
    @Column
    @ForeignKey(saveForeignKeyModel = false)
    private User user;
    @Column
    private String createdAt;
    //deserialize

    public static Tweet fromJson(JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        //extract and store values from json
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tweet;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<>();
        //iterate json array n create tweets
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject tweetJson = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJson(tweetJson);
                if (tweet != null) {
                    tweets.add(tweet);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return tweets;
    }

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public String getRelativeTimeAgo() {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);
        String relativeDate = "";
        try {
            long dateMillis = sf.parse(createdAt).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.FORMAT_ABBREV_ALL).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        HashMap<String, String> replaceMappings = new HashMap<>();
        replaceMappings.put(" hours ago", "h");
        replaceMappings.put(" hour ago", "h");
        replaceMappings.put(" minutes ago", "m");
        replaceMappings.put(" minute ago", "m");
        replaceMappings.put(" seconds ago", "s");
        replaceMappings.put(" second ago", "s");
        replaceMappings.put(" day ago", "d");
        replaceMappings.put(" days ago", "d");
        for (String suffixKey : replaceMappings.keySet()) {
            if (relativeDate.endsWith(suffixKey)) {
                relativeDate = relativeDate.replace(suffixKey, replaceMappings.get(suffixKey));
            }
        }
        return relativeDate;
    }

    public static void saveAll(List<Tweet> tweets) {
        FlowManager.getDatabase(MyDatabase.class)
                .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                        new ProcessModelTransaction.ProcessModel<Tweet>() {
                            @Override
                            public void processModel(Tweet tweet) {
                                // do work here -- i.e. user.delete() or user.update()
                                tweet.save();
                            }
                        }).addAll(tweets).build())  // add elements (can also handle multiple)
                .error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {
                    }
                })
                .success(new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {
                    }
                }).build().execute();
    }

    public static List<Tweet> findAll() {
        return SQLite.select().
                from(Tweet.class).queryList();
    }
}
