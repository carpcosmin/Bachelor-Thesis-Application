package com.example.aplicatielicenta.retrofit;

import com.example.aplicatielicenta.entities.Offer;
import com.example.aplicatielicenta.entities.Property;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface OfferAPI {

    @GET("/offer/get-all")
    Call<List<Offer>> getAllOffers();

    @GET("/offer/{oid}")
    Call<Offer> getOfferById(@Path("oid") int oid);

    @POST("/offer/save")
    Call<Offer> saveOffer(@Body Offer offer);

    @PUT("/offer/{oid}")
    Call<Offer> updateOfferById(@Path("oid") int oid, @Body Offer offer);

    @DELETE("/offer/{oid}")
    Call<Void> deleteOfferById(@Path("oid") int oid);
}
