package com.example.tindroom.ui.prelogin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tindroom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {

    View rootView;
    private FirebaseAuth mAuth;

    private TextInputLayout passwordInput, emailInput;
    private TextInputEditText passwordEditText, emailEditText;
    private TextView toRegistration;
    private String email, password;
    private Button login;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login, container, false);
        initViews();
        initListeners();

        return rootView;
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

    private void initViews(){
        passwordEditText = rootView.findViewById(R.id.passwordEditText);
        passwordInput = rootView.findViewById(R.id.passwordInput);

        emailInput = rootView.findViewById(R.id.emailInput);
        emailEditText = rootView.findViewById(R.id.emailEditText);

        login = rootView.findViewById(R.id.loginButton);
        toRegistration = rootView.findViewById(R.id.toRegistrationFragment);
    }

    private void initUser(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            navigateToHomeActivity(rootView);
        }
    }

    private void initListeners(){

        toRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToRegistrationFragment(rootView);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLoginForm();
            }
        });
    }

    private void checkLoginForm(){
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user = mAuth.getCurrentUser();
                    navigateToHomeActivity(rootView);
                } else {
                    Toast.makeText(getActivity(), "Krivo korisničko ime ili zaporka", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}