package edu.zjff.shzj.entity;

/**
 * Copyright 2020 bejson.com
 */
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Auto-generated: 2020-07-03 20:26:8
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class DataRoot implements Parcelable {

    private String code;
    private List<Data> data;
    private String message;

    private List<String> userLikes;
    public List<String> getUserLikes() {
        return userLikes;
    }

    public void setUserLikes(List<String> userLikes) {
        this.userLikes = userLikes;
    }

    public static Creator<DataRoot> getCREATOR() {
        return CREATOR;
    }


    protected DataRoot(Parcel in) {
        code = in.readString();
        data = in.createTypedArrayList(Data.CREATOR);
        message = in.readString();
        userLikes = in.createStringArrayList();
    }

    public static final Creator<DataRoot> CREATOR = new Creator<DataRoot>() {
        @Override
        public DataRoot createFromParcel(Parcel in) {
            return new DataRoot(in);
        }

        @Override
        public DataRoot[] newArray(int size) {
            return new DataRoot[size];
        }
    };

    public void setCode(String code) {
        this.code = code;
    }
    public String getCode() {
        return code;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }
    public List<Data> getData() {
        return data;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeTypedList(data);
        dest.writeString(message);
        dest.writeList(userLikes);
    }

    @Override
    public String toString() {
        return "DataRoot{" +
                "code='" + code + '\'' +
                ", data=" + data +
                ", message='" + message + '\'' +
                ", userLikes=" + userLikes +
                '}';
    }
}