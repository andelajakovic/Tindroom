package com.example.tindroom.ui.postlogin;

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
import android.widget.TextView;

import com.example.tindroom.R;
import com.example.tindroom.data.model.Faculty;
import com.example.tindroom.network.RetrofitService;
import com.example.tindroom.network.TindroomApiService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MyProfileFragment extends Fragment {

    private TextInputLayout nameInput, dateOfBirthInput, genderInput, facultyInput;
    private TextInputEditText nameEditText, dateOfBirthEditText, descriptionEditText;
    private AutoCompleteTextView genderDropdown, facultyDropdown;
    private TextInputLayout roommateGenderInput;
    private AutoCompleteTextView roommateGenderDropdown;
    private List<Faculty> facultyList;
    private Button signOut, updateInfo;
    private TextView deleteAccount;


    View rootView;
    FirebaseAuth mAuth;
    private Retrofit retrofit;
    private TindroomApiService tindroomApiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_profile, container, false);

        initViews();
        initListeners();
        initData();
        mAuth.getCurrentUser();

        return rootView;
    }

    // Odjava
    public void navigateToMainActivity (View view) {
        NavDirections action = MyProfileFragmentDirections.actionMyProfileFragmentToMainActivity();
        Navigation.findNavController(view).navigate(action);

    }

    private void initViews(){

        signOut = rootView.findViewById(R.id.signOut);
        deleteAccount = rootView.findViewById(R.id.deleteAccount);
        updateInfo = rootView.findViewById(R.id.save);

        nameInput = rootView.findViewById(R.id.usernameInput);
        nameEditText = rootView.findViewById(R.id.usernameEditText);

        dateOfBirthInput = rootView.findViewById(R.id.dateOfBirthInput);
        dateOfBirthEditText = rootView.findViewById(R.id.dateOfBirthEditText);

        genderInput = rootView.findViewById(R.id.genderInput);
        genderDropdown = rootView.findViewById(R.id.genderDropdown);

        facultyInput = rootView.findViewById(R.id.facultyInput);
        facultyDropdown = rootView.findViewById(R.id.facultyDropdown);

        descriptionEditText = rootView.findViewById(R.id.descriptionEditText);
        descriptionEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        descriptionEditText.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        roommateGenderInput = rootView.findViewById(R.id.roommateGenderInput);
        roommateGenderDropdown = rootView.findViewById(R.id.roommateGenderDropdown);

        setGenderMenuItems();
        setFacultyMenuItems();

    }

    private void initData(){
        // Dohvati zapise iz baze i upisi ih na profil
    }

    private void initListeners(){
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                navigateToMainActivity(rootView);
            }
        });

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteUser();
            }
        });

        updateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserInfo();
            }
        });


    }



    private void updateUserInfo(){
        //nameEditText.getText() = "zapis u bazi";

    }


    private void deleteUser(){
        mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    navigateToMainActivity(rootView);
                }
            }
        });
    }

    private void setGenderMenuItems(){

    }

    private void setFacultyMenuItems(){
        // Dohvati iz baze zapisan
        roommateGenderDropdown.setText("Zamet");

        facultyList = new ArrayList<>();

        Call<List<Faculty>> facultiesCall = tindroomApiService.getFaculties();

        facultiesCall.enqueue(new Callback<List<Faculty>>() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NonNull final Call<List<Faculty>> call, @NonNull final Response<List<Faculty>> response) {
                assert response.body() != null;
                facultyList.addAll(response.body());
                String[] items = facultyList.stream().map(Faculty::getName).toArray(String[]::new);

                ArrayAdapter<String> spinnerArrayAdapter;
                spinnerArrayAdapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        items);
                facultyDropdown.setAdapter(spinnerArrayAdapter);
            }

            @Override
            public void onFailure(@NonNull final Call<List<Faculty>> call, @NonNull final Throwable t) {
                Log.d("faculties FAILURE", t.toString());
            }
        });

    }

}