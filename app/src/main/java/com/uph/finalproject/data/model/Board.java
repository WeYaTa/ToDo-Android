package com.uph.finalproject.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "boards", foreignKeys = @ForeignKey(entity = User.class,
        parentColumns = "userID",
        childColumns = "userID",
        onDelete = ForeignKey.CASCADE))
public class Board implements Serializable {

    private static final long serialVersionUID = 1L;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "boardID", index = true)
    @SerializedName("boardID")
    private int boardID;

    @ColumnInfo(name = "name")
    @SerializedName("name")
    private String boardName;

    @ColumnInfo(name = "background")
    @SerializedName("background")
    private String backgroundColor;
    
    @ColumnInfo(name = "userID", index = true)
    @SerializedName("userID")
    private String userID;

    public int getBoardID() {
        return boardID;
    }

    public void setBoardID(int boardId) {
        this.boardID = boardId;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Board(String boardName, String color, String userID) {
        this.boardName = boardName;
        this.backgroundColor = color;
        this.userID = userID;
    }

    public Board(int boardID,String boardName, String color, String userID) {
        this.boardID = boardID;
        this.boardName = boardName;
        this.backgroundColor = color;
        this.userID = userID;
    }

    public Board(){}
}
