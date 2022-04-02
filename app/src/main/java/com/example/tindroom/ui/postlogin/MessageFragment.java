package com.example.tindroom.ui.postlogin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tindroom.R;

public class MessageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    public void navigateToMessageFragment (View view) {
        NavDirections action = MessageFragmentDirections.actionMessageFragmentToChatFragment();
        Navigation.findNavController(view).navigate(action);
    }

}