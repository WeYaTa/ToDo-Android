package com.uph.finalproject.data.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.uph.finalproject.data.model.Board;

import java.util.ArrayList;

@Dao
public interface BoardDAO {

    @Insert
    long insertBoard(Board board);

    @Update
    int updateBoard(Board board);

    @Delete
    int deleteBoard(Board board);

    @Query("SELECT * FROM boards")
    Board[] getAllBoards();

    @Query("SELECT * FROM boards WHERE boardID = :id LIMIT 1")
    Board getBoardbyID(int id);

    @Query("SELECT * FROM boards WHERE userID = :userID")
    Board[] getBoardsByUserID(String userID);
}
