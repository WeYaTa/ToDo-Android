package com.uph.finalproject.data.factory;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.uph.finalproject.data.DAO.BoardDAO;
import com.uph.finalproject.data.DAO.ToDoDAO;
import com.uph.finalproject.data.DAO.UserDAO;
import com.uph.finalproject.data.model.Board;
import com.uph.finalproject.data.model.ToDo;
import com.uph.finalproject.data.model.User;

@Database(entities = {User.class, Board.class, ToDo.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDAO UserDAO();
    public abstract BoardDAO BoardDAO();
    public abstract ToDoDAO ToDoDAO();

}
