package com.youcmt.youdmcapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Stanislav Ostrovskii on 9/18/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 * Objects in the model package represent either a JSON
 * request or response to a Retrofit call
 */

public class Video implements Parcelable {
    private String vid;
    private String title;
    private String date;
    private String author;
    private String description;

    protected Video(Parcel in) {
        vid = in.readString();
        title = in.readString();
        date = in.readString();
        author = in.readString();
        description = in.readString();
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    public String getVid() {
        return vid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(vid);
        parcel.writeString(title);
        parcel.writeString(date);
        parcel.writeString(author);
        parcel.writeString(description);
    }
}
