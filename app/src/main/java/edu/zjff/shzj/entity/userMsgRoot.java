/**
 * Copyright 2020 bejson.com
 */
package edu.zjff.shzj.entity;
import java.util.Date;

/**
 * Auto-generated: 2020-09-23 21:3:58
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class userMsgRoot {

    private String score;
    private String code;
    private String background;
    private String description;
    private String avatar;
    private String message;
    private String userId;
    private String username;
    private String createDate;
    public void setScore(String score) {
        this.score = score;
    }
    public String getScore() {
        return score;
    }
    public userMsgRoot(){};
    public void setCode(String code) {
        this.code = code;
    }
    public String getCode() {
        return code;
    }

    public void setBackground(String background) {
        this.background = background;
    }
    public String getBackground() {
        return background;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public String getAvatar() {
        return avatar;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserId() {
        return userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getUsername() {
        return username==null?null:username;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    @Override
    public String toString() {
        return "userMsgRoot{" +
                "score='" + score + '\'' +
                ", code='" + code + '\'' +
                ", background='" + background + '\'' +
                ", description='" + description + '\'' +
                ", avatar='" + avatar + '\'' +
                ", message='" + message + '\'' +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", createDate=" + createDate +
                '}';
    }
}