package com.uph.finalproject.data.repository;

/**
 * Created by IT on 3/26/2018.
 */
import com.uph.finalproject.data.model.Board;
import com.uph.finalproject.data.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Url;

public interface UserRepo {

    @GET("users")
    Call<List<User>> getAllUsers();

    @GET
    Call<User> getUserByUserID(@Url String url);

    @POST("users")
    Call<User> postUserToDB(@Body User o);

    @PUT("users")
    Call<User> updateUserToDB(@Body User o);

    @DELETE
    Call<Integer> deleteUser(@Url String url);
}