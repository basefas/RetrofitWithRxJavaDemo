package com.basefas.rx.retrofitwithrxjavademo.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * User model
 */
public class User implements Parcelable{
    private String avatar_url;

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.avatar_url);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.avatar_url = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
