package com.example.aplicatielicenta.retrofit;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ProfilePictureAPI {

    @GET("users/{uid}/profilePicture")
    Call<ResponseBody> getProfilePicture(@Path("uid") int uid);

    @Multipart
    @POST("users/{uid}/profilePicture")
    Call<Void> uploadProfilePicture(@Path("uid") int userId, @Part MultipartBody.Part image);

    @DELETE("users/{uid}/profilePicture")
    Call<ResponseBody> deleteProfilePicture(@Path("uid") int uid);
}
