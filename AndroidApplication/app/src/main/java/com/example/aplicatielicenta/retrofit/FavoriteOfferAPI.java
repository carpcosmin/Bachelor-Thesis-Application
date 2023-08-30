package com.example.aplicatielicenta.retrofit;

import com.example.aplicatielicenta.entities.FavoriteOffer;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FavoriteOfferAPI {
    @GET("/users/{userId}/favorite-offers")
    Call<List<FavoriteOffer>> getFavoriteOffersForUser(@Path("userId") int userId);

    @POST("/users/{userId}/offers/{offerId}/favorite")
    Call<Void> addOfferToFavorite(@Path("userId") int userId, @Path("offerId") int offerId);

    @DELETE("/favorites/{fid}")
    Call<Void> deleteFavoriteOfferById(@Path("fid") int fid);
}
