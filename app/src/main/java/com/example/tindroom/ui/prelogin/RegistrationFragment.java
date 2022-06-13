package com.example.tindroom.ui.prelogin;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.tindroom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

public class RegistrationFragment extends Fragment {

    private View rootView;
    Button registrationConfirm;

    private TextInputLayout passwordInput, passwordAgainInput, usernameInput, emailInput;
    private TextInputEditText passwordEditText, passwordAgainEditText, usernameEditText, emailEditText;
    private String email, password, passwordAgain,  username;
    static boolean flag = false;

    private FirebaseAuth mAuth;
    //private FirebaseDatabase database;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
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

        passwordAgainEditText = rootView.findViewById(R.id.repeatPasswordEditText);
        passwordAgainInput = rootView.findViewById(R.id.repeatPasswordInput);

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

    private boolean checkRegisterForm(){
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString().trim();
        passwordAgain = passwordAgainEditText.getText().toString().trim();
        username = usernameEditText.getText().toString();

        /*if (username vec postoji u bazi){
            usernameInput.setError("Korisničko ime već postoji!");
            return false;
        }*/

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailInput.setError("Email nije u dobrom obliku");
            return false;
        }else if(!checkEmailExistsOrNot()){
            emailInput.setError("Email već postoji");
            return false;
        }

        if (passwordEditText.getText().length() < 8) {
            passwordInput.setError("Zaporka mora imati najmanje 8 znakova");
            return false;
        }

        if (!(password.equals(passwordAgain))){
            passwordAgainInput.setError("Zaporke se ne podudaraju");
            return false;
        }

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Andela ovjde cemo upisat ID
                    //FirebaseDatabase database = FirebaseDatabase.getInstance("Users");
                    //DatabaseReference myRef = database.getReference("message");
                    //myRef.setValue("Hello, World!");
                }
            }
        });

        return true;

    }
    private boolean checkEmailExistsOrNot() {
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.getResult().getSignInMethods().size() == 0) {
                    flag = true;
                } else {
                    // email existed
                    flag = false;
                }

            }
        });
        return flag;
    }
}