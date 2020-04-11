package com.uph.finalproject.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "users")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "userID", index = true)
    @SerializedName("userID")
    private String userID;

    @ColumnInfo(name = "password")
    @SerializedName("password")
    private String password;

    @ColumnInfo(name = "name")
    @SerializedName("name")
    private String displayName;

    @ColumnInfo(name = "email")
    @SerializedName("email")
    private String email;

    public User(@NonNull String userID, String password, String displayName, String email) {
        this.userID = userID;
        this.password = password;
        this.displayName = displayName;
        this.email = email;
    }

    public User (LoggedInUser loggedInUser){
        this.userID = loggedInUser.getUserID();
        this.password = loggedInUser.getPassword();
        this.displayName = loggedInUser.getDisplayName();
        this.email = loggedInUser.getEmail();
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
