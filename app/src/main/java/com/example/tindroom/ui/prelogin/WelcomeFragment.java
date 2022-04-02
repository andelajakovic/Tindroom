package com.example.tindroom.ui.prelogin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tindroom.R;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

public class WelcomeFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    public void navigateToRegistrationFragment (View view) {
        NavDirections action = WelcomeFragmentDirections.actionWelcomeFragmentToRegistrationFragment();
        Navigation.findNavController(view).navigate(action);
    }

    public void navigateToLoginFragment (View view) {
        NavDirections action = WelcomeFragmentDirections.actionWelcomeFragmentToLoginFragment();
        Navigation.findNavController(view).navigate(action);
    }

}