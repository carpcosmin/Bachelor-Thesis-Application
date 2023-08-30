package com.example.aplicatielicenta.retrofit;

import retrofit2.Call;
import retrofit2.http.HEAD;

public interface ApiService {
    @HEAD("/")
    Call<Void> pingServer();
}
