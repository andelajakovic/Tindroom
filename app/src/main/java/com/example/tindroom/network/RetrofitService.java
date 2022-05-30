package com.example.tindroom.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class RetrofitService {

//    private static final String BASE_URL = "http://localhost:3000/";
    private static final String BASE_URL = "https://tindroom-api.herokuapp.com/";

    public static Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(RetrofitService.getBASE_URL())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static String getBASE_URL() {
        return BASE_URL;
    }
}
