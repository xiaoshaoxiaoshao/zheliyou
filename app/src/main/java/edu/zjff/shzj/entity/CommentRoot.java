package edu.zjff.shzj.entity;
/**
 * Copyright 2020 bejson.com
 */
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Auto-generated: 2020-07-16 19:0:43
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class CommentRoot {

    private int article_id;
    private String article_title;
    private String code;
    @SerializedName("data")
    private List<Comment> Comment;
    private String message;

    public List<edu.zjff.shzj.entity.Comment> getComment() {
        return Comment;
    }

    public void setComment(List<edu.zjff.shzj.entity.Comment> comment) {
        Comment = comment;
    }

    public void setArticle_id(int article_id) {
        this.article_id = article_id;
    }
    public int getArticle_id() {
        return article_id;
    }

    public void setArticle_title(String article_title) {
        this.article_title = article_title;
    }
    public String getArticle_title() {
        return article_title;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "CommentRoot{" +
                "article_id=" + article_id +
                ", article_title='" + article_title + '\'' +
                ", code='" + code + '\'' +
                ", Comment=" + Comment +
                ", message='" + message + '\'' +
                '}';
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

}