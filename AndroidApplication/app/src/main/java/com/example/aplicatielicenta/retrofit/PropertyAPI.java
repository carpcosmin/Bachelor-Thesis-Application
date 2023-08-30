package com.example.aplicatielicenta.retrofit;

import com.example.aplicatielicenta.LogInRequest;
import com.example.aplicatielicenta.entities.Property;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PropertyAPI {

    @GET("/property/get-all")
    Call<List<Property>> getAllProperties();

    @GET("/property/{pid}")
    Call<Property> getPropertyById(@Path("pid") int pid);

    @POST("/property/save")
    Call<Property> saveProperty(@Body Property property);

    @PUT("/property/{pid}")
    Call<Property> updatePropertyById(@Path("pid") int pid, @Body Property property);

    @DELETE("/property/{pid}")
    Call<Void> deletePropertyById(@Path("pid") int pid);
}
