package com.example.tindroom.ui.postlogin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tindroom.R;
import com.example.tindroom.data.local.SharedPreferencesStorage;
import com.example.tindroom.data.model.Faculty;
import com.example.tindroom.data.model.User;
import com.example.tindroom.network.RetrofitService;
import com.example.tindroom.network.TindroomApiService;
import com.example.tindroom.utils.ImageHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_OK;

public class MyProfileFragment extends Fragment {

    TextView changeProfilePicture, usersName, usersFaculty;
    ImageButton editProfile, logout, safety;
    ImageView profilePicture;

    View rootView;
    private TindroomApiService tindroomApiService;
    User sessionUser;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Retrofit retrofit = RetrofitService.getRetrofit();
        tindroomApiService = retrofit.create(TindroomApiService.class);
        sessionUser = SharedPreferencesStorage.getSessionUser(requireContext());

        initViews();
        initListeners();
        initData();

        return rootView;
    }

    private void initViews(){
        changeProfilePicture = rootView.findViewById(R.id.changeProfilePicture);
        usersName = rootView.findViewById(R.id.name);
        usersFaculty = rootView.findViewById(R.id.faculty);
        editProfile = rootView.findViewById(R.id.editProfileButton);
        logout = rootView.findViewById(R.id.logoutButton);
        safety = rootView.findViewById(R.id.safetyButton);
    }

    private void initListeners(){
        changeProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                // TODO (Anđela: bottom popup dialog s opcijama "Nova slika profila" i "Ukloni sliku profila")
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                navigateToSettingsFragment(view);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                SharedPreferencesStorage.setSessionUser(requireContext(), null);
                mAuth.signOut();
                navigateToMainActivity(view);
                requireActivity().finish();
            }
        });

        safety.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToChangePasswordFragment(view);
            }
        });
    }

    private void initData(){
        usersName.setText( sessionUser.getName());
        usersFaculty.setText(sessionUser.getFaculty().getName());
    }


    public void navigateToMainActivity (View view) {
        NavDirections action = MyProfileFragmentDirections.actionMyProfileFragmentToMainActivity();
        Navigation.findNavController(view).navigate(action);
    }

    public void navigateToSettingsFragment (View view) {
        NavDirections action = MyProfileFragmentDirections.actionMyProfileFragmentToSettingsFragment();
        Navigation.findNavController(view).navigate(action);
    }

    public void navigateToChangePasswordFragment (View view) {
        NavDirections action = MyProfileFragmentDirections.actionMyProfileFragmentToChangePasswordFragment();
        Navigation.findNavController(view).navigate(action);
    }


}