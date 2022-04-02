package com.example.tindroom.ui.prelogin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tindroom.R;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

public class RegistrationFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    public void navigateToLoginFragment (View view) {
        NavDirections action = RegistrationFragmentDirections.actionRegistrationFragmentToLoginFragment();
        Navigation.findNavController(view).navigate(action);
    }

    public void navigateToAboutYouFragment (View view) {
        NavDirections action = RegistrationFragmentDirections.actionRegistrationFragmentToAboutYouFragment();
        Navigation.findNavController(view).navigate(action);
    }

}