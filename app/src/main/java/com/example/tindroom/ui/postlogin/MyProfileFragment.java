package com.example.tindroom.ui.postlogin;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.tindroom.R;
import com.example.tindroom.data.local.SharedPreferencesStorage;
import com.example.tindroom.data.model.Faculty;
import com.example.tindroom.data.model.User;
import com.example.tindroom.network.RetrofitService;
import com.example.tindroom.network.TindroomApiService;
import com.example.tindroom.utils.ImageHandler;
import com.example.tindroom.utils.LoadingDialogBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_OK;

public class MyProfileFragment extends Fragment {

    TextView changeProfilePicture, usersName, usersFaculty;
    ImageButton editProfile, logout, safety;
    ImageView profilePicture;
    ProgressBar progressBar;

    View rootView;
    private TindroomApiService tindroomApiService;
    User sessionUser;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    LoadingDialogBar loadingDialogBar;
    private BottomSheetDialog bottomSheetDialog;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Retrofit retrofit = RetrofitService.getRetrofit();
        tindroomApiService = retrofit.create(TindroomApiService.class);
        sessionUser = SharedPreferencesStorage.getSessionUser(requireContext());
        loadingDialogBar = new LoadingDialogBar(getActivity());
        bottomSheetDialog = new BottomSheetDialog(requireContext());

        initViews();
        initListeners();
        initData();

        return rootView;
    }

    private void initViews(){
        changeProfilePicture = rootView.findViewById(R.id.changeProfilePicture);
        usersName = rootView.findViewById(R.id.name);
        usersFaculty = rootView.findViewById(R.id.faculty);
        editProfile = rootView.findViewById(R.id.editProfileButton);
        logout = rootView.findViewById(R.id.logoutButton);
        safety = rootView.findViewById(R.id.safetyButton);
        profilePicture = rootView.findViewById(R.id.profilePicture);
        progressBar = rootView.findViewById(R.id.progressBar);
    }

    private void initListeners(){
        changeProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                showBottomSheetDialog();
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                navigateToSettingsFragment(view);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                SharedPreferencesStorage.setSessionUser(requireContext(), null);
                sessionUser.setNotificationToken(null);
                updateUserToken();
                mAuth.signOut();
                navigateToMainActivity(view);
                requireActivity().finish();
            }
        });

        safety.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToChangePasswordFragment(view);
            }
        });
    }

    private void updateUserToken(){
        Call<User> userCall = tindroomApiService.updateUserById(sessionUser.getUserId(), sessionUser);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d("body", response.body().toString());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("failed", t.toString());
            }
        });
    }

    private void initData(){
        setProfilePicture();
        usersName.setText( sessionUser.getName());
        usersFaculty.setText(sessionUser.getFaculty().getName());
    }

    private void setProfilePicture() {
        progressBar.setVisibility(View.VISIBLE);
        Glide.with(rootView)
             .load(sessionUser.getImageUrl())
             .listener(new RequestListener<Drawable>() {
                 @Override
                 public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                     progressBar.setVisibility(View.GONE);
                     return false;
                 }

                 @Override
                 public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                     progressBar.setVisibility(View.GONE);
                     return false;
                 }
             })
             .error(getResources().getDrawable(R.drawable.avatar_placeholder))
             .into(profilePicture);
    }

    private void showBottomSheetDialog() {
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_layout);

        TextView upload = bottomSheetDialog.findViewById(R.id.upload);
        TextView remove = bottomSheetDialog.findViewById(R.id.remove);

        upload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {
                chooseImage();
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {
                loadingDialogBar.startLoadingDialog();
                sessionUser.setImageUrl(null);
                sessionUser.setDateOfBirth(sessionUser.getDateOfBirth().substring(0, 10));
                SharedPreferencesStorage.setSessionUser(requireContext(), sessionUser);

                updateUser();
            }
        });

        bottomSheetDialog.show();

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // Instructions on bottomSheetDialog Dismiss
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, ""), 1);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            uploadImageToFirebase(data.getData());
        }
    }

    private void uploadImageToFirebase(Uri uri) {
        loadingDialogBar.startLoadingDialog();
        StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                    @Override
                    public void onSuccess(final Uri uri) {
                        sessionUser.setImageUrl(uri.toString());
                        sessionUser.setDateOfBirth(sessionUser.getDateOfBirth().substring(0, 10));
                        SharedPreferencesStorage.setSessionUser(requireContext(), sessionUser);
                        updateUser();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onProgress(@NonNull final UploadTask.TaskSnapshot taskSnapshot) {

            }
        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull final Exception e) {

            }
        });
    }

    private String getFileExtension(final Uri uri) {
        ContentResolver contentResolver = requireContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void updateUser() {
        Call<User> userCall = tindroomApiService.updateUserById(sessionUser.getUserId(), sessionUser);

        userCall.enqueue(new Callback<User>() {

            @Override
            public void onResponse(final Call<User> call, final Response<User> response) {
                loadingDialogBar.dismissDialog();
                bottomSheetDialog.dismiss();
                /*Glide.with(rootView)
                     .asBitmap()
                     .load(sessionUser.getImageUrl())
                     .placeholder(R.drawable.avatar_placeholder)
                     .into(profilePicture);*/
                setProfilePicture();
            }

            @Override
            public void onFailure(final Call<User> call, final Throwable t) {
                loadingDialogBar.dismissDialog();
                Toast.makeText(getContext(), getResources().getString(R.string.unexpected_error_occurred), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void navigateToMainActivity (View view) {
        NavDirections action = MyProfileFragmentDirections.actionMyProfileFragmentToMainActivity();
        Navigation.findNavController(view).navigate(action);
    }

    public void navigateToSettingsFragment (View view) {
        NavDirections action = MyProfileFragmentDirections.actionMyProfileFragmentToSettingsFragment();
        Navigation.findNavController(view).navigate(action);
    }

    public void navigateToChangePasswordFragment (View view) {
        NavDirections action = MyProfileFragmentDirections.actionMyProfileFragmentToChangePasswordFragment();
        Navigation.findNavController(view).navigate(action);
    }


}