package edu.zjff.shzj.entity;

/**
 * Copyright 2020 bejson.com
 */

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Auto-generated: 2020-07-16 19:0:43
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Comment {

    private String avatar;
    private int commentId;
    private String content;
    private long date;
    private String nickname;
    private ArrayList<Child> child;

    @Override
    public String toString() {
        return "Comment{" +
                "avatar='" + avatar + '\'' +
                ", commentId=" + commentId +
                ", content='" + content + '\'' +
                ", date=" + date +
                ", nickname='" + nickname + '\'' +
                ", child=" + child +
                '}';
    }

    public ArrayList<Child> getChild() {
        return child;
    }

    public void setChild(ArrayList<Child> child) {
        this.child = child;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public String getAvatar() {
        return avatar;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }
    public int getCommentId() {
        return commentId;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public String getContent() {
        return content;
    }

    public void setDate(long date) {
        this.date = date;
    }
    public long getDate() {
        return date;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getNickname() {
        return nickname;
    }

}