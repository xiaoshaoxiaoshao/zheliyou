package edu.zjff.shzj.entity;

/**
 * Copyright 2020 bejson.com
 */

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Auto-generated: 2020-07-03 20:26:8
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Data implements Parcelable {
    @Override
    public String toString() {
        return "Data{" +
                "articleId='" + articleId + '\'' +
                ", category=" + category +
                ", categoryId='" + categoryId + '\'' +
                ", clicks='" + clicks + '\'' +
                ", content='" + content + '\'' +
                ", creatorId='" + creatorId + '\'' +
                ", nice='" + nice + '\'' +
                ", pubDate='" + pubDate + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", title='" + title + '\'' +
                ", user=" + user +
                '}';
    }

    private String articleId;
    private Category category;
    private String categoryId;
    boolean like;

    public void setLike(boolean like) {
        this.like = like;
    }
    public boolean getLike(){
        return like;
    }
    public String getArticleId() {
        return articleId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getClicks() {
        return clicks;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public String getNice() {
        return nice;
    }

    public String getPubDate() {
        return pubDate;
    }

    public static Creator<Data> getCREATOR() {
        return CREATOR;
    }

    private String clicks;
    private String content;
    private String creatorId;
    private String nice;
    private String pubDate;
    private String thumbnail;
    private String title;
    private User user;

    public Data(){}

    protected Data(Parcel in) {
        articleId = in.readString();
        category = in.readParcelable(Category.class.getClassLoader());
        categoryId = in.readString();
        clicks = in.readString();
        content = in.readString();
        creatorId = in.readString();
        nice = in.readString();
        pubDate = in.readString();
        thumbnail = in.readString();
        title = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
        like = in.readByte() != 0;
    }

    public static final Creator<Data> CREATOR = new Creator<Data>() {
        @Override
        public Data createFromParcel(Parcel in) {
            return new Data(in);
        }

        @Override
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setClicks(String clicks) {
        this.clicks = clicks;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public void setNice(String nice) {
        this.nice = nice;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
    public Category getCategory() {
        return category;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public String getContent() {
        return content;
    }


    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
    public String getThumbnail() {
        return thumbnail;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public User getUser() {
        return user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(articleId);
        dest.writeParcelable(category, flags);
        dest.writeString(categoryId);
        dest.writeString(clicks);
        dest.writeString(content);
        dest.writeString(creatorId);
        dest.writeString(nice);
        dest.writeString(pubDate);
        dest.writeString(thumbnail);
        dest.writeString(title);
        dest.writeParcelable(user, flags);
        dest.writeByte((byte) (like ? 1 : 0));
    }
}