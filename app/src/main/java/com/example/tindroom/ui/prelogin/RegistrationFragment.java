package com.example.tindroom.ui.prelogin;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.tindroom.R;
import com.example.tindroom.data.model.User;
import com.example.tindroom.network.RetrofitService;
import com.example.tindroom.network.TindroomApiService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegistrationFragment extends Fragment {

    private Retrofit retrofit;
    private TindroomApiService tindroomApiService;
    private User user;

    private View rootView;
    Button registrationConfirm;

    private TextInputLayout passwordInput, repeatPasswordInput, usernameInput, emailInput;
    private TextInputEditText passwordEditText, repeatPasswordEditText, usernameEditText, emailEditText;
    private String email, password, repeatPassword,  username;


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

        retrofit = RetrofitService.getRetrofit();
        tindroomApiService = retrofit.create(TindroomApiService.class);

        user = new User();

        initViews();
        initListeners();


        return rootView;
    }

    public void navigateToLoginFragment (View view) {
        NavDirections action = RegistrationFragmentDirections.actionRegistrationFragmentToLoginFragment();
        Navigation.findNavController(view).navigate(action);
    }

    public void navigateToAboutYouFragment (View view) {
        NavDirections action = RegistrationFragmentDirections.actionRegistrationFragmentToAboutYouFragment(user);
        Navigation.findNavController(view).navigate(action);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void initViews(){
        passwordEditText = rootView.findViewById(R.id.passwordEditText);
        passwordInput = rootView.findViewById(R.id.passwordInput);

        repeatPasswordEditText = rootView.findViewById(R.id.repeatPasswordEditText);
        repeatPasswordInput = rootView.findViewById(R.id.repeatPasswordInput);

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
                checkRegisterForm();
            }
        });
    }

    private void checkRegisterForm(){
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString().trim();
        repeatPassword = repeatPasswordEditText.getText().toString().trim();
        username = usernameEditText.getText().toString();

        /*if (username vec postoji u bazi){
            usernameInput.setError("Korisničko ime već postoji!");
            return false;
        }*/

        boolean emailPatternFlag = true, passwordLengthFlag = true, passwordsMatchFlag = true;
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailInput.setError(getResources().getString(R.string.please_enter_a_valid_email_address));
            emailPatternFlag = false;
        }
        if (passwordEditText.getText().length() < 8) {
            passwordInput.setError(getResources().getString(R.string.password_must_have_at_least_eight_characters));
            passwordLengthFlag = false;
        }
        if (!(password.equals(repeatPassword))){
            repeatPasswordInput.setError(getResources().getString(R.string.passwords_do_not_match));
            passwordsMatchFlag = false;
        }
        if (emailPatternFlag && passwordLengthFlag && passwordsMatchFlag) {
            checkEmailExistsOrNot();
        }

    }

    private void insertUserToFirebase () {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    user.setUserId(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                    user.setRegistered(false);
                    Call<User> userCall = tindroomApiService.registerUser(user);
                    userCall.enqueue(new Callback<User>() {

                        @Override
                        public void onResponse(final Call<User> call, final Response<User> response) {
                            navigateToAboutYouFragment(rootView);
                        }

                        @Override
                        public void onFailure(final Call<User> call, final Throwable t) {
                            Toast.makeText(getContext(), getResources().getString(R.string.unexpected_error_occurred), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void checkEmailExistsOrNot() {
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.getResult().getSignInMethods().size() == 0) {
                    insertUserToFirebase();
                } else {
                    // email existed
                    emailInput.setError(getResources().getString(R.string.user_with_this_email_address_already_exists));
                }
            }
        });
    }
}