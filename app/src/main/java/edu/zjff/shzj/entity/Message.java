package edu.zjff.shzj.entity; /**
 * Copyright 2020 bejson.com
 */

import android.os.Parcel;
import android.os.Parcelable;

import edu.zjff.shzj.entity.User;

/**
 * Auto-generated: 2020-08-03 14:14:14
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Message implements Parcelable {

    private String code;
    private String message;
    private User user;

    protected Message(Parcel in) {
        code = in.readString();
        message = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

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
        dest.writeString(code);
        dest.writeString(message);
        dest.writeParcelable(user, flags);
    }
}