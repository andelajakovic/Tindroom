package com.example.tindroom.ui.prelogin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.tindroom.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import java.util.Objects;

public class RegistrationFragment extends Fragment {

    private View rootView;
    Button registrationConfirm;

    private TextInputLayout passwordInput, passwordAgainInput, usernameInput, emailInput;
    private TextInputEditText passwordEditText, passwordAgainEditText, usernameEditText, emailEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_registration, container, false);

        initViews();
        initListeners();

        return rootView;
    }

    public void navigateToLoginFragment (View view) {
        NavDirections action = RegistrationFragmentDirections.actionRegistrationFragmentToLoginFragment();
        Navigation.findNavController(view).navigate(action);
    }

    public void navigateToAboutYouFragment (View view) {
        NavDirections action = RegistrationFragmentDirections.actionRegistrationFragmentToAboutYouFragment();
        Navigation.findNavController(view).navigate(action);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void initViews(){
        passwordEditText = rootView.findViewById(R.id.passwordEditText);
        passwordInput = rootView.findViewById(R.id.passwordInput);

        passwordAgainEditText = rootView.findViewById(R.id.passwordAgainEditText);
        passwordAgainInput = rootView.findViewById(R.id.passwordInput);

        usernameInput = rootView.findViewById(R.id.usernameInput);
        usernameEditText = rootView.findViewById(R.id.usernameEditText);

        emailInput = rootView.findViewById(R.id.emailInput);
        emailEditText = rootView.findViewById(R.id.emailEditText);

        registrationConfirm = rootView.findViewById(R.id.registrationButton);

    }

    public void initListeners(){
        registrationConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkRegisterForm()){
                    navigateToAboutYouFragment(rootView);
                }else{
                    // error
                }
            }
        });
    }

    public boolean checkRegisterForm(){

        if (passwordEditText.getText().length() < 8) {
            passwordInput.setHelperText("Zaporka mora imati najmanje 8 znakova");
            return false;
        }
        /*else if (!passwordEditText.getText().equals(passwordAgainEditText.getText())){
            passwordAgainInput.setError("Zaporke se ne podudaraju");
            return false;
        }*/

        return true;

    }
}