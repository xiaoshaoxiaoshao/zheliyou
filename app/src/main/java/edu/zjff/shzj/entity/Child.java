package edu.zjff.shzj.entity;
/**
 * Copyright 2020 bejson.com
 */

/**
 * Auto-generated: 2020-07-17 18:13:28
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Child {

    private String avatar;
    private int commentId;
    private String content;

    @Override
    public String toString() {
        return "Child{" +
                "avatar='" + avatar + '\'' +
                ", commentId=" + commentId +
                ", content='" + content + '\'' +
                ", date=" + date +
                ", nickname='" + nickname + '\'' +
                ", parentId=" + parentId +
                '}';
    }

    private long date;
    private String nickname;
    private int parentId;
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

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }
    public int getParentId() {
        return parentId;
    }

}