package com.example.tindroom.ui.prelogin;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tindroom.R;
import com.example.tindroom.data.local.SharedPreferencesStorage;
import com.example.tindroom.data.model.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

public class SplashFragment extends Fragment {

    View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_splash, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // TODO (Andrea: napraviti odgodu 1500-3000 ms)
        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SharedPreferencesStorage.getSessionUser(requireContext()) == null) {
                    navigateToLoginFragment(rootView);
                } else if (SharedPreferencesStorage.getSessionUser(requireContext()).isRegistered()) {
                    navigateToHomeActivity(rootView);
                } else {
                    navigateToAboutYouFragment(rootView);
                }
            }
        }, 2000);


    }

    public void navigateToAboutYouFragment(View view) {
        NavDirections action = SplashFragmentDirections.actionSplashFragmentToAboutYouFragment(SharedPreferencesStorage.getSessionUser(requireContext()));
        Navigation.findNavController(view).navigate(action);
    }

    public void navigateToLoginFragment (View view) {
        NavDirections action = SplashFragmentDirections.actionSplashFragmentToLoginFragment();
        Navigation.findNavController(view).navigate(action);
    }

    public void navigateToHomeActivity (View view) {
        NavDirections action = SplashFragmentDirections.actionSplashFragmentToHomeActivity();
        Navigation.findNavController(view).navigate(action);
    }

}