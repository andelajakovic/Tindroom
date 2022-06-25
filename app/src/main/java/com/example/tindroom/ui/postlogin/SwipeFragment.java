package com.example.tindroom.ui.postlogin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tindroom.R;

public class SwipeFragment extends Fragment {

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_swipe, container, false);

        initViews();
        initListeners();

        return rootView;
    }

    private void initViews() {
    }

    private void initListeners() {
    }
}