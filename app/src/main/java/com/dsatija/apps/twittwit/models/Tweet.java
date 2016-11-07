package com.dsatija.apps.twittwit.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.util.Log;

import com.dsatija.apps.twittwit.dao.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
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
public class Tweet extends BaseModel implements Parcelable {
    public Tweet() {

    }

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

    @Column
    private int retweetCount;
    @Column
    private int favCount;

    @Column
    private boolean favorited = false;

    @Column
    private boolean retweeted = false;

    @Column
    private long inReplyToStatusID = -1;

    public void setInReplyToStatusID(long inReplyToStatusID) {
        this.inReplyToStatusID = inReplyToStatusID;
    }

    public User getRetweetingUser() {
        return retweetingUser;
    }

    public void setRetweetingUser(User retweetingUser) {
        this.retweetingUser = retweetingUser;
    }

    public void setRetweet(boolean retweet) {
        isRetweet = retweet;
    }

    public long getInReplyToUserID() {
        return inReplyToUserID;
    }

    public void setInReplyToUserID(long inReplyToUserID) {
        this.inReplyToUserID = inReplyToUserID;
    }

    @Column
    private long inReplyToUserID = -1;

    public String getInReplyToScreenName() {
        return inReplyToScreenName;
    }

    public void setInReplyToScreenName(String inReplyToScreenName) {
        this.inReplyToScreenName = inReplyToScreenName;
    }

    @Column
    @ForeignKey(onDelete = ForeignKeyAction.CASCADE, onUpdate = ForeignKeyAction.CASCADE)

    private User retweetingUser = null;

    @Column
    private boolean isRetweet = false;

    @Column
    private String current_user_retweet_id_str = null;

    @Column
    private String id_str_x = null;

    @Column
    private String inReplyToScreenName = null;

    //deserialize
    public static Tweet fromJson(JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        //extract and store values from json
        try {
            if (jsonObject.optJSONObject("current_user_retweet") != null) {
                tweet.current_user_retweet_id_str = jsonObject.getJSONObject("current_user_retweet").getString("id_str");
                Log.d("DEBUG", "retweet is found: " + tweet.current_user_retweet_id_str);
            }
            tweet.id_str_x = jsonObject.getString("id_str");

            if (jsonObject.optJSONObject("retweeted_status") != null) {//retweet
                tweet.retweetingUser = User.fromJson(jsonObject.getJSONObject("user"));
                jsonObject = jsonObject.getJSONObject("retweeted_status");
                tweet.isRetweet = true;
            }

            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
            tweet.favCount = User.fromJson(jsonObject.getJSONObject("user")).getFavCount();
            tweet.retweetCount = jsonObject.getInt("retweet_count");
            tweet.favorited = jsonObject.getBoolean("favorited");
            tweet.retweeted = jsonObject.getBoolean("retweeted");

            if (!tweet.isRetweet()) {

                if (jsonObject.optString("in_reply_to_status_id") != null && !jsonObject.optString("in_reply_to_status_id").equals("null")) {
                    tweet.inReplyToStatusID = jsonObject.getLong("in_reply_to_status_id");
                }
                if (jsonObject.optString("in_reply_to_user_id") != null && !jsonObject.optString("in_reply_to_user_id").equals("null")) {
                    tweet.inReplyToUserID = jsonObject.getLong("in_reply_to_user_id");
                }
                if (jsonObject.optString("in_reply_to_screen_name") != null && !jsonObject.optString("in_reply_to_screen_name").equals("null")) {
                    tweet.inReplyToScreenName = jsonObject.getString("in_reply_to_screen_name");
                }
            }

            tweet.save();

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


    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public static Tweet findTweet(ArrayList<Tweet> tweets, long uid) {
        for (int i = 0; i < tweets.size(); i++) {
            if (tweets.get(i).getUid() == uid) {
                return tweets.get(i);
            }
        }
        return null;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public String getCurrent_user_retweet_id_str() {
        return current_user_retweet_id_str;
    }


    public void setCurrent_user_retweet_id_str(String current_user_retweet_id_str) {
        this.current_user_retweet_id_str = current_user_retweet_id_str;
    }

    public long getInReplyToStatusID() {
        return inReplyToStatusID;
    }

    public boolean isRetweet() {
        return isRetweet;
    }

    public void setId_str_x(String id_str_x) {
        this.id_str_x = id_str_x;
    }

    public String getId_str_x() {
        return id_str_x;
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


    public int getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }


    public int getFavCount() {
        return favCount;
    }

    public void setFavCount(int favCount) {
        this.favCount = favCount;
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


    protected Tweet(Parcel in) {
        body = in.readString();
        uid = in.readLong();
        this.user = in.readParcelable(User.class.getClassLoader());
        createdAt = in.readString();
        retweetCount = in.readInt();
        favCount = in.readInt();
        favorited = in.readByte() != 0x00;
        retweeted = in.readByte() != 0x00;
        inReplyToStatusID = in.readLong();
        inReplyToUserID = in.readLong();
        isRetweet = in.readByte() != 0x00;
        current_user_retweet_id_str = in.readString();
        //this.retweetingUser = in.readParcelable(User.class.getClassLoader());
        id_str_x = in.readString();
        inReplyToScreenName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(body);
        dest.writeLong(uid);
        dest.writeParcelable(this.user, 0);
        dest.writeString(createdAt);
        dest.writeInt(retweetCount);
        dest.writeInt(favCount);
        dest.writeByte((byte) (favorited ? 0x01 : 0x00));
        dest.writeByte((byte) (retweeted ? 0x01 : 0x00));
        dest.writeLong(inReplyToStatusID);
        dest.writeLong(inReplyToUserID);
        dest.writeByte((byte) (isRetweet ? 0x01 : 0x00));
        dest.writeString(current_user_retweet_id_str);
        dest.writeString(id_str_x);
        //dest.writeParcelable(this.retweetingUser, 0);
        dest.writeString(inReplyToScreenName);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Tweet> CREATOR = new Parcelable.Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(Parcel in) {
            return new Tweet(in);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
}