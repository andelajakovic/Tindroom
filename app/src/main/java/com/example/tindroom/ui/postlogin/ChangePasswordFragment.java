package com.example.tindroom.ui.postlogin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.tindroom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ChangePasswordFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private View rootView;

    private Button changePassword;
    private TextInputLayout oldPasswordInput, newPasswordInput, againNewPasswordInput;
    private TextInputEditText oldPasswordText, newPasswordEditText, againNewPasswordEditText;

    private String newPassword, repeatNewPassword;

    private boolean passwordLengthFlag = true, newPasswordsMatchFlag = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.fragment_change_password, container, false);

        initViews();
        initListeners();

        return rootView;
    }

    public void navigateToSettingsFragment(View view) {
        NavDirections action = ChangePasswordFragmentDirections.actionChangePasswordFragmentToMyProfileFragment();
        Navigation.findNavController(view).navigate(action);
    }

    public void initViews(){
        oldPasswordText = rootView.findViewById(R.id.oldPasswordText);
        oldPasswordInput = rootView.findViewById(R.id.oldPasswordInput);

        newPasswordEditText = rootView.findViewById(R.id.newPasswordText);
        newPasswordInput = rootView.findViewById(R.id.newPasswordInput);

        againNewPasswordEditText = rootView.findViewById(R.id.againNewPasswordText);
        againNewPasswordInput = rootView.findViewById(R.id.againNewPasswordInput);

        changePassword = rootView.findViewById(R.id.changePasswordButton);
    }

    public void initListeners(){
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPasswords()){
                    Toast.makeText(getActivity(), "Password is updated", Toast.LENGTH_LONG).show();
                    navigateToSettingsFragment(view);
                }
            }
        });
    }

    private boolean checkPasswords(){
        newPassword = newPasswordEditText.getText().toString().trim();
        repeatNewPassword = againNewPasswordEditText.getText().toString().trim();

        if (newPassword.length() < 8) {
            newPasswordInput.setError(getResources().getString(R.string.password_must_have_at_least_eight_characters));
            passwordLengthFlag = false;
            return false;
        }

        if (!(newPassword.equals(repeatNewPassword))){
            againNewPasswordInput.setError(getResources().getString(R.string.passwords_do_not_match));
            newPasswordsMatchFlag = false;
            return false;
        }

        if ( passwordLengthFlag && newPasswordsMatchFlag) {
            if(currentUser != null) {
                updatePassword();
            }
        }
        return true;
    }

    private void updatePassword(){
        String email = currentUser.getEmail();
        String oldPassword = oldPasswordText.getText().toString().trim();
        AuthCredential credential = EmailAuthProvider.getCredential(email,oldPassword);

        currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    currentUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(getContext(), "Something went wrong. Please try again later", Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(getContext(), "Password updated", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(getContext(), "Authentication Failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}