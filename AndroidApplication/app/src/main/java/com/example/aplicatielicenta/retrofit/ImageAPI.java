package com.example.aplicatielicenta.retrofit;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ImageAPI {

    @GET("/offer/{oid}/images")
    Call<ResponseBody> getOfferImages(@Path("oid") int oid);

    @GET("/offer/{oid}/imagesIdList")
    Call<List<Long>> getOfferImageIds(@Path("oid") long offerId);

    @Multipart
    @POST("offer/{oid}/images")
    Call<Void> uploadImage(@Path("oid") int offerId, @Part List<MultipartBody.Part> images);

    @DELETE("images/{id}")
    Call<Void> deleteImage(@Path("id") Long id);

    @DELETE("offer/{oid}/images")
    Call<Void> deleteImages(@Path("oid") Long oid);

}
