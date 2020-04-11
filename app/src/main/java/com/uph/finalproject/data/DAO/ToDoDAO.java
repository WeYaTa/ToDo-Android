package com.uph.finalproject.data.DAO;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.uph.finalproject.data.model.ToDo;

import java.util.ArrayList;

@Dao
public interface ToDoDAO {

    @Insert
    long insertToDo(ToDo todo);

    @Update
    int updateToDo(ToDo todo);

    @Delete
    int deleteToDo(ToDo todo);

    @Query("SELECT * FROM todos")
    ToDo[] getAllToDos();

    @Query("SELECT * FROM todos WHERE todoID = :id LIMIT 1")
    ToDo getToDoByID(int id);

    @Query("SELECT * FROM todos WHERE boardID = :id")
    ToDo[] getToDosByBoardID(int id);

    @Query("SELECT * FROM todos WHERE userID = :userID")
    ToDo[] getToDosByUserID(String userID);
}
