package com.example.aplicatielicenta.retrofit;

import com.example.aplicatielicenta.LogInRequest;
import com.example.aplicatielicenta.entities.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserAPI {

    @GET("/user/get-all")
    Call<List<User>> getAllUsers();

    @GET("/user/{uid}")
    Call<User> getUserById(@Path("uid") int uid);

    @GET("/user/token/{token}")
    Call<User> getUserByToken(@Path("token") String token);

    @POST("/user/save")
    Call<User> saveUser(@Body User user);

    @POST("/user/login")
    Call<User> login(@Body LogInRequest loginRequest);

    @PUT("/user/{uid}")
    Call<User> updateUserById(@Path("uid") int id, @Body User user);

    @DELETE("/user/{uid}")
    Call<Void> deleteUserById(@Path("uid") int id);

}
