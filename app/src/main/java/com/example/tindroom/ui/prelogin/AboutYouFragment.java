package com.example.tindroom.ui.prelogin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tindroom.R;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

public class AboutYouFragment extends Fragment {

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

    public void navigateToHomeActivity (View view) {
        NavDirections action = AboutYouFragmentDirections.actionAboutYouFragmentToHomeActivity();
        Navigation.findNavController(view).navigate(action);
    }

}