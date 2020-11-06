package edu.zjff.shzj.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.EditText;

import java.util.Date;

public class User implements Parcelable {
    String username;
    String password;
    String phone;
    String email;
    String captcha;
    String avatar;
    String token;
    String description;

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    String background;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    Long date=new Long(0);

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getToken() {
        return token;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setToken(String token) {
        this.token = token;
    }

    protected User(Parcel in) {
        username = in.readString();
        password = in.readString();
        phone = in.readString();
        email = in.readString();
        captcha = in.readString();
        avatar = in.readString();
        token = in.readString();
        date = in.readLong();
        description=in.readString();
        background=in.readString();

    }
    public User(){}

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", captcha='" + captcha + '\'' +
                ", avatar='" + avatar + '\'' +
                ", token='" + token + '\'' +
                ", date=" + date +
                '}';
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeString(captcha);
        dest.writeString(avatar);
        dest.writeString(token);
        dest.writeLong(date);
        dest.writeString(description);
        dest.writeString(background);
    }

}
