package com.uph.finalproject.data.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.uph.finalproject.data.model.User;

import java.util.ArrayList;

@Dao
public interface UserDAO {

    @Insert
    long insertUser(User user);

    @Update
    int updateUser(User user);

    @Delete
    int deleteUser(User user);

    @Query("SELECT * FROM users")
    User[] getAllUsers();

    @Query("SELECT * FROM users WHERE userID = :id LIMIT 1")
    User getUserByUserID(String id);
}
