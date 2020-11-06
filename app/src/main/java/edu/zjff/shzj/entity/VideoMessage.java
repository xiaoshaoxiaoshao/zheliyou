package edu.zjff.shzj.entity;

/**
 * Copyright 2020 bejson.com
 */

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Auto-generated: 2020-09-03 13:52:29
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class VideoMessage implements Parcelable {
    public VideoMessage(){

    }
    protected VideoMessage(Parcel in) {
        cover = in.readString();
        creatorId = in.readInt();
        focused = in.readByte() != 0;
        liked = in.readByte() != 0;
        nice = in.readInt();
        profile = in.readString();
        pubDate = in.readLong();
        tag = in.readString();
        title = in.readString();
        url = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
        videoId = in.readInt();
    }

    public static final Creator<VideoMessage> CREATOR = new Creator<VideoMessage>() {
        @Override
        public VideoMessage createFromParcel(Parcel in) {
            return new VideoMessage(in);
        }

        @Override
        public VideoMessage[] newArray(int size) {
            return new VideoMessage[size];
        }
    };

    @Override
    public String toString() {
        return "VideoMessage{" +
                "cover='" + cover + '\'' +
                ", creatorId=" + creatorId +
                ", focused=" + focused +
                ", liked=" + liked +
                ", nice=" + nice +
                ", profile='" + profile + '\'' +
                ", pubDate=" + pubDate +
                ", tag='" + tag + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", user=" + user +
                ", videoCategory=" + videoCategory +
                ", videoId=" + videoId +
                '}';
    }

    private String cover;
    private int creatorId;
    private boolean focused;
    private boolean liked;
    private int nice;
    private String profile;
    private long pubDate;
    private String tag;
    private String title;
    private String url;
    private User user;
    private VideoCategory videoCategory;
    private int videoId;
    public void setCover(String cover) {
        this.cover = cover;
    }
    public String getCover() {
        return cover;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }
    public int getCreatorId() {
        return creatorId;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }
    public boolean getFocused() {
        return focused;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }
    public boolean getLiked() {
        return liked;
    }

    public void setNice(int nice) {
        this.nice = nice;
    }
    public int getNice() {
        return nice;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
    public String getProfile() {
        return profile;
    }

    public void setPubDate(long pubDate) {
        this.pubDate = pubDate;
    }
    public long getPubDate() {
        return pubDate;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
    public String getTag() {
        return tag;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public String getUrl() {
        return url;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public User getUser() {
        return user;
    }

    public void setVideoCategory(VideoCategory videoCategory) {
        this.videoCategory = videoCategory;
    }
    public VideoCategory getVideoCategory() {
        return videoCategory;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }
    public int getVideoId() {
        return videoId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cover);
        dest.writeInt(creatorId);
        dest.writeByte((byte) (focused ? 1 : 0));
        dest.writeByte((byte) (liked ? 1 : 0));
        dest.writeInt(nice);
        dest.writeString(profile);
        dest.writeLong(pubDate);
        dest.writeString(tag);
        dest.writeString(title);
        dest.writeString(url);
        dest.writeParcelable(user, flags);
        dest.writeInt(videoId);
    }
}