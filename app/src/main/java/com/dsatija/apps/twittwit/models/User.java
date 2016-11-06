package com.dsatija.apps.twittwit.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.dsatija.apps.twittwit.dao.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * Created by Disha on 10/20/2016.
 */
@Table(database = MyDatabase.class)
public class User extends BaseModel implements Parcelable {
    public User() {

    }
    //attributes

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    @Column
    private String name;
    @Column
    @PrimaryKey
    private long uid;
    @Column
    private String screenName;
    @Column
    private String profileImageUrl;


    public String getTagLine() {
        return tagline;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFriendsCount() {
        return followingCount;
    }

    private String tagline;

    private int followersCount;
    private int followingCount;
    private int favCount;

    //deserialise user
    public static User fromJson(JSONObject json) {
        User u = new User();
        try {
            u.name = json.getString("name");
            u.uid = json.getLong("id");
            u.screenName = json.getString("screen_name");
            u.profileImageUrl = json.getString("profile_image_url");
            u.tagline = json.getString("description");
            u.followersCount = json.getInt("followers_count");
            u.followingCount = json.getInt("friends_count");

            u.favCount = json.getInt("favourites_count");


            u.save();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return u;
    }

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }


    public int getFavCount() {
        return favCount;
    }

    public void setFavCount(int favCount) {
        this.favCount = favCount;
    }

    public static User findById(Long id) {
        return SQLite.select().
                from(User.class).
                where(User_Table.uid.eq(id)).querySingle();
    }



    protected User(Parcel in) {
        name = in.readString();
        uid = in.readLong();
        screenName = in.readString();
        profileImageUrl = in.readString();
        tagline = in.readString();
        followersCount = in.readInt();
        followingCount = in.readInt();
        favCount = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeLong(uid);
        dest.writeString(screenName);
        dest.writeString(profileImageUrl);
        dest.writeString(tagline);
        dest.writeInt(followersCount);
        dest.writeInt(followingCount);
        dest.writeInt(favCount);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}