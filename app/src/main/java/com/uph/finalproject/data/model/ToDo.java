package com.uph.finalproject.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "todos", foreignKeys = {@ForeignKey(entity = Board.class,
        parentColumns = "boardID",
        childColumns = "boardID",
        onDelete = ForeignKey.CASCADE),

        @ForeignKey(entity = User.class,
                parentColumns = "userID",
                childColumns = "userID",
                onDelete = ForeignKey.CASCADE)})
public class ToDo implements Serializable {

    private static final long serialVersionUID = 1L;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "todoID", index = true)
    @SerializedName("todoID")
    private int todoID;

    @ColumnInfo(name = "status")
    @SerializedName("status")
    private String status;

    @ColumnInfo(name = "description")
    @SerializedName("description")
    private String description;

    @ColumnInfo(name = "boardID", index = true)
    @SerializedName("boardID")
    private int boardID;

    @ColumnInfo(name = "userID", index = true)
    @SerializedName("userID")
    private String userID;

    @Ignore
    public ToDo(String status, String description) {
        this.status = status;
        this.description = description;
    }

    public ToDo(String status, String description, int boardID, String userID) {
        this.status = status;
        this.description = description;
        this.boardID = boardID;
        this.userID = userID;
    }

    @Ignore
    public ToDo(int todoID, String status, String description, int boardID, String userID) {
        this.todoID = todoID;
        this.status = status;
        this.description = description;
        this.boardID = boardID;
        this.userID = userID;
    }


    public int getTodoID() {
        return todoID;
    }

    public void setTodoID(int todoID) {
        this.todoID = todoID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getBoardID() {
        return boardID;
    }

    public void setBoardID(int boardID) {
        this.boardID = boardID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
