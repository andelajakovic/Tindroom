
package com.example.tindroom.network;

import com.example.tindroom.data.model.Faculty;
import com.example.tindroom.data.model.Neighborhood;
import com.example.tindroom.data.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TindroomApiService {
    @GET("faculties")
    Call<List<Faculty>> getFaculties();

    @GET("faculties/{faculty_id}")
    Call<Faculty> getFacultyById(@Path("faculty_id") Long facultyId);

    @GET("neighborhoods")
    Call<List<Neighborhood>> getNeighborhoods();

    @GET("neighborhoods/{neighborhood_id}")
    Call<Neighborhood> getNeighborhoodById(@Path("neighborhood_id") int neighborhoodId);

    @GET("users")
    Call<List<User>> getUsers();

    @GET("users/{user_id}")
    Call<User> getUserById(@Path("user_id") String userId);

    @POST("users/register")
    Call<User> registerUser(@Body User post);

    @PATCH("users/update/{user_id}")
    Call<User> updateUserById(@Path("user_id") String user_id, @Body User user);
}

