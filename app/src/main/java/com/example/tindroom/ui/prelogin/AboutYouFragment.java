package com.example.tindroom.ui.prelogin;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.tindroom.R;
import com.example.tindroom.data.model.Faculty;
import com.example.tindroom.data.model.User;
import com.example.tindroom.network.RetrofitService;
import com.example.tindroom.network.TindroomApiService;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AboutYouFragment extends Fragment {

    private List<Faculty> facultyList;
    private Spinner facultySpinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about_you, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Retrofit retrofit = RetrofitService.getRetrofit();
        TindroomApiService tindroomApiService = retrofit.create(TindroomApiService.class);

        facultyList = new ArrayList<Faculty>();

        facultySpinner = getView().findViewById(R.id.faculty);
        facultyList.clear();

        Call<List<Faculty>> call = tindroomApiService.getFaculties();

        call.enqueue(new Callback<List<Faculty>>() {

            @Override
            public void onResponse(final Call<List<Faculty>> call, final Response<List<Faculty>> response) {
                assert response.body() != null;
                facultyList.addAll(response.body());
                String[] items = new String[facultyList.size()];

                items = facultyList.stream().map(Faculty::getName).toArray(String[]::new);

                ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(getContext(),
                                                                    android.R.layout.simple_spinner_dropdown_item,
                                                                    items);
                facultySpinner.setAdapter(spinnerArrayAdapter);
            }

            @Override
            public void onFailure(final Call<List<Faculty>> call, final Throwable t) {
                Log.d("failure!!!!", t.toString());
            }
        });


        User user = (new User("Anđela", "1999-07-20", "opis", 'F', 1, 'F', 18, 25, true, 1500.0, 2000.0, null));
        Call<User> post = tindroomApiService.register(user);
        post.enqueue(new Callback<User>() {

            @Override
            public void onResponse(final Call<User> call, final Response<User> response) {
                Log.d("success", response.body().getFeedback().toString());
            }

            @Override
            public void onFailure(final Call<User> call, final Throwable t) {
                Log.d("failure", t.toString());
            }
        });
    }

    public void navigateToHomeActivity(View view) {
        NavDirections action = AboutYouFragmentDirections.actionAboutYouFragmentToHomeActivity();
        Navigation.findNavController(view).navigate(action);
    }

}