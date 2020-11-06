package edu.zjff.shzj.entity;

/**
 * Copyright 2020 bejson.com
 */
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Auto-generated: 2020-09-03 13:52:29
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class VideoRoot {

    private String code;
    @SerializedName("data")
    private List<VideoMessage> videoMessages;

    public List<VideoMessage> getVideoMessages() {
        return videoMessages;
    }

    public void setVideoMessages(List<VideoMessage> videoMessages) {
        this.videoMessages = videoMessages;
    }

    @Override
    public String toString() {
        return "VideoRoot{" +
                "code='" + code + '\'' +
                ", videoMessages=" + videoMessages +
                ", message='" + message + '\'' +
                '}';
    }

    private String message;
    public void setCode(String code) {
        this.code = code;
    }
    public String getCode() {
        return code;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

}