
package com.example.tindroom.network;

import com.example.tindroom.data.model.Faculty;
import com.example.tindroom.data.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface TindroomApiService {
    @GET("faculties")
    Call<List<Faculty>> getFaculties();

    @POST("register")
    Call<User> register(@Body User post);
}

