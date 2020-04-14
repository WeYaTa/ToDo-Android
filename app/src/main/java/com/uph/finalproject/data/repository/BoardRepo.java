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
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface BoardRepo {

    @GET("boards")
    Call<List<Board>> getAllBoards();

//    @GET
//    Call<Board> getBoardByID(@Url String url);

    @GET("boards/{boardID}")
    Call<Board> getBoardByID(@Path("boardID") int boardID);

//    @GET
//    Call<List<Board>> getBoardsByUserID(@Url String url);

    @GET("boards/user/{userID}")
    Call<List<Board>> getBoardsByUserID(@Path("userID") String userID);

    @POST("boards")
    Call<Board> postBoardToDB(@Body Board o);

    @PUT("boards")
    Call<Board> updateBoardToDB(@Body Board o);

    @DELETE
    Call<Integer> deleteBoard(@Url String url);
}