package com.uph.finalproject.data.repository;

/**
 * Created by IT on 3/26/2018.
 */
import com.uph.finalproject.data.model.Board;
import com.uph.finalproject.data.model.ToDo;
import com.uph.finalproject.data.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Url;

public interface ToDoRepo {

    @GET("todos")
    Call<List<ToDo>> getAllToDos();

    @GET
    Call<List<ToDo>> getToDosByUserID(@Url String url);

    @GET
    Call<List<ToDo>> getToDosByBoardID(@Url String url);

    @POST("todos")
    Call<ToDo> postToDoToDB(@Body ToDo o);

    @PUT("todos")
    Call<ToDo> updateToDoToDB(@Body ToDo o);

    @DELETE
    Call<Integer> deleteToDo(@Url String url);
}