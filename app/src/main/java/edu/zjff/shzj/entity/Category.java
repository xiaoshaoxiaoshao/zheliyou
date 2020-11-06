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
public class Category implements Parcelable {
    @Override
    public String toString() {
        return "Category{" +
                "categoryName='" + categoryName + '\'' +
                '}';
    }

    public Category() {
    }


    protected Category(Parcel in) {
        categoryName = in.readString();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    public String getCategoryName() {
        return categoryName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private String categoryName;
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(categoryName);
    }
}