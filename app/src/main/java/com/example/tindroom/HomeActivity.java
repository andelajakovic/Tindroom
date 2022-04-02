package com.example.tindroom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        NavController navController = Navigation.findNavController(this, R.id.activity_home_nav_host_fragment);
        BottomNavigationView bottomNavigationView = findViewById(R.id.activity_home_bottom_navigation_view);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }
}