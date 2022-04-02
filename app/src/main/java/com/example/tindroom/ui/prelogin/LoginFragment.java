package com.example.tindroom.ui.prelogin;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tindroom.R;

public class LoginFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    public void navigateToRegistrationFragment (View view) {
        NavDirections action = LoginFragmentDirections.actionLoginFragmentToRegistrationFragment();
        Navigation.findNavController(view).navigate(action);
    }

    // Prijava
    public void navigateToHomeActivity (View view) {
        NavDirections action = LoginFragmentDirections.actionLoginFragmentToHomeActivity();
        Navigation.findNavController(view).navigate(action);
    }

}