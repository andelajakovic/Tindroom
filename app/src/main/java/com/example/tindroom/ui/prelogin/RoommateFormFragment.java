package com.example.tindroom.ui.prelogin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import retrofit2.Retrofit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.tindroom.R;
import com.example.tindroom.data.model.User;
import com.example.tindroom.network.RetrofitService;
import com.example.tindroom.network.TindroomApiService;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RoommateFormFragment extends Fragment {

    private User user;

    private Retrofit retrofit;
    private TindroomApiService tindroomApiService;
    private View rootView;

    private TextInputLayout roommateGenderInput;
    private AutoCompleteTextView roommateGenderDropdown;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_roommate_form, container, false);

        retrofit = RetrofitService.getRetrofit();
        tindroomApiService = retrofit.create(TindroomApiService.class);

        initViews();
        initListeners();

        return rootView;    }

    private void initViews() {
        roommateGenderInput = rootView.findViewById(R.id.roommateGenderInput);
        roommateGenderDropdown = rootView.findViewById(R.id.roommateGenderDropdown);

        setGenderMenuItems();
    }

    private void setGenderMenuItems() {
        String[] items =  getResources().getStringArray(R.array.roommates_gender_items);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        roommateGenderDropdown.setAdapter(arrayAdapter);
    }

    private void initListeners() {
    }
}