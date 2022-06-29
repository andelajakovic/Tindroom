package com.example.tindroom.ui.prelogin;

import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tindroom.R;
import com.example.tindroom.data.local.SharedPreferencesStorage;
import com.example.tindroom.data.model.Faculty;
import com.example.tindroom.data.model.Neighborhood;
import com.example.tindroom.data.model.User;
import com.example.tindroom.network.RetrofitService;
import com.example.tindroom.network.TindroomApiService;
import com.example.tindroom.utils.InputValidator;
import com.example.tindroom.utils.LoadingDialogBar;
import com.example.tindroom.utils.NetworkChangeListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginFragment extends Fragment {

    private View rootView;
    private FirebaseAuth mAuth;
    private TindroomApiService tindroomApiService;

    private TextInputLayout passwordInput, emailInput;
    private TextInputEditText passwordEditText, emailEditText;
    private TextView linkToRegistrationFragment;
    private Button loginButton;
    LoadingDialogBar loadingDialogBar;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login, container, false);

        Retrofit retrofit = RetrofitService.getRetrofit();
        tindroomApiService = retrofit.create(TindroomApiService.class);
        loadingDialogBar = new LoadingDialogBar(getActivity());

        initViews();
        initListeners();

        return rootView;
    }

    private void initViews() {
        passwordEditText = rootView.findViewById(R.id.passwordEditText);
        passwordInput = rootView.findViewById(R.id.passwordInput);

        emailInput = rootView.findViewById(R.id.emailInput);
        emailEditText = rootView.findViewById(R.id.emailEditText);

        loginButton = rootView.findViewById(R.id.loginButton);
        linkToRegistrationFragment = rootView.findViewById(R.id.linkToRegistrationFragment);
    }

    private void initUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            navigateToHomeActivity(rootView);
        }
    }

    private void initListeners() {

        linkToRegistrationFragment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                navigateToRegistrationFragment(rootView);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(checkUserInput()) checkLoginForm();
            }
        });

    }

    private boolean checkUserInput() {
        InputValidator inputValidator = new InputValidator(getContext());

        boolean emailNotEmptyFlag = inputValidator.isInputEditTextFilled(emailEditText, emailInput);
        boolean passwordNotEmptyFlag = inputValidator.isInputEditTextFilled(passwordEditText, passwordInput);

        return emailNotEmptyFlag && passwordNotEmptyFlag;
    }

    private void checkLoginForm() {

        loadingDialogBar.startLoadingDialog();

        mAuth.signInWithEmailAndPassword(Objects.requireNonNull(emailEditText.getText()).toString().trim(), Objects.requireNonNull(passwordEditText.getText()).toString().trim())
             .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                 @Override
                 public void onComplete(@NonNull Task<AuthResult> task) {
                     loadingDialogBar.dismissDialog();
                     if (task.isSuccessful()) {
                         // Sign in success, update UI with the signed-in user's information
                         FirebaseUser user = mAuth.getCurrentUser();

                         Call<User> userCall = tindroomApiService.getUserById(user.getUid());
                         userCall.enqueue(new Callback<User>() {

                             @Override
                             public void onResponse(final Call<User> call, final Response<User> response) {
                                 User user = response.body();
                                 setUsersFaculty(user);
                             }

                             @Override
                             public void onFailure(final Call<User> call, final Throwable t) {

                             }
                         });

                     } else {
                         Toast.makeText(getActivity(), getResources().getString(R.string.incorrect_email_or_password), Toast.LENGTH_LONG).show();
                     }
                 }
             });
    }

    private void setUsersFaculty(User user) {
        Call<Faculty> facultyCall = tindroomApiService.getFacultyById(user.getIdFaculty());
        facultyCall.enqueue(new Callback<Faculty>() {

            @Override
            public void onResponse(final Call<Faculty> call, final Response<Faculty> response) {
                user.setFaculty(response.body());
                loadingDialogBar.dismissDialog();
                if(user.isHasApartment()) {
                    setUsersNeighborhood(user);
                } else {
                    SharedPreferencesStorage.setSessionUser(requireContext(), user);
                    navigateToHomeActivity(rootView);
                    requireActivity().finish();
                }
            }

            @Override
            public void onFailure(final Call<Faculty> call, final Throwable t) {
                loadingDialogBar.dismissDialog();
            }
        });
    }

    private void setUsersNeighborhood (User user) {
        Call<Neighborhood> neighborhoodCall = tindroomApiService.getNeighborhoodById(user.getIdNeighborhood());
        neighborhoodCall.enqueue(new Callback<Neighborhood>() {

            @Override
            public void onResponse(final Call<Neighborhood> call, final Response<Neighborhood> response) {
                loadingDialogBar.dismissDialog();
                user.setNeighborhood(response.body());
                SharedPreferencesStorage.setSessionUser(requireContext(), user);
                navigateToHomeActivity(rootView);
                requireActivity().finish();
            }

            @Override
            public void onFailure(final Call<Neighborhood> call, final Throwable t) {
                loadingDialogBar.dismissDialog();
            }
        });
    }


    public void navigateToRegistrationFragment(View view) {
        NavDirections action = LoginFragmentDirections.actionLoginFragmentToRegistrationFragment();
        Navigation.findNavController(view).navigate(action);
    }

    public void navigateToHomeActivity(View view) {
        NavDirections action = LoginFragmentDirections.actionLoginFragmentToHomeActivity();
        Navigation.findNavController(view).navigate(action);
    }


    @Override
    public void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(networkChangeListener,intentFilter);
        super.onStart();
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(networkChangeListener);
        super.onStop();
    }

}